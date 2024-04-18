package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.tab.ShopTab;

public class EditShopTab extends BaseC2SMessage {
    private final int tab;
    private final CompoundTag nbt;

    public EditShopTab(ShopTab t, boolean delete) {
        tab = t.getIndex();
        nbt = delete ? null : t.serializeSettings();
    }

    public EditShopTab(FriendlyByteBuf buf) {
        tab = buf.readVarInt();
        nbt = buf.readNbt();
    }


    @Override
    public MessageType getType() {
        return SDMShopNetwork.EDIT_SHOP_TAB;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(tab);
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            ShopTab tab = Shop.SERVER.shopTabs.get(this.tab);

            if(nbt == null)
                Shop.SERVER.shopTabs.remove(this.tab);
            else
                tab.deserializeNBT(nbt);
            Shop.SERVER.saveAndSync();
        }
    }
}
