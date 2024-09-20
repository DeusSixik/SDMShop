package net.sixik.sdmshoprework.network.server.create;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network.ShopNetwork;
import net.sixik.sdmshoprework.network.client.SyncShopS2C;

public class SendCreateShopTabC2S extends BaseC2SMessage {

    private final CompoundTag nbt;

    public SendCreateShopTabC2S(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SendCreateShopTabC2S(FriendlyByteBuf nbt) {
        this.nbt = nbt.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.CREATE_SHOP_TAB;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            ShopTab shopTab = new ShopTab(ShopBase.SERVER);
            shopTab.deserializeNBT(nbt);
            ShopBase.SERVER.getShopTabs().add(shopTab);
            new SyncShopS2C(ShopBase.SERVER.serializeNBT()).sendToAll(packetContext.getPlayer().getServer());
            ShopBase.SERVER.saveShopToFile();
        } catch (Exception e){
            SDMShopRework.printStackTrace("", e);
        }
    }
}
