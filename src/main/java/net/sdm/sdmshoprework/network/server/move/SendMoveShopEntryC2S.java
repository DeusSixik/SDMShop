package net.sdm.sdmshoprework.network.server.move;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.common.shop.ShopTab;
import net.sdm.sdmshoprework.common.utils.ListHelper;
import net.sdm.sdmshoprework.network.ShopNetwork;
import net.sdm.sdmshoprework.network.client.SyncShopS2C;

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
            new SyncShopS2C(ShopBase.SERVER.serializeNBT()).sendToAll(packetContext.getPlayer().getServer());
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
