package net.sixik.sdmshoprework.network.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.economy.EconomyManager;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendGetMoneyC2S extends BaseC2SMessage {
    public SendGetMoneyC2S() {}

    public SendGetMoneyC2S(FriendlyByteBuf buf) {
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_GET_MONEY;
    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        EconomyManager.economy.sync().accept(context.getPlayer());
    }
}
