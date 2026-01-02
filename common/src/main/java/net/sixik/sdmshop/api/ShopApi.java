package net.sixik.sdmshop.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.server.SendBuyEntryC2S;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;

import java.util.UUID;

public class ShopApi {

    public static ShopLimiter getLimiter() {
        return SDMShopServer.Instance().getShopLimiter();
    }

    public static void resetLimitAllForGlobal() {
        getLimiter().resetAllDataGlobal();
    }

    public static void resetAllData(final UUID id, final ShopObjectTypes types) {
        getLimiter().resetAllData(id, types);
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

    @Environment(EnvType.CLIENT)
    public static void sendBuyEntry(final ShopEntry entry, final int count) {
        new SendBuyEntryC2S(entry.getOwnerShop().getId(), entry.getId(), count).sendToServer();
    }
}
