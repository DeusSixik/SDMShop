package net.sixik.sdmshoprework.network2.sync.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.shop.ShopBase;

import java.util.UUID;

public class SendDeleteEntryS2C extends BaseS2CMessage {

    private final UUID entryID;

    public SendDeleteEntryS2C(UUID uuid) {
        this.entryID = uuid;
    }

    public SendDeleteEntryS2C(FriendlyByteBuf buf) {
        this.entryID = buf.readUUID();
    }


    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(entryID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if(!ShopBase.CLIENT.deleteEntry(entryID)) {
            SDMShopRework.LOGGER.warn("[CLIENT] Couldn't delete entry: {} on CLIENT", entryID);
        }
    }
}
