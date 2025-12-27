package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendRemoveTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SendRemoveTabC2S extends BaseC2SMessage {

    private final UUID shopId;
    private final UUID tabUuid;

    public SendRemoveTabC2S(BaseShop shop, ShopTab tab) {
        this(shop.getUuid(), tab.getId());
    }

    public SendRemoveTabC2S(UUID shopId, UUID tabUuid) {
        this.shopId = shopId;
        this.tabUuid = tabUuid;
    }

    public SendRemoveTabC2S(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.tabUuid = byteBuf.readUUID();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_REMOVE_TAB_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(tabUuid);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!ShopUtils.isEditMode(packetContext.getPlayer())) return;
        Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(shopId);
        if(optShop.isEmpty()) return;
        BaseShop shop = optShop.get();

        if(shop.removeShopTab(tabUuid).success() &&
           shop.removeShopEntry(s -> Objects.equals(s.getTab(), tabUuid)).success()) {

            ShopNetworkUtils.changeShop(shop, new SendRemoveTabS2C(shopId, tabUuid), packetContext);
        }

    }
}
