package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;

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
                int newIndex = tab;
                if (isUp) {
                    newIndex -= 1;
                } else {
                    newIndex += 1;
                }
                if (tab < 0 || tab >= Shop.CLIENT.shopTabs.size() || newIndex < 0 || newIndex >= Shop.CLIENT.shopTabs.size()) {
                    SDMShopR.LOGGER.error("[MOVE] Index a broken !");
                    return;
                }


                ShopTab f1 = Shop.CLIENT.shopTabs.get(tab);
                ShopTab f2 = Shop.CLIENT.shopTabs.get(newIndex);
                Shop.CLIENT.shopTabs.set(newIndex, f1);
                Shop.CLIENT.shopTabs.set(tab, f2);

                Shop.SERVER.saveAndSync();
            } catch (Exception e){
                SDMShopR.LOGGER.error(e.toString());
            }
        }
    }
}
