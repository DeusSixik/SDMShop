package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendMoveEntryS2C;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendMoveEntryC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID entryFrom;
    private final UUID entryTo;
    private final MoveType moveType;

    public SendMoveEntryC2S(UUID shopId, UUID entryFrom, UUID entryTo, MoveType moveType) {
        this.shopId = shopId;
        this.entryFrom = entryFrom;
        this.entryTo = entryTo;
        this.moveType = moveType;
    }

    public SendMoveEntryC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryFrom = byteBuf.readUUID();
        this.entryTo = byteBuf.readUUID();
        this.moveType = MoveType.values()[byteBuf.readInt()];
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_MOVE_ENTRY_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(entryFrom);
        friendlyByteBuf.writeUUID(entryTo);
        friendlyByteBuf.writeInt(moveType.ordinal());
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;
        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;
        BaseShop shop = optShop.get();

        if(moveType == MoveType.Up || moveType == MoveType.Down) {
            if(!shop.moveEntry(entryFrom, moveType)) {
                SDMShop.LOGGER.error("Can't move entry {} method {}", entryFrom, moveType);
                return;
            }

        } else if(!shop.swapEntries(entryFrom, entryTo, moveType)) {
            SDMShop.LOGGER.error("Can't move entry {} to {}", entryFrom, entryTo);
            return;
        }
        ShopNetworkUtils.changeShop(shop, new SendMoveEntryS2C(shopId, entryFrom, entryTo, moveType), packetContext);
    }
}
