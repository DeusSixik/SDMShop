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

public class UpdateMoney extends BaseS2CMessage {
    private final UUID playerId;
    private final long money;

    public UpdateMoney(UUID id, long m) {
        playerId = id;
        money = m;
    }

    public UpdateMoney(FriendlyByteBuf buf) {
        playerId = buf.readUUID();
        money = buf.readVarLong();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.UPDATE_MONEY;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeVarLong(money);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Optional<KnownClientPlayer> team = ClientTeamManagerImpl.getInstance().getKnownPlayer(playerId);

        if (team != null) {
            SDMShopR.setMoney(team.get(), money);
        }
    }
}
