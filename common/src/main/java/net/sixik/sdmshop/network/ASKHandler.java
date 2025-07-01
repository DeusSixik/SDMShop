package net.sixik.sdmshop.network;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.network.ASK.ASK_base.ShopDataSyncASKC2S;
import net.sixik.sdmshop.network.ASK.ASK_base.ShopDataSyncASKS2C;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ASKHandler {

    public static final long WAIT_TIME = 1L;
    public static final long TIME_OUT = 800L;

    private static volatile ASKHandler instance;
    public static ASKHandler getInstance() {
        if (instance == null) {
            synchronized (ASKHandler.class) {
                if (instance == null) {
                    throw new IllegalStateException("ACKHandler not initialized");
                }
            }
        }
        return instance;
    }

    protected Thread executor;
    protected Map<UUID, Request> requests = new ConcurrentHashMap<>();
    protected MinecraftServer server;

    public ASKHandler(MinecraftServer server) {
        this.server = server;
        this.executor = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    tick();
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ACK Thread");

        LifecycleEvent.SERVER_STOPPED.register(s -> executor.interrupt());

        executor.start();
        instance = this;
    }

    public void addRequests(Player player, Data... nbt) {
        addRequests(player.getGameProfile().getId(), nbt);
    }

    public void addRequests(UUID player, Data... nbt) {
        Request request = requests.computeIfAbsent(player, k -> new Request(new ConcurrentLinkedQueue<>()));
        request.runnables().addAll(Arrays.asList(nbt));
    }

    public Optional<Request> getNextRequest(Player player) {
        return getNextRequest(player.getGameProfile().getId());
    }

    public Optional<Request> getNextRequest(UUID player) {
        return Optional.ofNullable(requests.get(player));
    }

    public void tick() {
        for (Iterator<Map.Entry<UUID, Request>> iterator = requests.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<UUID, Request> entry = iterator.next();
            UUID playerId = entry.getKey();
            Request request = entry.getValue();

            if (!request.waitRequest) {
                Data value = request.runnables.poll();
                if (value == null) continue;

                Optional<ServerPlayer> opt = getPlayer(playerId);
                if (opt.isEmpty()) {
                    iterator.remove();
                    continue;
                }

                opt.ifPresent(serverPlayer -> server.execute(() -> {
                    request.startTime(System.currentTimeMillis());
                    request.waitRequest(true);
                    new ShopDataSyncASKS2C(value.id, value.data).sendTo(serverPlayer);
                }));
            } else {
                if((System.currentTimeMillis() - request.startTime) >= TIME_OUT)
                    request.waitRequest(false);
            }
        }

        if(SDMShopConstants.ASK_DEBUG_MODE)
            SDMShop.LOGGER.info("Requests handler tick");
    }

    protected Optional<ServerPlayer> getPlayer(UUID uuid) {
        return server.getPlayerList().getPlayers().stream()
                .filter(s -> s.getGameProfile().getId().equals(uuid))
                .findFirst();
    }

    public static final class Request {
        private final ConcurrentLinkedQueue<Data> runnables;
        public volatile boolean waitRequest = false;
        public long startTime;

        public Request(ConcurrentLinkedQueue<Data> runnables) {
            this.runnables = runnables;
        }

        public Request waitRequest(boolean value) {
            waitRequest = value;
            return this;
        }

        public Request startTime(long time) {
            this.startTime = time;
            return this;
        }

        public ConcurrentLinkedQueue<Data> runnables() {
            return runnables;
        }
    }

    public record Data(String id, CompoundTag data) {
    }

    public static void sendToServer(Data data) {
        new ShopDataSyncASKC2S(data.id, data.data).sendToServer();
    }

    public static void sendToClient(ServerPlayer player, Data data) {
        getInstance().addRequests(player, data);
    }
}
