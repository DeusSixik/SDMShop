package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeEntryS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendChangeEntryC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID entryUuid;
    private final CompoundTag nbt;

    public SendChangeEntryC2S(BaseShop shop, ShopEntry entry) {
        this(shop.getUuid(), entry.getUuid(), entry.serialize());
    }

    public SendChangeEntryC2S(UUID shopId, UUID entryUuid, CompoundTag nbt) {
        this.shopId = shopId;
        this.entryUuid = entryUuid;
        this.nbt = nbt;
    }

    public SendChangeEntryC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.entryUuid = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_CHANGE_ENTRY_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(entryUuid);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;
        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;
        BaseShop shop = optShop.get();

        shop.findShopEntryByUUID(entryUuid).ifPresent(entry -> {
            entry.deserialize(nbt);
            ShopNetworkUtils.changeShop(shop, new SendChangeEntryS2C(shopId, entryUuid, nbt), packetContext);
        });
    }
}
