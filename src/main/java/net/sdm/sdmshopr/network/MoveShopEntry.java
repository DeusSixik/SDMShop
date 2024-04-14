package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;

import java.util.ArrayList;
import java.util.List;

public class MoveShopEntry extends BaseC2SMessage {
    private final int tab;
    private final int entry;
    private final boolean isUp;

    public MoveShopEntry(int tab1, int entry1, boolean isUp1){

        this.tab = tab1;
        this.entry = entry1;
        this.isUp = isUp1;
    }

    public MoveShopEntry(FriendlyByteBuf buf){
        this.tab = buf.readInt();
        this.entry = buf.readInt();
        this.isUp = buf.readBoolean();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.MOVE_SHOP_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(tab);
        friendlyByteBuf.writeInt(entry);
        friendlyByteBuf.writeBoolean(isUp);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            try {
                int entryId = entry;
                ShopTab d1 = Shop.SERVER.shopTabs.get(tab);
                int newIndex = entryId;
                if (isUp) {
                    newIndex -= 1;
                } else {
                    newIndex += 1;
                }
                if (entryId < 0 || entryId >= d1.shopEntryList.size() || newIndex < 0 || newIndex >= d1.shopEntryList.size()) {
                    SDMShopR.LOGGER.error("[MOVE] Index a broken !");
                    return;
                }

                ShopEntry<?> f1 = d1.shopEntryList.get(entryId);
                ShopEntry<?> f2 = d1.shopEntryList.get(newIndex);
                d1.shopEntryList.set(newIndex, f1);
                d1.shopEntryList.set(entryId, f2);

                Shop.SERVER.saveAndSync();
            } catch (Exception e){
                SDMShopR.LOGGER.error(e.toString());
            }
        }
    }
}
