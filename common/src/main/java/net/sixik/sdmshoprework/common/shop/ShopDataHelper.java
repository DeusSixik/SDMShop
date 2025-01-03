package net.sixik.sdmshoprework.common.shop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.network.sync.SendClearTabsS2C;

public class ShopDataHelper {

    public static void syncShopData(ServerPlayer player) {
        new SendClearTabsS2C().sendTo(player);
    }

    public static void syncShopData(MinecraftServer server) {
        new SendClearTabsS2C().sendToAll(server);
    }
}
