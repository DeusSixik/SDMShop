package net.sdm.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendBuyShopEntryC2S extends BaseC2SMessage {

    private final UUID tabUUID;
    private final UUID entryUUID;
    private final int count;

    public SendBuyShopEntryC2S(UUID tabUUID, UUID entryUUID, int count) {
        this.tabUUID = tabUUID;
        this.entryUUID = entryUUID;
        this.count = count;
    }

    public SendBuyShopEntryC2S(FriendlyByteBuf buf) {
        this.tabUUID = buf.readUUID();
        this.entryUUID = buf.readUUID();
        this.count = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_BUY_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(tabUUID);
        friendlyByteBuf.writeUUID(entryUUID);
        friendlyByteBuf.writeInt(count);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
       AbstractShopEntry entry = ShopBase.SERVER.getShopTab(tabUUID)
                .getShopEntry(entryUUID);

       if(entry.isSell) {
           try {
               entry.getEntryType().sell(packetContext.getPlayer(), count, entry);
           } catch (Exception e) {
               SDMShopRework.printStackTrace("", e);
           }
       } else {
           try {
               entry.getEntryType().buy(packetContext.getPlayer(), count, entry);
           } catch (Exception e) {
               SDMShopRework.printStackTrace("", e);
           }
       }
    }
}
