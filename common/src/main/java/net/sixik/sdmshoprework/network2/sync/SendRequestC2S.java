package net.sixik.sdmshoprework.network2.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.network2.SDMRequests;

import java.util.List;

public class SendRequestC2S extends BaseC2SMessage {

    private final String request;
    private final List<String> arg;

    public SendRequestC2S(String request) {
        this.request = request;
        this.arg = List.of("null");
    }

    public SendRequestC2S(String request, List<String> arg) {

        this.request = request;
        this.arg = arg;
    }

    public SendRequestC2S(FriendlyByteBuf buf) {
        this.request = buf.readUtf();
        this.arg = buf.readList(FriendlyByteBuf::readUtf);
    }

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(request);
        buf.writeCollection(arg, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        SDMRequests.executeRequest(request, context, arg, false);
    }
}
