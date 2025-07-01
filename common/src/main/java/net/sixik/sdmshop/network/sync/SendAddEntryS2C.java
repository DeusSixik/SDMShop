package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SendAddEntryS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final CompoundTag nbt;

    public SendAddEntryS2C(UUID shopId, CompoundTag nbt) {
        this.shopId = shopId;
        this.nbt = nbt;
    }

    public SendAddEntryS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_ADD_ENTRY_S2C;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        @Nullable BaseShop shop = SDMShopClient.CurrentShop;
        if(shop == null || !Objects.equals(shop.getUuid(), shopId)) return;

        ShopEntry entry = new ShopEntry(shop);
        entry.deserialize(nbt);
        shop.getShopEntries().add(entry);
        shop.onChange();
    }
}
