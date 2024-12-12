package net.sixik.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdm_economy.api.ICustomData;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendEditModeS2C extends BaseS2CMessage {

    public boolean edit;

    public SendEditModeS2C(boolean edit) {
        this.edit = edit;
    }

    public SendEditModeS2C(FriendlyByteBuf buf){
        this.edit = buf.readBoolean();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_EDIT_MODE;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(edit);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        ((ICustomData) Minecraft.getInstance().player).sdm$getCustomData().putBoolean("edit_mode", edit);
    }
}
