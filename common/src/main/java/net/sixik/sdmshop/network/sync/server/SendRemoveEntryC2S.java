package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendRemoveEntryS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendRemoveEntryC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID entryUuid;

    public SendRemoveEntryC2S(BaseShop shop, ShopEntry entry) {
        this(shop.getId(), entry.getId());
    }

    public SendRemoveEntryC2S(UUID shopId, UUID entryUuid) {
        this.shopId = shopId;
        this.entryUuid = entryUuid;
    }

    public SendRemoveEntryC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryUuid = byteBuf.readUUID();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_REMOVE_ENTRY_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(entryUuid);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;

        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;

        BaseShop shop = optShop.get();
        if(!shop.removeEntry(entryUuid).success()) {
            SDMShop.LOGGER.warn("Can't remove shop entry {} he not exists!", entryUuid);
            return;
        }

        ShopNetworkUtils.changeShop(shop, new SendRemoveEntryS2C(shopId, entryUuid), packetContext);
    }
}
