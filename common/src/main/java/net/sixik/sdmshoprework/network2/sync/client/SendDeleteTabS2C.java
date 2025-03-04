package net.sixik.sdmshoprework.network2.sync.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.shop.ShopBase;

import java.util.UUID;

public class SendDeleteTabS2C extends BaseS2CMessage {
    private final UUID tabID;

    public SendDeleteTabS2C(UUID uuid) {
        this.tabID = uuid;
    }

    public SendDeleteTabS2C(FriendlyByteBuf buf) {
        this.tabID = buf.readUUID();
    }

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(tabID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if(!ShopBase.CLIENT.deleteTab(tabID)) {
            SDMShopRework.LOGGER.warn("[CLIENT] Couldn't delete tab: {} on CLIENT", tabID);
        }
    }
}
