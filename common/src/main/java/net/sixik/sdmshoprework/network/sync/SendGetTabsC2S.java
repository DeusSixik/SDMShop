package net.sixik.sdmshoprework.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.SDMSerializeParam;
import net.sixik.sdmshoprework.api.ShopHandler;
import net.sixik.sdmshoprework.api.ShopSerializerHandler;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.Optional;
import java.util.UUID;

public class SendGetTabsC2S extends BaseC2SMessage {

    private final String tabID;

    public SendGetTabsC2S() {
        this.tabID = "null";
    }

    public SendGetTabsC2S(String tabID) {
        this.tabID = tabID;
    }

    public SendGetTabsC2S(FriendlyByteBuf buf) {
        this.tabID = buf.readUtf();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_GET_TABS;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(tabID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {

        if(tabID.equals("null")) {
            for (ShopTab shopTab : ShopBase.SERVER.getShopTabs()) {
                new SendShopTabS2C(ShopSerializerHandler.serializeShopTab(shopTab, SDMSerializeParam.SERIALIZE_WITHOUT_ENTRIES))
                        .sendTo((ServerPlayer) context.getPlayer());
            }

            SDMShopRework.LOGGER.debug("Send shopTabs: {}", ShopBase.SERVER.getShopTabs().size());
            return;
        }


        UUID uuid = UUID.fromString(tabID);

        Optional<ShopTab> tab = ShopHandler.getShopTab(uuid, false);
        tab.ifPresent(shopTab -> {
            new SendShopTabS2C(ShopSerializerHandler.serializeShopTab(shopTab, SDMSerializeParam.SERIALIZE_WITHOUT_ENTRIES))
                    .sendTo((ServerPlayer) context.getPlayer());

            SDMShopRework.LOGGER.debug("Send shopTab: {}", shopTab.shopTabUUID);
        });
    }
}
