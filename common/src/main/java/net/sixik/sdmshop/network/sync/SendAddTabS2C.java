package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SendAddTabS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final CompoundTag nbt;

    public SendAddTabS2C(UUID shopId, CompoundTag nbt) {
        this.shopId = shopId;
        this.nbt = nbt;
    }

    public SendAddTabS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_ADD_TAB_S2C;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        @Nullable BaseShop shop = SDMShopClient.CurrentShop;
        if(shop == null || !Objects.equals(shop.getId(), shopId)) return;

        ShopTab shopTab = new ShopTab(shop);
        shopTab.deserialize(nbt);
        shop.getTabs().add(shopTab);
        shop.onChange();
    }
}
