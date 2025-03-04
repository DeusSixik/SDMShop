package net.sixik.sdmshoprework.network2.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.network2.SDMRequests;

import java.util.List;

public class SendRequestS2C extends BaseS2CMessage {

    private final String request;
    private final List<String> arg;

    public SendRequestS2C(String request) {
        this.request = request;
        this.arg = List.of("null");
    }

    public SendRequestS2C(String request, List<String> arg) {

        this.request = request;
        this.arg = arg;
    }

    public SendRequestS2C(FriendlyByteBuf buf) {
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
        SDMRequests.executeRequest(request, context, arg, true);
    }
}
