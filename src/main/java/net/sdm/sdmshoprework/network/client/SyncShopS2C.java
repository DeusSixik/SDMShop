package net.sdm.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.network.ShopNetwork;

public class SyncShopS2C extends BaseS2CMessage {

    private final CompoundTag nbt;

    public SyncShopS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncShopS2C(FriendlyByteBuf nbt) {
        this.nbt = nbt.readAnySizeNbt();
    }


    @Override
    public MessageType getType() {
        return ShopNetwork.SYNC_SHOP;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()){
            try {
                ShopBase.CLIENT.deserializeNBT(nbt);
                AbstractShopScreen.refreshIfOpen();
            } catch (Exception e){
                SDMShopRework.printStackTrace("", e);
            }
        }
    }
}
