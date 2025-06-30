package net.sixk.sdmshop.shop.network.server;

import dev.architectury.networking.NetworkManager;



import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixk.sdmshop.shop.ShopPage;


public class SendOpenShopScreenS2C implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SendOpenShopScreenS2C> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "open_shop"));
    public static final StreamCodec<FriendlyByteBuf, SendOpenShopScreenS2C> STREAM_CODEC;
    boolean r;

    public SendOpenShopScreenS2C(boolean r) {
        this.r = r;
    }

    public boolean getBool() {
        return this.r;
    }

    public static void handle(SendOpenShopScreenS2C message, NetworkManager.PacketContext context) {
        (new ShopPage()).openGui();
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, SendOpenShopScreenS2C::getBool, SendOpenShopScreenS2C::new);
    }
}
