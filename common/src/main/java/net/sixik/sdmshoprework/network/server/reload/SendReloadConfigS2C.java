package net.sixik.sdmshoprework.network.server.reload;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendReloadConfigS2C extends BaseS2CMessage {

    public SendReloadConfigS2C() {}
    public SendReloadConfigS2C(FriendlyByteBuf buf) {}

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_RELOAD_CONFIG;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Config.reload();
        packetContext.getPlayer().sendSystemMessage(Component.literal("Reload Complete"));
    }
}
