package net.sixik.sdmshop.old_api.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.network.ASKHandler;

import java.util.function.Supplier;

public abstract class AbstractASKRequest {

    public AbstractASKRequest(Void empty) {}

    public abstract void onServerTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext);

    @Environment(EnvType.CLIENT)
    public abstract void onClientTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext);

    public abstract String getId();

    public void sendTo(ServerPlayer player, CompoundTag data) {
        ASKHandler.sendToClient(player, new ASKHandler.Data(getId(), data));
    }

    public void sendTo(ServerPlayer player, CompoundTag... data) {
        for (CompoundTag datum : data) {
            ASKHandler.sendToClient(player, new ASKHandler.Data(getId(), datum));
        }
    }

    public void sendToAll(MinecraftServer server, CompoundTag data) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(player, data);
        }
    }

    public void sendToAll(MinecraftServer server, CompoundTag... data) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(player, data);
        }
    }

    public void sendToServer(CompoundTag data) {
        ASKHandler.sendToServer(new ASKHandler.Data(getId(), data));
    }

    public void executePerSend(Supplier<CompoundTag> supplier) {
        sendToServer(supplier.get());
    }

    public void executePerSend(Supplier<CompoundTag[]> supplier, ServerPlayer player) {
        sendTo(player, supplier.get());
    }

    public void executePerSend(Supplier<CompoundTag[]> supplier, MinecraftServer server) {
        sendToAll(server, supplier.get());
    }
}
