package net.sdm.sdmshopr.network.mainshop;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.network.SDMShopNetwork;

import java.util.Optional;
import java.util.UUID;

public class UpdateEditMode extends BaseS2CMessage {
    private final UUID playerId;
    private final boolean value;

    public UpdateEditMode(UUID playerId, boolean value){
        this.playerId = playerId;
        this.value = value;
    }

    public UpdateEditMode(FriendlyByteBuf buf){
        playerId = buf.readUUID();
        value = buf.readBoolean();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.UPDATE_EDIT_MODE;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(playerId);
        friendlyByteBuf.writeBoolean(value);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()){
            Optional<KnownClientPlayer> team = ClientTeamManagerImpl.getInstance().getKnownPlayer(playerId);

            if (team != null) {
                SDMShopR.setEditMode(team.get(), value);
            }
        }
    }
}
