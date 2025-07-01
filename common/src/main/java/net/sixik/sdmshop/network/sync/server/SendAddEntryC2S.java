package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendAddEntryS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendAddEntryC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final CompoundTag nbt;

    public SendAddEntryC2S(BaseShop shop, ShopEntry entry) {
        this(shop.getUuid(), entry.serialize());
    }

    public SendAddEntryC2S(UUID shopId, CompoundTag nbt) {
        this.shopId = shopId;
        this.nbt = nbt;
    }

    public SendAddEntryC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_ADD_ENTRY_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;
        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;
        BaseShop shop = optShop.get();

        ShopEntry entry = new ShopEntry(shop);
        entry.deserialize(nbt);
        shop.getShopEntries().add(entry);
        ShopNetworkUtils.changeShop(shop, new SendAddEntryS2C(shopId, nbt), packetContext);
    }
}
