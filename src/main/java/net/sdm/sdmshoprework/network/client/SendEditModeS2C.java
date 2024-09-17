package net.sdm.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshoprework.network.ShopNetwork;

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
    @OnlyIn(Dist.CLIENT)
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient())
            Minecraft.getInstance().player.getPersistentData().putBoolean("edit_mode", edit);
    }
}
