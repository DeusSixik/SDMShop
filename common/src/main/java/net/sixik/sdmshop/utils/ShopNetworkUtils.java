package net.sixik.sdmshop.utils;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.Objects;
import java.util.UUID;

public class ShopNetworkUtils {

    public static void changeShop(BaseShop shop, BaseS2CMessage message, NetworkManager.PacketContext context) {
        shop.onChange();
        sendToAllExcept(message, context);
        SDMShopServer.Instance().saveShop(context.getPlayer().getServer(), shop.getId());
    }

    public static void sendToAllExcept(BaseS2CMessage message, NetworkManager.PacketContext context) {
        sendToAllExcept(message, context.getPlayer().getServer(), (ServerPlayer) context.getPlayer());
    }

    public static void sendToAllExcept(BaseS2CMessage message, MinecraftServer server, UUID uuid) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if(Objects.equals(serverPlayer.getGameProfile().getId(), uuid)) continue;
            message.sendTo(serverPlayer);
        }
    }

    public static void sendToAllExcept(BaseS2CMessage message, MinecraftServer server, ServerPlayer player) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if(Objects.equals(serverPlayer, player)) continue;
            message.sendTo(serverPlayer);
        }
    }
}
