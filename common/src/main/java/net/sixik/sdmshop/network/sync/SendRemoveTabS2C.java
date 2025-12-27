package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SendRemoveTabS2C extends BaseS2CMessage {

    private final UUID shopId;
    private final UUID tabUuid;

    public SendRemoveTabS2C(UUID shopId, UUID tabUuid) {
        this.shopId = shopId;
        this.tabUuid = tabUuid;
    }

    public SendRemoveTabS2C(FriendlyByteBuf byteBuf) {
        this.shopId = byteBuf.readUUID();
        this.tabUuid = byteBuf.readUUID();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_REMOVE_TAB_S2C;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(shopId);
        friendlyByteBuf.writeUUID(tabUuid);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        @Nullable BaseShop shop = SDMShopClient.CurrentShop;
        if(shop == null || !Objects.equals(shop.getId(), shopId)) return;

        if(!shop.removeTab(tabUuid).success()) {
            SDMShop.LOGGER.error("Can't remove shop tab {}", tabUuid);
            return;
        }

        if(!shop.removeEntry(s -> Objects.equals(s.getTab(), tabUuid), entry -> {
            SDMShopClient.userData.getEntries().remove(entry.getId());
        }).success()) {
            SDMShop.LOGGER.error("Can't remove shop tab entries {}", tabUuid);
        } else {
            SDMShopClient.userData.save();
            shop.onChange();
        }
    }
}
