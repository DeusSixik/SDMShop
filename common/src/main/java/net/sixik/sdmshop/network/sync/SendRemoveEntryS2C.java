package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SendRemoveEntryS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final UUID entryUuid;

    public SendRemoveEntryS2C(UUID shopId, UUID entryUuid) {
        this.shopId = shopId;
        this.entryUuid = entryUuid;
    }

    public SendRemoveEntryS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryUuid = byteBuf.readUUID();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_REMOVE_ENTRY_S2C;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(entryUuid);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        @Nullable BaseShop shop = SDMShopClient.CurrentShop;
        if(shop == null || !Objects.equals(shop.getUuid(), shopId)) return;

        if(!shop.removeShopEntry(entryUuid).success()) {
            SDMShop.LOGGER.warn("Can't remove shop entry {} he not exists!", entryUuid);
        } else {
            if(SDMShopClient.userData.getEntries().remove(entryUuid)) {
                SDMShopClient.userData.save();
            }

        }

        shop.onChange();
    }
}
