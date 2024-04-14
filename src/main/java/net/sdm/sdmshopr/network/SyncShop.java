package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.shop.Shop;
import org.antlr.v4.codegen.model.Sync;

public class SyncShop extends BaseS2CMessage {

    public CompoundTag shopData;

    public SyncShop(CompoundTag shopData){
        this.shopData = shopData;
    }

    public SyncShop(FriendlyByteBuf buf){
        this.shopData = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SYNC_SHOP;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(shopData);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()) {
            Shop.CLIENT = new Shop();
            Shop.CLIENT.deserializeNBT(shopData);
        }
    }
}
