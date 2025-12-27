package net.sixik.sdmshop.api;

import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;

public class ShopApi {

    public static ShopLimiter getLimiter() {
        return SDMShopServer.Instance().getShopLimiter();
    }

    public static void resetLimitAllForGlobal() {
        getLimiter().resetAllDataGlobal();
    }

    public static void resetLimitAllForPlayer(final ServerPlayer player) {
        getLimiter().resetAllData(player);
    }

    public static void resetLimit(final ServerPlayer player, final ShopEntry entry) {
        getLimiter().resetEntryData(entry.getId(), player);
    }

    public static void resetLimit(final ServerPlayer player, final ShopTab entry) {
        getLimiter().resetTabData(entry.getId(), player);
    }
}
