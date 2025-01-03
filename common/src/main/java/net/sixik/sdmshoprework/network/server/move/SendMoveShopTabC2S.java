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

public class SendMoveShopTabC2S extends BaseC2SMessage {

    private final int from;
    private final int to;

    public SendMoveShopTabC2S(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public SendMoveShopTabC2S(FriendlyByteBuf buf) {
        this.from = buf.readInt();
        this.to = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_MOVE_TAB;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(from);
        friendlyByteBuf.writeInt(to);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ListHelper.swap(ShopBase.SERVER.getShopTabs(), from, to);
            ShopBase.SERVER.syncShop(packetContext.getPlayer().getServer());
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
