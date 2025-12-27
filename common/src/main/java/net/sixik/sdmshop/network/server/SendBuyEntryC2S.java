package net.sixik.sdmshop.network.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.ShopEvents;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;

import java.util.UUID;

public class SendBuyEntryC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID entryId;
    private final int requestedCount;

    public SendBuyEntryC2S(UUID shopId, UUID entryId, int count) {
        this.shopId = shopId;
        this.entryId = entryId;
        this.requestedCount = count;
    }

    public SendBuyEntryC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryId = byteBuf.readUUID();
        this.requestedCount = byteBuf.readInt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_BUY_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(entryId);
        friendlyByteBuf.writeInt(requestedCount);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        final ServerPlayer player = (ServerPlayer) context.getPlayer();

        final SDMShopServer shopServer = SDMShopServer.InstanceOptional().orElse(null);
        if (shopServer == null) return;

        final BaseShop shop = shopServer.getShop(shopId).orElse(null);
        if (shop == null) {
            SDMShop.LOGGER.warn("Player {} tried to buy from non-existent shop {}", player.getName().getString(), shopId);
            return;
        }

        final ShopEntry entry = shop.getEntryOptional(entryId).orElse(null);
        if (entry == null) return;

        final ShopTab tab = shop.getTabOptional(entry).orElse(null);
        if (tab == null) return;

        context.queue(() -> processPurchase(player, shop, tab, entry));
    }

    private void processPurchase(
            final ServerPlayer player,
            final BaseShop shop,
            final ShopTab tab,
            final ShopEntry entry
    ) {
        final ShopLimiter limiter = SDMShopServer.Instance().getShopLimiter();

        /*
            Calculation of the ACTUAL available quantity
            We don't trust the client. We count it again on the server.
         */
        final int limitTab = tab.getObjectLimitLeft(player);   // Returns Integer.MAX_VALUE if unlimited
        final int limitEntry = entry.getObjectLimitLeft(player);

        /*
            If the limit is reached (0), we interrupt
         */
        if ((tab.isLimiterActive() && limitTab <= 0) || (entry.isLimiterActive() && limitEntry <= 0)) {

            /*
                TODO: You can send the "Error: Limit reached" package to update the GUI.
             */
            return;
        }

        /*
            We calculate how much we can actually buy
            Math.min protects against attempts to buy more than the limit
         */
        int safeCount = requestedCount;
        if (tab.isLimiterActive()) safeCount = Math.min(safeCount, limitTab);
        if (entry.isLimiterActive()) safeCount = Math.min(safeCount, limitEntry);

        if (safeCount <= 0) return;

        /*
            Verification of conditions (money, permissions, etc.)
         */
        if (!entry.getEntryType().canExecute(player, entry, safeCount)) {
            return;
        }

        /*
            TRANSACTION (Purchase/Sale)
         */
        final boolean success = entry.getType().isSell()
                ? entry.onSell(player, safeCount)
                : entry.onBuy(player, safeCount);

        if (!success) {
            SDMShop.LOGGER.error("Transaction failed for player {}", player.getName().getString());
            return;
        }

        if(entry.getType().isSell()) ShopEvents.ENTRY_SELL_EVENT.invoker().handle(shop, entry, tab, player);
        else ShopEvents.ENTRY_BUY_EVENT.invoker().handle(shop, entry, tab, player);

        /*
            UPDATING DATA (Only after a successful transaction!)
         */
        updateLimiterData(limiter, tab, entry, player, safeCount);

        if (ShopConfig.SEND_NOTIFY.get()) {
            entry.getEntryType().sendNotifiedMessage(player, entry, safeCount);
        }

        broadcastUpdates(limiter, tab, entry, player);
    }

    private void updateLimiterData(
           final ShopLimiter limiter,
           final ShopTab tab,
           final ShopEntry entry,
           final ServerPlayer player,
           final int count
    ) {
        final UUID playerId = player.getGameProfile().getId();

        /*
            Update tabs
         */
        if (tab.isLimiterActive()) {
            if (tab.getLimiterType().isPlayer()) {
                limiter.addTabData(tab.getId(), playerId, count);
            } else {
                limiter.addTabData(tab.getId(), count); // Global
            }
        }

        /*
            Update entries
         */
        if (entry.isLimiterActive()) {
            if (entry.getLimiterType().isPlayer()) {
                limiter.addOrSetEntryData(entry.getId(), playerId, count);
            } else {
                limiter.addOrSetEntryData(entry.getId(), count); // Global
            }
        }
    }

    private void broadcastUpdates(
           final ShopLimiter limiter,
           final ShopTab tab,
           final ShopEntry entry,
           final ServerPlayer buyer
    ) {
        final boolean isGlobalUpdate = (tab.isLimiterActive() && tab.getLimiterType().isGlobal()) ||
                (entry.isLimiterActive() && entry.getLimiterType().isGlobal());

        if (isGlobalUpdate) {

            /*
                Option A: Global Limit -> Helmet for EVERYONE, as the figure has changed for everyone
             */
            for (ServerPlayer p : buyer.getServer().getPlayerList().getPlayers()) {

                /*
                    IMPORTANT: serializeClient generates a package for a SPECIFIC player
                 */
                new SendLimiterS2C(limiter.serializeClient(p)).sendTo(p);
            }
        } else {

            /*
                Option B: Personal Limit -> Helmet for buyer ONLY
             */
            new SendLimiterS2C(limiter.serializeClient(buyer)).sendTo(buyer);
        }
    }
}
