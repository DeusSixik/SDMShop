package net.sdm.sdmshopr.network.mainshop;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.network.SDMShopNetwork;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.ListHelper;

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


                ShopTab d1 = Shop.SERVER.shopTabs.get(tab);
                if(isUp) {
                    ListHelper.moveUp(d1.shopEntryList, entry);
                }
                else {
                    ListHelper.moveDown(d1.shopEntryList, entry);
                }

                Shop.SERVER.saveToFileWithSync();
            } catch (Exception e){
                SDMShopR.LOGGER.error(e.toString());
            }
        }
    }
}
