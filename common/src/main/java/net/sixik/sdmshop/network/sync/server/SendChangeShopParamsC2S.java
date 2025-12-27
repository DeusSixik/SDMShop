package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeShopParamsS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopNetworkUtils;

import java.util.UUID;

public class SendChangeShopParamsC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final CompoundTag nbt;

    public SendChangeShopParamsC2S(BaseShop shop) {
        this(shop.getId(), shop.getShopParams().serialize());
    }

    public SendChangeShopParamsC2S(UUID shopId, CompoundTag nbt) {
        this.shopId = shopId;
        this.nbt = nbt;
    }

    public SendChangeShopParamsC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.CHANGE_PARAMS_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        ShopDebugUtils.log("SendChangeShopParamsC2S ACCEPT");

        SDMShopServer.InstanceOptional().flatMap(sdmShopServer -> sdmShopServer.getShop(shopId)).ifPresent(shop -> {
            ShopDebugUtils.log("ShopParams: {}", nbt);

            shop.getShopParams().deserialize(nbt);
            ShopNetworkUtils.changeShop(shop, new SendChangeShopParamsS2C(shopId, nbt), packetContext);
        });
    }
}
