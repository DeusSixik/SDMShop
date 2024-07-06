package net.sdm.sdmshopr.network.mainshop;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

public class CreateShopEntry extends BaseC2SMessage {

    private final int tab;
    private final CompoundTag nbt;

    public CreateShopEntry(ShopEntry<?> entry) {
        this.tab = entry.tab.getIndex();
        this.nbt = entry.serializeNBT();
    }
    public CreateShopEntry(FriendlyByteBuf buf) {
        this.tab = buf.readInt();
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.CREATE_SHOP_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(tab);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            try {
                ShopEntry<?> entry = new ShopEntry<>(Shop.SERVER.shopTabs.get(tab));
                entry.deserializeNBT(nbt);
                entry.type = NBTUtils.getEntryType(nbt.getCompound("type"));
                Shop.SERVER.shopTabs.get(tab).shopEntryList.add(entry);
                Shop.SERVER.saveToFileWithSync();
            } catch (Exception e) {

                SDMShopR.LOGGER.error("CreateShopEntry: " + e.toString());
            }
        }
    }
}
