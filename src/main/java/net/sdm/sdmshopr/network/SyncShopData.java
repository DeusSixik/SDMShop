package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SyncShopData extends BaseS2CMessage {
    public CompoundTag nbt;
    public SyncShopData(CompoundTag nbt){
        this.nbt = nbt;
    }

    public SyncShopData(FriendlyByteBuf buf){
        nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SYNC_SHOP_DATA;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()){
            Minecraft.getInstance().player.getPersistentData().put("sdmshop", nbt);
        }
    }
}
