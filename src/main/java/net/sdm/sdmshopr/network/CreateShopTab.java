package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.tab.ShopTab;

public class CreateShopTab extends BaseC2SMessage {

    private final CompoundTag nbt;

    public CreateShopTab(CompoundTag nbt) {
        this.nbt = nbt;
    }
    public CreateShopTab(FriendlyByteBuf buf) {
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.CREATE_SHOP_TAB;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            ShopTab tab = new ShopTab(Shop.SERVER);
            tab.deserializeNBT(nbt);
            Shop.SERVER.shopTabs.add(tab);
            Shop.SERVER.saveAndSync();
        }
    }
}
