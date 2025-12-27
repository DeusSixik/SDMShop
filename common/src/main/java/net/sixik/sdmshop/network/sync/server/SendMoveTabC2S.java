package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendMoveTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendMoveTabC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID tabFrom;
    private final UUID tabTo;
    private final MoveType moveType;

    public SendMoveTabC2S(UUID shopId, UUID tabFrom, UUID tabTo, MoveType moveType) {
        this.shopId = shopId;
        this.tabFrom = tabFrom;
        this.tabTo = tabTo;
        this.moveType = moveType;
    }

    public SendMoveTabC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.tabFrom = byteBuf.readUUID();
        this.tabTo = byteBuf.readUUID();
        this.moveType = MoveType.values()[byteBuf.readInt()];
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_MOVE_TAB_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(tabFrom);
        friendlyByteBuf.writeUUID(tabTo);
        friendlyByteBuf.writeInt(moveType.ordinal());
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;
        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;
        BaseShop shop = optShop.get();

        if(moveType == MoveType.Up || moveType == MoveType.Down) {
            if(!shop.moveTab(tabFrom, moveType)) {
                SDMShop.LOGGER.error("Can't move tab {} method {}", tabFrom, moveType);
                return;
            }

        } else if(!shop.swapTabs(tabFrom, tabTo, moveType)) {
            SDMShop.LOGGER.error("Can't move tab {} to {}", tabFrom, tabTo);
            return;
        }

        ShopNetworkUtils.changeShop(shop, new SendMoveTabS2C(shopId, tabFrom, tabTo, moveType), packetContext);
    }
}
