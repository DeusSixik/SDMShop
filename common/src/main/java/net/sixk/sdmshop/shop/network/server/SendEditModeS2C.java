package net.sixk.sdmshop.shop.network.server;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class SendEditModeS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendEditModeS2C> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "editmod"));
    public static final StreamCodec<FriendlyByteBuf, SendEditModeS2C> STREAM_CODEC;
    boolean editMod;

    public SendEditModeS2C(boolean editMod) {
        this.editMod = editMod;
    }

    public boolean getEditMod() {
        return this.editMod;
    }

    public static void handle(SendEditModeS2C message, NetworkManager.PacketContext context) {
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, SendEditModeS2C::getEditMod, SendEditModeS2C::new);
    }
}
