package net.sdm.sdmshopr.network.mainshop;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.network.SDMShopNetwork;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.utils.ListHelper;

public class MoveShopTab extends BaseC2SMessage {

    private final int tab;
    private final boolean isUp;

    public MoveShopTab(int tab1, boolean isUp1){

        this.tab = tab1;
        this.isUp = isUp1;
    }

    public MoveShopTab(FriendlyByteBuf buf){
        this.tab = buf.readInt();
        this.isUp = buf.readBoolean();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.MOVE_SHOP_TAB;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(tab);
        friendlyByteBuf.writeBoolean(isUp);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(SDMShopR.isEditMode(packetContext.getPlayer())){
            try {
                if(isUp) {
                    ListHelper.moveUp(Shop.SERVER.shopTabs, tab);
                }
                else {
                    ListHelper.moveDown(Shop.SERVER.shopTabs, tab);
                }

                Shop.SERVER.saveToFileWithSync();
            } catch (Exception e){
                SDMShopR.LOGGER.error(e.toString());
            }
        }
    }
}
