package net.sixik.sdmshop.network.async;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BlobTransfer {
    public static final ResourceLocation CHANNEL = new ResourceLocation(SDMShop.MODID, "blob_channel");

    /**
     * Optimal chunk size: 50 KB.
     * Too little = overhead on headers. Too much (1MB+) = network freezes.
     */
    private static final int CHUNK_SIZE = 50 * 1024;

    private static final Map<Long, ByteBuf> INCOMING_BUFFERS = new ConcurrentHashMap<>();

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHANNEL, BlobTransfer::onPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, BlobTransfer::onPacket);
    }

    /**
     * Sends large amounts of data. Returns a Future that will complete when (theoretically) the transfer is finished.
     * But for “request-response,” how we receive the data is more important to us.
     */
    public static void sendToPlayer(ServerPlayer player, long responseId, FriendlyByteBuf hugeData) {
        sendInternal(hugeData, responseId, buf -> NetworkManager.sendToPlayer(player, CHANNEL, buf));
    }

    public static void sendToServer(long requestId, FriendlyByteBuf hugeData) {
        sendInternal(hugeData, requestId, buf -> NetworkManager.sendToServer(CHANNEL, buf));
    }

    private static void sendInternal(ByteBuf data, long id, Consumer<FriendlyByteBuf> sender) {
        int totalSize = data.readableBytes();
        int chunks = (int) Math.ceil((double) totalSize / CHUNK_SIZE);

        /*
             We use slice to avoid copying memory (Zero-Copy when reading)
         */
        for (int i = 0; i < chunks; i++) {
            int offset = i * CHUNK_SIZE;
            int length = Math.min(CHUNK_SIZE, totalSize - offset);

            FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
            packet.writeLong(id);       // Request ID
            packet.writeInt(i);         // Current chunk index
            packet.writeInt(chunks);    // Total pieces

            /*
                We record a piece of data
             */
            packet.writeBytes(data, offset, length);

            sender.accept(packet);
        }
    }

    private static void onPacket(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        long id = buf.readLong();
        int chunkIndex = buf.readInt();
        int totalChunks = buf.readInt();

        /*
            Obtain or create a buffer for assembly
         */
        ByteBuf accumulator = INCOMING_BUFFERS.computeIfAbsent(id, k -> Unpooled.buffer());

        /*
            IMPORTANT: Netty packets may arrive out of order (rarely, but it happens in UDP; in TCP, MC
            guarantees order, but it is better to write to the end, relying on sequential sending).
            Here, we simply write to the end, since TCP guarantees byte order.
         */
        accumulator.writeBytes(buf);

        /*
            If this is the last piece
         */
        if (chunkIndex == totalChunks - 1) {
            INCOMING_BUFFERS.remove(id); // Remove from map

            FriendlyByteBuf fullData = new FriendlyByteBuf(accumulator);

            /*
                If this was a response to our AsyncBridge request -> we complete it
             */
            context.queue(() -> completeBridgeRequest(id, fullData));
        }
    }

    private static void completeBridgeRequest(long id, FriendlyByteBuf data) {
        AsyncBridge.completeExternal(id, data);
    }
}
