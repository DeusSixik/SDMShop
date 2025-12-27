package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendAddTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Optional;
import java.util.UUID;

public class SendAddTabC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final CompoundTag nbt;

    public SendAddTabC2S(BaseShop shop, ShopTab tab) {
        this(shop.getId(), tab.serialize());
    }

    public SendAddTabC2S(UUID shopId, CompoundTag nbt) {
        this.shopId = shopId;
        this.nbt = nbt;
    }

    public SendAddTabC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_ADD_TAB_C2S;
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
        if(optShop.isEmpty()) {
            SDMShop.LOGGER.error("Shop {} not found!", shopId);
            return;
        }
        BaseShop shop = optShop.get();

        ShopTab tab = new ShopTab(shop);
        tab.deserialize(nbt);
        shop.getTabsList().add(tab);
        ShopNetworkUtils.changeShop(shop, new SendAddTabS2C(shopId, nbt), packetContext);
    }
}
