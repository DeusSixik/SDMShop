package net.sixik.sdmshoprework.network.server.misc;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendOpenShopScreenS2C extends BaseS2CMessage {

    public SendOpenShopScreenS2C() {

    }
    public SendOpenShopScreenS2C(FriendlyByteBuf buf) {

    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_OPEN_SHOP;
    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        SDMShopClient.openGui(true);
    }
}
