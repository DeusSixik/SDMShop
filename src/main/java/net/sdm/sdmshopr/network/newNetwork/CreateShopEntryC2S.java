package net.sdm.sdmshopr.network.newNetwork;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;
import net.sixik.sdmcore.impl.utils.serializer.DataNetworkHelper;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerHelper;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;

public class CreateShopEntryC2S extends BaseC2SMessage{

    public IData data;

    public CreateShopEntryC2S(ShopEntry<?> entry){
        KeyData d1 = new KeyData();
        d1.put("entry", entry.serializeNBT());
        d1.put("tab", entry.tab.getIndex());
        this.data = d1;
    }

    public CreateShopEntryC2S(IData data){
        this.data = data;
    }

    public CreateShopEntryC2S(FriendlyByteBuf buf){
        this.data = DataNetworkHelper.readData(buf);
    }


    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        DataNetworkHelper.writeData(friendlyByteBuf, data);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            try {
                int tab = data.asKeyMap().getData("tab").asInt();
                CompoundTag nbt = (CompoundTag) SDMSerializerHelper.dataToNBT(data.asKeyMap().getData("entry"));

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
