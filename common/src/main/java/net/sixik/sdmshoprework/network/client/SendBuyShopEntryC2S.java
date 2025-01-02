package net.sixik.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.events.ShopEvents;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.common.data.limiter.LimiterData;
import net.sixik.sdmshoprework.common.integration.KubeJS.KubeJSHelper;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.network.ShopNetwork;
import org.jetbrains.annotations.Debug;

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
       AbstractShopEntry entry = ShopBase.SERVER.getShopTab(tabUUID)
                .getShopEntry(entryUUID);

       if(entry instanceof ShopEntry shopEntry)
           entry = shopEntry;

        if(entry.limit != 0){
            Map<UUID, Integer> BuyData = LimiterData.SERVER.PLAYER_DATA.getOrDefault(packetContext.getPlayer().getGameProfile().getId(), new HashMap<>());
            int counBuy = BuyData.getOrDefault(entryUUID, 0) ;
            if( counBuy > entry.limit) return;
            BuyData.put(entryUUID, counBuy + 1 * count);
            LimiterData.SERVER.PLAYER_DATA.put(packetContext.getPlayer().getGameProfile().getId(), BuyData);
            new SendEntryLimitS2C(LimiterData.SERVER.serializeClient(packetContext.getPlayer().getGameProfile().getId())).sendTo((ServerPlayer) packetContext.getPlayer());
            LimiterData.SERVER.save(packetContext.getPlayer().getServer());
        }

       if(entry.isSell) {
           try {
               KubeJSHelper.postEvent(packetContext.getPlayer(), entry, count, KubeJSHelper.EventType.SELL);

               entry.getEntryType().sell(packetContext.getPlayer(), count, entry);
           } catch (Exception e) {
               SDMShopRework.printStackTrace("", e);
           }
       } else {
           try {
               KubeJSHelper.postEvent(packetContext.getPlayer(), entry, count, KubeJSHelper.EventType.BUY);

               entry.getEntryType().buy(packetContext.getPlayer(), count, entry);
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
