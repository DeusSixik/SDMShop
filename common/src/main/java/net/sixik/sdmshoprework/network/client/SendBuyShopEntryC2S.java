package net.sixik.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.platform.Platform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.common.data.LimiterData;
import net.sixik.sdmshoprework.common.integration.KubeJS.KubeJSHelper;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendBuyShopEntryC2S extends BaseC2SMessage {

    private final UUID tabUUID;
    private final UUID entryUUID;
    private final int count;

    public SendBuyShopEntryC2S(UUID tabUUID, UUID entryUUID, int count) {
        this.tabUUID = tabUUID;
        this.entryUUID = entryUUID;
        this.count = count;
    }

    public SendBuyShopEntryC2S(FriendlyByteBuf buf) {
        this.tabUUID = buf.readUUID();
        this.entryUUID = buf.readUUID();
        this.count = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_BUY_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(tabUUID);
        friendlyByteBuf.writeUUID(entryUUID);
        friendlyByteBuf.writeInt(count);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        AbstractShopTab shopTab = ShopBase.SERVER.getShopTab(tabUUID);
        AbstractShopEntry entry = shopTab.getShopEntry(entryUUID);

        if(!entry.getEntryType().canExecute(packetContext.getPlayer(), entry.isSell, count, entry)) {
            SDMShopRework.LOGGER.warn("Player {} tried to buy entry {} from tab {} which is not allowed", packetContext.getPlayer().getGameProfile().getName(), entry.entryUUID, shopTab.shopTabUUID);
            return;
        }

        /////////////////////////////////////////////////////
        //              Shop Tab limit logic               //
        /////////////////////////////////////////////////////
        if(shopTab.limit != 0) {
            Map<UUID, Integer> buyDataMap = LimiterData.SERVER.PLAYER_TAB_DATA.getOrDefault(shopTab.globalLimit ? LimiterData.defaul_ : packetContext.getPlayer().getGameProfile().getId(), new HashMap<>());
            int counBuy = buyDataMap.getOrDefault(tabUUID, 0);
            if (counBuy > shopTab.limit) return;

            buyDataMap.put(tabUUID, counBuy + 1 * count);

            LimiterData.SERVER.PLAYER_TAB_DATA.put(shopTab.globalLimit ? LimiterData.defaul_ : packetContext.getPlayer().getGameProfile().getId(), buyDataMap);
            new SendEntryLimitS2C(LimiterData.SERVER.serializeClient(packetContext.getPlayer().getGameProfile().getId())).sendTo((ServerPlayer) packetContext.getPlayer());
            LimiterData.SERVER.save(packetContext.getPlayer().getServer());

            if(shopTab.globalLimit) {
                for (ServerPlayer player : packetContext.getPlayer().getServer().getPlayerList().getPlayers()) {
                    new SendEntryLimitS2C(LimiterData.SERVER.serializeClient(player.getGameProfile().getId())).sendTo(player);
                }
            }
        }

        /////////////////////////////////////////////////////
        //              Shop Entry limit logic             //
        /////////////////////////////////////////////////////
        if(entry.limit != 0 ){
           Map<UUID, Integer> buyDataMap = LimiterData.SERVER.PLAYER_ENTRY_DATA.getOrDefault(entry.globalLimit ? LimiterData.defaul_ : packetContext.getPlayer().getGameProfile().getId(), new HashMap<>());

           int counBuy = buyDataMap.getOrDefault(entryUUID, 0);
           if (counBuy > entry.limit) return;

           buyDataMap.put(entryUUID, counBuy + 1 * count);

           LimiterData.SERVER.PLAYER_ENTRY_DATA.put(entry.globalLimit ? LimiterData.defaul_ : packetContext.getPlayer().getGameProfile().getId(), buyDataMap);
           new SendEntryLimitS2C(LimiterData.SERVER.serializeClient(packetContext.getPlayer().getGameProfile().getId())).sendTo((ServerPlayer) packetContext.getPlayer());
           LimiterData.SERVER.save(packetContext.getPlayer().getServer());

           if(entry.globalLimit) {
               for (ServerPlayer player : packetContext.getPlayer().getServer().getPlayerList().getPlayers()) {
                   new SendEntryLimitS2C(LimiterData.SERVER.serializeClient(player.getGameProfile().getId())).sendTo(player);
               }
           }
       }

       long defaultPrice = entry.entryPrice;
       long defaultCount = entry.entryCount;

       if(entry.isSell) {
           try {
               if(Platform.isModLoaded("kubejs")) KubeJSHelper.postEvent(packetContext.getPlayer(), entry, count, KubeJSHelper.EventType.SELL);

               entry.getEntryType().sell(packetContext.getPlayer(), count, entry);

               if(defaultPrice != entry.entryPrice || defaultCount != entry.entryCount) {
                   ShopBase.SERVER.syncShop(packetContext.getPlayer().getServer());
               }
           } catch (Exception e) {
               SDMShopRework.printStackTrace("", e);
           }
       } else {
           try {
               if(Platform.isModLoaded("kubejs")) KubeJSHelper.postEvent(packetContext.getPlayer(), entry, count, KubeJSHelper.EventType.BUY);

               entry.getEntryType().buy(packetContext.getPlayer(), count, entry);

               if(defaultPrice != entry.entryPrice || defaultCount != entry.entryCount) {
                   ShopBase.SERVER.syncShop(packetContext.getPlayer().getServer());
               }
           } catch (Exception e) {
               SDMShopRework.printStackTrace("", e);
           }
       }

       if(Config.SEND_NOTIFY.get()) {
           entry.getEntryType().sendNotifiedMessage(packetContext.getPlayer());
       }

        packetContext.getPlayer().playNotifySound(SDMShopRework.BUY_SOUND, SoundSource.VOICE, 1.0F, 1.0F);
    }
}
