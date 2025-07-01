package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.server.SDMShopServer;

public class SendLimiterS2C extends BaseS2CMessage {

    private final CompoundTag nbt;

    public SendLimiterS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SendLimiterS2C(FriendlyByteBuf byteBuf) {
        this.nbt = byteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SEND_LIMITER;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        SDMShopClient.shopLimiter.deserializeClient(nbt);
    }
}
