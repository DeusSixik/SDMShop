package net.sixik.sdmshoprework.network2;

import dev.architectury.networking.NetworkManager;
import net.sixik.sdmshoprework.network2.requests.SendEntryRequest;
import net.sixik.sdmshoprework.network2.requests.SyncRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SDMRequests {

    public static void init() {}

    private static final Map<String, Request> REQUEST_MAP = new HashMap<>();

    public static String registerRequest(String requestID, Request request) {
        REQUEST_MAP.put(requestID, request);
        return requestID;
    }

    public static void executeRequest(String requestID, NetworkManager.PacketContext handler, List<String> arg, boolean isClient) {
        Request request = REQUEST_MAP.get(requestID);
        if (request == null) {
            throw new IllegalArgumentException("No request registered with ID: " + requestID);
        }

        if(isClient)    request.client.accept(handler, arg);
        else            request.server.accept(handler, arg);
    }


    public record Request(BiConsumer<NetworkManager.PacketContext, List<String>> server, BiConsumer<NetworkManager.PacketContext, List<String>> client) {}



    public static final String SYNC = registerRequest("sync_request", new Request(SyncRequest::server, SyncRequest::client));
    public static final String SEND_ENTRIES = registerRequest("send_entries", new Request(SendEntryRequest::server, SendEntryRequest::client));
}
