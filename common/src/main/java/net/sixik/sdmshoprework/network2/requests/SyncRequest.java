package net.sixik.sdmshoprework.network2.requests;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network2.sync.client.SendAddTabS2C;

import java.util.List;

public class SyncRequest {

    public static void server(NetworkManager.PacketContext context, List<String> arg) {
        for (ShopTab shopTab : ShopBase.SERVER.getShopTabs()) {

            new SendAddTabS2C(shopTab.serializeNBT()).sendTo((ServerPlayer) context.getPlayer());
        }
    }

    public static void client(NetworkManager.PacketContext context, List<String> arg) {

    }
}
