package net.sixik.sdmshop.network.async;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class AsyncBridge {
    public static final ResourceLocation CHANNEL = new ResourceLocation("my_mod", "async_bridge");

    private static final Map<Long, CompletableFuture<FriendlyByteBuf>> PENDING = new ConcurrentHashMap<>();
    private static final Map<String, Function<FriendlyByteBuf, FriendlyByteBuf>> HANDLERS = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GEN = new AtomicLong();
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHANNEL, AsyncBridge::onPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, AsyncBridge::onPacket);
    }

    // --- API ---

    /**
     * Клиент -> Сервер: Отправляет запрос и ждет ответ.
     * @param subject Уникальный ID хендлера (например, "GetMana")
     * @param writer Функция записи данных запроса
     */
    public static CompletableFuture<FriendlyByteBuf> askServer(String subject, Function<FriendlyByteBuf, FriendlyByteBuf> writer) {
        return sendInternal(subject, writer, buf -> NetworkManager.sendToServer(CHANNEL, buf));
    }

    /**
     * Сервер -> Клиент: Запрос конкретному игроку.
     */
    public static CompletableFuture<FriendlyByteBuf> askPlayer(ServerPlayer player, String subject, Function<FriendlyByteBuf, FriendlyByteBuf> writer) {
        return sendInternal(subject, writer, buf -> NetworkManager.sendToPlayer(player, CHANNEL, buf));
    }

    /**
     * Регистрация логики обработки запроса (на стороне получателя).
     * @param subject ID запроса (например, "GetMana")
     * @param processor Функция: принимает входной buf, возвращает buf с ответом (или null, если void)
     */
    public static void registerHandler(String subject, Function<FriendlyByteBuf, FriendlyByteBuf> processor) {
        HANDLERS.put(subject, processor);
    }

    // --- Internals ---

    private static CompletableFuture<FriendlyByteBuf> sendInternal(String subject, Function<FriendlyByteBuf, FriendlyByteBuf> writer, java.util.function.Consumer<FriendlyByteBuf> sender) {
        long reqId = ID_GEN.incrementAndGet();
        CompletableFuture<FriendlyByteBuf> future = new CompletableFuture<>();

        PENDING.put(reqId, future);
        // Safety: удаляем зависшие запросы через 5 сек
        SCHEDULER.schedule(() -> {
            if (PENDING.remove(reqId) != null) {
                future.completeExceptionally(new TimeoutException("Packet timed out: " + subject));
            }
        }, 5, TimeUnit.SECONDS);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeLong(reqId);
        buf.writeBoolean(true); // true = это ЗАПРОС
        buf.writeUtf(subject);
        writer.apply(buf);

        sender.accept(buf);
        return future;
    }

    private static void onPacket(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        long id = buf.readLong();
        boolean isRequest = buf.readBoolean();

        if (isRequest) {
            // Это ВХОДЯЩИЙ запрос (кто-то нас спрашивает)
            String subject = buf.readUtf();
            Function<FriendlyByteBuf, FriendlyByteBuf> handler = HANDLERS.get(subject);

            // Выполняем в основном потоке (thread-safe)
            context.queue(() -> {
                if (handler != null) {
                    FriendlyByteBuf responsePayload = handler.apply(buf); // buf здесь уже содержит аргументы

                    // Шлем ответ
                    FriendlyByteBuf reply = new FriendlyByteBuf(Unpooled.buffer());
                    reply.writeLong(id);
                    reply.writeBoolean(false); // false = это ОТВЕТ
                    if (responsePayload != null) reply.writeBytes(responsePayload);

                    if (context.getPlayer() instanceof ServerPlayer sp) {
                        NetworkManager.sendToPlayer(sp, CHANNEL, reply);
                    } else {
                        NetworkManager.sendToServer(CHANNEL, reply);
                    }
                }
            });
        } else {
            // Это ВХОДЯЩИЙ ответ (нам ответили)
            CompletableFuture<FriendlyByteBuf> future = PENDING.remove(id);
            if (future != null) {
                // Копируем буфер, т.к. Netty уничтожит оригинал
                FriendlyByteBuf safeCopy = new FriendlyByteBuf(buf.copy());
                future.complete(safeCopy);
            }
        }
    }
}
