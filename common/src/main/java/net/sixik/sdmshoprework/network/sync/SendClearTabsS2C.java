package net.sixik.sdmshoprework.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendClearTabsS2C extends BaseS2CMessage {

    private static final UUID DEFAULT = UUID.fromString("e1967fdb-cb22-4f83-b437-ba1a12f771bb");

    private final UUID tabID;

    public SendClearTabsS2C() {
        this.tabID = DEFAULT;
    }

    public SendClearTabsS2C(UUID tabID) {
        this.tabID = tabID;
    }

    public SendClearTabsS2C(FriendlyByteBuf buf) {
        this.tabID = buf.readUUID();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_CLEAR_TAB;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(tabID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if(tabID.equals(DEFAULT)) {
            ShopBase.CLIENT.getShopTabs().clear();
            new SendGetTabsC2S().sendToServer();
            return;
        }

       ShopTab tab = ShopBase.CLIENT.getShopTab(tabID);
       if(tab == null) return;

       tab.getTabEntry().clear();

       new SendGetTabsC2S(tab.shopTabUUID.toString()).sendToServer();
    }
}
