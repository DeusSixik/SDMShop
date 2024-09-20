package net.sixik.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network.ShopNetwork;

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
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ShopBase.CLIENT.deserializeNBT(nbt);
            AbstractShopScreen.refreshIfOpen();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }

    }
}
