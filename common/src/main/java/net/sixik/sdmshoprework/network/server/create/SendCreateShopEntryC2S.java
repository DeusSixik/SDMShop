package net.sixik.sdmshoprework.network.server.create;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendCreateShopEntryC2S extends BaseC2SMessage {

    private final UUID tabID;
    private final CompoundTag nbt;

    public SendCreateShopEntryC2S(UUID tabID, CompoundTag nbt) {
        this.nbt = nbt;
        this.tabID = tabID;
    }

    public SendCreateShopEntryC2S(FriendlyByteBuf nbt) {
        this.nbt = nbt.readAnySizeNbt();
        this.tabID = nbt.readUUID();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.CREATE_SHOP_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
        friendlyByteBuf.writeUUID(tabID);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ShopTab shopTab = ShopBase.SERVER.getShopTab(tabID);
            ShopEntry shopEntry = new ShopEntry(shopTab);
            shopEntry.deserializeNBT(nbt);
            shopTab.getTabEntry().add(shopEntry);
            ShopBase.SERVER.syncShop(packetContext.getPlayer().getServer());
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
