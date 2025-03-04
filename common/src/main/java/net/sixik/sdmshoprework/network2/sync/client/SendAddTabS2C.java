package net.sixik.sdmshoprework.network2.sync.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.ShopHandler;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network2.SDMRequests;
import net.sixik.sdmshoprework.network2.sync.SendRequestC2S;

import java.util.List;
import java.util.Optional;

public class SendAddTabS2C extends BaseS2CMessage {

    private final CompoundTag tabNBT;

    public SendAddTabS2C(CompoundTag tabNBT) {
        this.tabNBT = tabNBT;
    }

    public SendAddTabS2C(FriendlyByteBuf buf) {
        this.tabNBT = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(tabNBT);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Optional<ShopTab> shopTab = ShopHandler.createShopTab(tabNBT, true);

        if(shopTab.isEmpty()) return;

        ShopTab tab = shopTab.get();

        if(ShopBase.CLIENT.getShopTabs().removeIf(s -> s.shopTabUUID.equals(tab.shopTabUUID))) {
            SDMShopRework.LOGGER.info("[CLIENT] Deleted old shop tab {} and create new!", tab.shopTabUUID);
        }

        try {
            ShopBase.CLIENT.getShopTabs().add(tab);
            new SendRequestC2S(SDMRequests.SEND_ENTRIES, List.of(tab.shopTabUUID.toString())).sendToServer();
        } catch (Exception e) {
            SDMShopRework.printStackTrace("[CLIENT] Failed when add tab on client", e);
        }
    }
}
