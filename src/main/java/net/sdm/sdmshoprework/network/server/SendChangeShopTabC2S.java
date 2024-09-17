package net.sdm.sdmshoprework.network.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendChangeShopTabC2S extends BaseC2SMessage {

    public final UUID tab;
    public final CompoundTag nbt;
    public SendChangeShopTabC2S(UUID tab, CompoundTag nbt){
        this.tab = tab;
        this.nbt = nbt;
    }

    public SendChangeShopTabC2S(FriendlyByteBuf buf){
        this.tab = buf.readUUID();
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_CHANGE_TAB;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(tab);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ShopBase.SERVER.getShopTab(tab).deserializeNBT(nbt);
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
