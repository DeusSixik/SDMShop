package net.sixik.sdmshop.network.ASK;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.old_api.network.AbstractASKRequest;
import net.sixik.sdmshop.network.SDMShopNetwork;

public class SyncShopASK extends AbstractASKRequest {

    public SyncShopASK(Void empty) {
        super(empty);
    }

    @Override
    public void onServerTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext) {

    }

    @Override
    public void onClientTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext) {

    }

    @Override
    public String getId() {
        return SDMShopNetwork.SYNC_SHOP_REQUEST;
    }
}
