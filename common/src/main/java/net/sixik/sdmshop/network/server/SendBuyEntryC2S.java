package net.sixik.sdmshop.network.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.exceptions.TabNotFoundException;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendBuyEntryC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID entryId;
    private final int count;

    public SendBuyEntryC2S(UUID shopId, UUID entryId, int count) {
        this.shopId = shopId;
        this.entryId = entryId;
        this.count = count;
    }

    public SendBuyEntryC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryId = byteBuf.readUUID();
        this.count = byteBuf.readInt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_BUY_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(entryId);
        friendlyByteBuf.writeInt(count);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {

        ShopDebugUtils.log("Get buy packet from: {}", packetContext.getPlayer().getName());

        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) {
            SDMShop.LOGGER.error("Shop not found!");
            return;
        }

        BaseShop shop = optShop.get();
        ShopLimiter limiter = SDMShopServer.Instance().getShopLimiter();
        shop.findShopEntryByUUID(entryId).ifPresent(entry -> {
            ShopTab shopTab = shop.findTabByEntry(entry).orElse(null);

            if(shopTab == null) {
                SDMShop.LOGGER.error("Tab not found!");
                return;
            }

            int d1 = shopTab.getObjectLimit();
            int d2 = entry.getObjectLimit();

            int tabEntry = shopTab.getObjectLimitLeft(packetContext.getPlayer());
            int entryValue = entry.getObjectLimitLeft(packetContext.getPlayer());

            ShopDebugUtils.log("Tab Left: {}/{}", tabEntry, d1);
            ShopDebugUtils.log("Entry Left: {}/{}", entryValue, d2);

            if(tabEntry == 0) {
                SDMShop.LOGGER.error("Tab limit reached!");
                return;
            }

            if(entryValue == 0) {
                SDMShop.LOGGER.error("Entry limit reached!");
                return;
            }

            ShopLimiterData limitData = ShopUtils.getShopLimit(shopTab, entry, packetContext.getPlayer());
            int limitValue = ShopUtils.getMaxEntryOfferSize(entry, packetContext.getPlayer(), limitData.value() != 0 ? limitData.value() : -1);

            int currentCount = Math.min(limitValue, count);

            if(currentCount <= 0) {
                SDMShop.LOGGER.error("Can't buy because count <= 0");
                return;
            }

            if(!entry.getEntryType().canExecute(packetContext.getPlayer(), entry, currentCount)) {
                SDMShop.LOGGER.error("Can't execute");
                return;
            }

            boolean result = false;

            if(entry.getType().isSell())
                    result = entry.onSell(packetContext.getPlayer(), currentCount);
            else    result = entry.onBuy(packetContext.getPlayer(), currentCount);

            if(!result) {
                SDMShop.LOGGER.error("Error when try buy or sell");
                return;
            }

            if(shopTab.getLimiterType().isPlayer()) limiter.addTabData(shopTab.getUuid(), packetContext.getPlayer().getGameProfile().getId(), currentCount);
            else                                  limiter.addTabData(shopTab.getUuid(), currentCount);

            if(entry.getLimiterType().isPlayer()) limiter.addEntryData(entryId, packetContext.getPlayer().getGameProfile().getId(), currentCount);
            else                                  limiter.addEntryData(entryId, currentCount);

            if(ShopConfig.SEND_NOTIFY.get())
                entry.getEntryType().sendNotifiedMessage(packetContext.getPlayer(), entry, count);

            for (ServerPlayer player : packetContext.getPlayer().getServer().getPlayerList().getPlayers()) {
                new SendLimiterS2C(limiter.serializeClient(player.getGameProfile().getId())).sendTo(player);
            }

        });

    }
}
