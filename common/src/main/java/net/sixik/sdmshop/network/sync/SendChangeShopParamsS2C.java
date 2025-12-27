package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;

import java.util.Objects;
import java.util.UUID;

public class SendChangeShopParamsS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final CompoundTag nbt;

    public SendChangeShopParamsS2C(BaseShop shop) {
        this(shop.getId(), shop.getParams().serialize());
    }

    public SendChangeShopParamsS2C(UUID shopId, CompoundTag nbt) {
        this.shopId = shopId;
        this.nbt = nbt;
    }

    public SendChangeShopParamsS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.CHANGE_PARAMS_S2C;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        ShopDebugUtils.log("SendChangeShopParamsS2C ACCEPT");

        BaseShop shop = ShopUtilsClient.getShop();
        if(shop == null || !Objects.equals(shop.getId(), shopId)) {
            ShopDebugUtils.error("Can't sync shop params! {}, {}", shop != null ? shop.getId() : "null", shopId);
            return;
        }

        ShopDebugUtils.log("ShopParams: {}", nbt);


        shop.getParams().deserialize(nbt);
        shop.onChange();

    }
}
