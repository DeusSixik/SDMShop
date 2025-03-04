package net.sixik.sdmshoprework.network.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendChangesShopEntriesC2S extends BaseC2SMessage {

    private final UUID tabID;
    private final CompoundTag nbt;

    public SendChangesShopEntriesC2S(UUID tabID, CompoundTag nbt) {
        this.tabID = tabID;
        this.nbt = nbt;
    }

    public SendChangesShopEntriesC2S(FriendlyByteBuf buf) {
        this.tabID = buf.readUUID();
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_CHANGE_ENTRIES;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(tabID);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ShopBase.SERVER.getShopTab(tabID).deserializeNBT(nbt);
            ShopBase.SERVER.syncShop(packetContext.getPlayer().getServer());
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
