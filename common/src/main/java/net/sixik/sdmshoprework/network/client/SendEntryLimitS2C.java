package net.sixik.sdmshoprework.network.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.common.data.LimiterData;
import net.sixik.sdmshoprework.network.ShopNetwork;

public class SendEntryLimitS2C extends BaseS2CMessage {

    private final CompoundTag nbt;

    public SendEntryLimitS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SendEntryLimitS2C(FriendlyByteBuf nbt) {
        this.nbt = nbt.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_ENTRY_LIMIT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {

        LimiterData.CLIENT.deserializeClient(nbt);

    }
}
