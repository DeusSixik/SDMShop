package net.sixik.sdmshoprework.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendGetTabsC2S extends BaseC2SMessage {

    public SendGetTabsC2S() {

    }

    public SendGetTabsC2S(FriendlyByteBuf buf) {

    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_GET_TABS;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        for (CompoundTag serializeTab : ShopBase.SERVER.serializeTabs()) {
            new SendShopTabS2C(serializeTab).sendTo((ServerPlayer) context.getPlayer());
        }
    }
}
