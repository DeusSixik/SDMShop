package net.sixk.sdmshop.utils;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

public class ShopNetworkUtils {

    public static void sendShopDataS2C(ServerPlayer player, RegistryAccess registryAccess) {
        NetworkManager.sendToPlayer(player, new SendShopDataS2C(TovarList.SERVER.serializeNBT(registryAccess), TovarTab.SERVER.serializeNBT(registryAccess)));
    }

    public static void sendShopDataS2C(MinecraftServer server, RegistryAccess registryAccess) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendShopDataS2C(player, registryAccess);
        }
    }
}
