package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixk.sdmshop.SDMShop;

public record UpdateServerDataC2S(CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateServerDataC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "update_server_data"));
    public static final StreamCodec<FriendlyByteBuf, UpdateServerDataC2S> STREAM_CODEC;

    public UpdateServerDataC2S(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateServerDataC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            SDMShop.saveData(context.getPlayer().getServer());
        });
    }

    public CompoundTag nbt() {
        return this.nbt;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, UpdateServerDataC2S::nbt, UpdateServerDataC2S::new);
    }
}