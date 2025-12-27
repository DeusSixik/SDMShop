package net.sixik.sdmshop.network.ASK.ASK_base;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.old_api.network.AbstractASKRequest;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.registers.ShopContentRegister;

import java.util.Optional;
import java.util.function.Function;

public class ShopDataSyncASKS2C extends BaseS2CMessage {

    private final String id;
    private final CompoundTag nbt;

    public ShopDataSyncASKS2C(String id, CompoundTag nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public ShopDataSyncASKS2C(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readUtf();
        this.nbt = byteBuf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.ASK_TO_CLIENT;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(id);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Optional<Function<Void, AbstractASKRequest>> opt = ShopContentRegister.getRequest(id);
        if(opt.isEmpty()) return;
        opt.get().apply(null).onClientTakeRequest(nbt, packetContext);
    }
}
