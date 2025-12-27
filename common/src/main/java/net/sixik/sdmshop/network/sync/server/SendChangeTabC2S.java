package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendChangeTabC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID tabId;
    private final CompoundTag nbt;

    public SendChangeTabC2S(UUID shopId, UUID tabId, CompoundTag nbt) {
        this.shopId = shopId;
        this.tabId = tabId;
        this.nbt = nbt;
    }

    public SendChangeTabC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.tabId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_CHANGE_TAB_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(tabId);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;
        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;
        BaseShop shop = optShop.get();

        shop.getTabOptional(tabId).ifPresent(shopTab -> {
            shopTab.deserialize(nbt);
            ShopNetworkUtils.changeShop(shop, new SendChangeTabS2C(shopId, tabId, nbt), packetContext);
        });
    }
}
