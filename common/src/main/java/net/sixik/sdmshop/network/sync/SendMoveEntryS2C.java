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

public class SendMoveEntryS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final UUID entryFrom;
    private final UUID entryTo;
    private final MoveType moveType;

    public SendMoveEntryS2C(UUID shopId, UUID entryFrom, UUID entryTo, MoveType moveType) {
        this.shopId = shopId;
        this.entryFrom = entryFrom;
        this.entryTo = entryTo;
        this.moveType = moveType;
    }

    public SendMoveEntryS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryFrom = byteBuf.readUUID();
        this.entryTo = byteBuf.readUUID();
        this.moveType = MoveType.values()[byteBuf.readInt()];
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_MOVE_ENTRY_S2C;
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
        @Nullable BaseShop shop = SDMShopClient.CurrentShop;
        if(shop == null || !Objects.equals(shop.getId(), shopId)) return;

        if(moveType == MoveType.Up || moveType == MoveType.Down) {
            if(!shop.moveEntry(entryFrom, moveType)) {
                SDMShop.LOGGER.error("Can't move entry {} method {}", entryFrom, moveType);
                return;
            }

        } else if(!shop.swapEntries(entryFrom, entryTo, moveType)) {
            SDMShop.LOGGER.error("Can't move entry {} to {}", entryFrom, entryTo);
            return;
        }

        shop.onChange();
    }
}
