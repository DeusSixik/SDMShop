package net.sixik.sdmshoprework.network2.sync.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network2.SDMRequests;
import net.sixik.sdmshoprework.network2.sync.SendRequestC2S;

public class SendClearShopS2C extends BaseS2CMessage {

    public SendClearShopS2C() {

    }

    public SendClearShopS2C(FriendlyByteBuf buf) {

    }

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ShopBase.CLIENT.getShopTabs().clear();

        new SendRequestC2S(SDMRequests.SYNC).sendToServer();
    }
}
