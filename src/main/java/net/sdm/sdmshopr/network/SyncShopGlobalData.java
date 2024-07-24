package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.data.ClientShopData;
import net.sdm.sdmshopr.shop.limiter.AbstractLimiterData;
import net.sdm.sdmshopr.shop.limiter.ClientLimiterData;
import net.sdm.sdmshopr.shop.limiter.ServerLimiterData;

public class SyncShopGlobalData extends BaseS2CMessage {

    public CompoundTag nbt;

    public SyncShopGlobalData(CompoundTag nbt){
        this.nbt = nbt;
    }

    public SyncShopGlobalData(FriendlyByteBuf buf){
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SYNC_SHOP_GLOBAL_DATA;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()){
            ServerLimiterData limiterData = new ServerLimiterData();
            limiterData.deserializeNBT(nbt.getCompound("limiterData"));

            SDMShopRClient.clientShopData.limiterData.clear();

            for (AbstractLimiterData.LimiteEntry value : limiterData.ENTIES_MAP.values()) {
                if(value.isGlobal) {
                    SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.put(value.entryID, new ClientLimiterData.LimiteEntry(value.entryID, value.usersData.get(value.entryID)));
                } else if(value.usersData.containsKey(packetContext.getPlayer().getUUID())){
                    SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.put(value.entryID,
                            new ClientLimiterData.LimiteEntry(value.entryID, value.usersData.get(packetContext.getPlayer().getUUID())));
                }
            }

        }
    }
}
