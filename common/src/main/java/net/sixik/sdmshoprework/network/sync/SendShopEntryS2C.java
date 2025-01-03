package net.sixik.sdmshoprework.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendShopEntryS2C extends BaseS2CMessage {

    private final CompoundTag nbt;
    private final UUID tabID;

    public SendShopEntryS2C(AbstractShopEntry entry) {
        this.nbt = entry.serializeNBT();
        this.tabID = entry.getShopTab().shopTabUUID;
    }

    public SendShopEntryS2C(FriendlyByteBuf bug) {
        this.nbt = bug.readAnySizeNbt();
        this.tabID = bug.readUUID();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_SHOP_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
        buf.writeUUID(tabID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        AbstractShopEntry entry = new ShopEntry(null);
        entry.deserializeNBT(nbt);

        AbstractShopEntry d = ShopBase.CLIENT.getShopTab(tabID).getShopEntry(entry.entryUUID);
        if(d != null) {
            d.deserializeNBT(nbt);
        }
        else {
            ShopBase.CLIENT.getShopTab(tabID).createShopEntry(entry.serializeNBT());
        }
    }
}
