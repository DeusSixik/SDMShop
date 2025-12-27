package net.sixik.sdmshop.network.async;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class AsyncBridge {
    public static final ResourceLocation CHANNEL = new ResourceLocation(SDMShop.MODID, "async_bridge");

    private static final Map<Long, CompletableFuture<FriendlyByteBuf>> PENDING = new ConcurrentHashMap<>();
    private static final Map<String, Function<FriendlyByteBuf, FriendlyByteBuf>> HANDLERS = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GEN = new AtomicLong();
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static void initServer() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHANNEL, AsyncBridge::onPacket);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, AsyncBridge::onPacket);
    }


    /**
     * Client -> Server: Sends a request and waits for a response.
     *
     * @param subject Unique handler ID (for example, "OpenShop")
     * @param writer  Request data recording function
     */
    public static CompletableFuture<FriendlyByteBuf> askServer(
            final String subject,
            final Function<FriendlyByteBuf, FriendlyByteBuf> writer
    ) {
        return sendInternal(subject, writer, buf -> NetworkManager.sendToServer(CHANNEL, buf));
    }

    /**
     * Server -> Client: Request to a specific player.
     */
    public static CompletableFuture<FriendlyByteBuf> askPlayer(
            final ServerPlayer player,
            final String subject,
            final Function<FriendlyByteBuf, FriendlyByteBuf> writer) {
        return sendInternal(subject, writer, buf -> NetworkManager.sendToPlayer(player, CHANNEL, buf));
    }

    /**
     * Registration of request processing logic (on the recipient's side).
     *
     * @param subject   Unique handler ID (for example, "OpenShop")
     * @param processor Function: takes input buf, returns buf with response (or null if void)
     */
    public static void registerHandler(
            final String subject,
            final Function<FriendlyByteBuf, FriendlyByteBuf> processor
    ) {
        HANDLERS.put(subject, processor);
    }

    public static void completeExternal(
            final long id,
            final FriendlyByteBuf fullData
    ) {
        CompletableFuture<FriendlyByteBuf> future = PENDING.remove(id);
        if (future != null) {
            future.complete(fullData);
        }
    }

    private static CompletableFuture<FriendlyByteBuf> sendInternal(
            final String subject,
            final Function<FriendlyByteBuf, FriendlyByteBuf> writer,
            final java.util.function.Consumer<FriendlyByteBuf> sender) {
        long reqId = ID_GEN.incrementAndGet();
        CompletableFuture<FriendlyByteBuf> future = new CompletableFuture<>();

        PENDING.put(reqId, future);

        /*
            Safety: we delete frozen requests after 5 seconds
         */
        SCHEDULER.schedule(() -> {
            if (PENDING.remove(reqId) != null) {
                future.completeExceptionally(new TimeoutException("Packet timed out: " + subject));
            }
        }, 5, TimeUnit.SECONDS);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeLong(reqId);
        buf.writeBoolean(true); // true = this is a REQUEST
        buf.writeUtf(subject);
        writer.apply(buf);

        sender.accept(buf);
        return future;
    }

    private static void onPacket(
            final FriendlyByteBuf buf,
            final NetworkManager.PacketContext context
    ) {
        final long id = buf.readLong();
        final boolean isRequest = buf.readBoolean();

        if (isRequest) {
            final String subject = buf.readUtf();
            final Function<FriendlyByteBuf, FriendlyByteBuf> handler = HANDLERS.get(subject);

            if (handler != null) {

                /*
                    Make a copy of the incoming data
                 */
                final FriendlyByteBuf inputCopy = new FriendlyByteBuf(buf.copy());

                context.queue(() -> {
                    try {

                        /*
                            Execute logic (may return null here)
                         */
                        FriendlyByteBuf responsePayload = handler.apply(inputCopy);

                        /*
                            If the response is HUGE (and not null) -> helmet via BlobTransfer
                         */
                        if (responsePayload != null && responsePayload.readableBytes() > 2_000_000) {
                            if (context.getPlayer() instanceof ServerPlayer sp) {
                                BlobTransfer.sendToPlayer(sp, id, responsePayload);
                            } else {
                                BlobTransfer.sendToServer(id, responsePayload);
                            }
                        }

                        /*
                            If the response is small or NULL -> regular packet helmet
                         */
                        else {
                            FriendlyByteBuf reply = new FriendlyByteBuf(Unpooled.buffer());
                            reply.writeLong(id);
                            reply.writeBoolean(false); // This is the answer

                            /*
                                We only write data if it exists.
                             */
                            if (responsePayload != null) {
                                reply.writeBytes(responsePayload);
                            }

                            if (context.getPlayer() instanceof ServerPlayer sp) {
                                NetworkManager.sendToPlayer(sp, CHANNEL, reply);
                            } else {
                                NetworkManager.sendToServer(CHANNEL, reply);
                            }
                        }
                    } catch (Exception e) {
                        SDMShop.LOGGER.error(e.getMessage(), e);
                    } finally {
                        inputCopy.release();
                        /*
                            If responsePayload was created but not sent (e.g., exception),
                            it should also be released, but Netty usually handles GC
                            if it is a heap buffer.
                         */
                    }
                });
            }
        } else {
            /*
                Response reception logic
             */
            CompletableFuture<FriendlyByteBuf> future = PENDING.remove(id);
            if (future != null) {
                FriendlyByteBuf responseCopy = new FriendlyByteBuf(buf.copy());
                future.complete(responseCopy);
            }
        }
    }
}
