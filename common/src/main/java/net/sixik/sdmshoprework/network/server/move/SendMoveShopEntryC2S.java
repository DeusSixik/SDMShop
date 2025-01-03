package net.sixik.sdmshoprework.network.server.move;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.common.utils.ListHelper;
import net.sixik.sdmshoprework.network.ShopNetwork;
import net.sixik.sdmshoprework.network.client.SyncShopS2C;

import java.util.UUID;

public class SendMoveShopEntryC2S extends BaseC2SMessage {

    private final UUID tabID;
    private final int from;
    private final int to;

    public SendMoveShopEntryC2S(UUID tabID, int from, int to) {
        this.tabID = tabID;
        this.from = from;
        this.to = to;
    }

    public SendMoveShopEntryC2S(FriendlyByteBuf buf) {
        this.tabID = buf.readUUID();
        this.from = buf.readInt();
        this.to = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_MOVE_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(tabID);
        friendlyByteBuf.writeInt(from);
        friendlyByteBuf.writeInt(to);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ShopTab shopTab = ShopBase.SERVER.getShopTab(tabID);
            ListHelper.swap(shopTab.getTabEntry(), from, to);
            ShopBase.SERVER.syncShop(packetContext.getPlayer().getServer());
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
