package net.sixik.sdmshoprework.network2.requests;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.api.ShopHandler;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network2.sync.client.SendAddEntryS2C;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SendEntryRequest {

    public static void server(NetworkManager.PacketContext context, List<String> arg) {
        if(arg.isEmpty())
            throw new IllegalArgumentException("Not enough arguments!");

        UUID tabID = UUID.fromString(arg.get(0));

        Optional<ShopTab> shopTabOptional = ShopHandler.getShopTab(tabID, false);

        if(shopTabOptional.isEmpty())
            throw new IllegalArgumentException("Invalid shop tab ID!");

        ShopTab shopTab = shopTabOptional.get();

        for (AbstractShopEntry entry : shopTab.getTabEntry()) {
            new SendAddEntryS2C(shopTab.shopTabUUID, entry.serializeNBT()).sendTo((ServerPlayer) context.getPlayer());
        }


    }

    public static void client(NetworkManager.PacketContext context, List<String> arg) {

    }
}
