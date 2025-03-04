package net.sixik.sdmshoprework.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.api.SDMSerializeParam;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.UUID;

public class SendShopTabS2C extends BaseS2CMessage {

    private final CompoundTag nbt;

    public SendShopTabS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SendShopTabS2C(FriendlyByteBuf bug) {
        this.nbt = bug.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_SHOP_TAB;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        new SendGetEntriesC2S(ShopBase.CLIENT.createShopTab(nbt, SDMSerializeParam.SERIALIZE_WITHOUT_ENTRIES).shopTabUUID).sendToServer();
        AbstractShopScreen.refreshIfOpen();
    }
}
