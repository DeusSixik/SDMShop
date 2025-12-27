package net.sixik.sdmshop.network.ASK.ASK_base;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.old_api.network.AbstractASKRequest;
import net.sixik.sdmshop.network.ASKHandler;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.registers.ShopContentRegister;

import java.util.Optional;
import java.util.function.Function;

public class ShopDataSyncASKC2S extends BaseC2SMessage {

    private final String id;
    private final CompoundTag nbt;

    public ShopDataSyncASKC2S(String id, CompoundTag nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public ShopDataSyncASKC2S(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readUtf();
        this.nbt = byteBuf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.ASK_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(id);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Optional<Function<Void, AbstractASKRequest>> opt = ShopContentRegister.getRequest(id);
        if(opt.isEmpty()) {
            SDMShop.LOGGER.error("Request is null");
            return;
        }
        opt.get().apply(null).onServerTakeRequest(nbt, packetContext);
        ASKHandler.getInstance().getNextRequest(packetContext.getPlayer()).ifPresent(s -> s.waitRequest(false));
    }
}
