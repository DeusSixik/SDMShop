package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.utils.ShopNetworkUtils;

public class UpdateTovarDataC2S implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateTovarDataC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "update_tovar"));
    public static final StreamCodec<FriendlyByteBuf, UpdateTovarDataC2S> STREAM_CODEC;
    public Tag tag;

    public UpdateTovarDataC2S(Tag tag) {
        this.tag = tag;
    }

    public static void handle(UpdateTovarDataC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            TovarList.SERVER.deserializeNBT((CompoundTag) message.tag, context.registryAccess());

            ShopNetworkUtils.sendShopDataS2C(context.getPlayer().getServer(), context.registryAccess());

            SDMShop.saveData(context.getPlayer().getServer());
        });
    }

    public Tag getTag() {
        return this.tag;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.TAG, UpdateTovarDataC2S::getTag, UpdateTovarDataC2S::new);
    }
}
