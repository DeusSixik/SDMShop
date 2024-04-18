package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;

public class EditShopEntry extends BaseC2SMessage {

    private final int tab;
    private final int id;
    private final CompoundTag nbt;

    public EditShopEntry(ShopEntry<?> e, boolean delete) {
        tab = e.tab.getIndex();
        id = e.getIndex();
        nbt = delete ? null : e.serializeNBT();
    }

    public EditShopEntry(FriendlyByteBuf buf) {
        tab = buf.readVarInt();
        id = buf.readVarInt();
        nbt = buf.readNbt();
    }


    @Override
    public MessageType getType() {
        return SDMShopNetwork.EDIT_SHOP_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(tab);
        buf.writeVarInt(id);
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            ShopTab t = Shop.SERVER.shopTabs.get(tab);

            if (nbt == null)
                t.shopEntryList.remove(id);
            else
                t.shopEntryList.get(id).deserializeNBT(nbt);

            Shop.SERVER.saveAndSync();
        }
    }
}
