package net.sdm.sdmshopr.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;

public class ReloadClientData extends BaseS2CMessage {

    public ReloadClientData(){

    }

    public ReloadClientData(FriendlyByteBuf buf){

    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.RELOAD_CLIENT;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()){
            SDMShopR.ClientModEvents.parse();
        }
    }
}
