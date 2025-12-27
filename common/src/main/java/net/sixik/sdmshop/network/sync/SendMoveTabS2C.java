package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.shop.BaseShop;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SendMoveTabS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final UUID tabFrom;
    private final UUID tabTo;
    private final MoveType moveType;

    public SendMoveTabS2C(UUID shopId, UUID tabFrom, UUID tabTo, MoveType moveType) {
        this.shopId = shopId;
        this.tabFrom = tabFrom;
        this.tabTo = tabTo;
        this.moveType = moveType;
    }

    public SendMoveTabS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.tabFrom = byteBuf.readUUID();
        this.tabTo = byteBuf.readUUID();
        this.moveType = MoveType.values()[byteBuf.readInt()];
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_MOVE_TAB_S2C;
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
        @Nullable BaseShop shop = SDMShopClient.CurrentShop;
        if(shop == null || !Objects.equals(shop.getId(), shopId)) return;

        if(moveType == MoveType.Up || moveType == MoveType.Down) {
            if(!shop.moveTab(tabFrom, moveType)) {
                SDMShop.LOGGER.error("Can't move tab {} method {}", tabFrom, moveType);
                return;
            }

        } else if(!shop.swapTabs(tabFrom, tabTo, moveType)) {
            SDMShop.LOGGER.error("Can't move tab {} to {}", tabFrom, tabTo);
            return;
        }

        shop.onChange();
    }
}
