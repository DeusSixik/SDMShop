package net.sixik.sdmshoprework.network.server.misc;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.common.config.ConfigFile;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendConfigS2C extends BaseS2CMessage {

    public CompoundTag nbt;

    public SendConfigS2C(){
        nbt = new CompoundTag();
        nbt.putBoolean("disableKeyBind", ConfigFile.SERVER.disableKeyBind);
    }

    public SendConfigS2C(FriendlyByteBuf buf){
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_CONFIG;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
//        ConfigFile.disableButton = nbt.getBoolean("disableButton");
        ConfigFile.CLIENT.disableKeyBind = nbt.getBoolean("disableKeyBind");
    }
}
