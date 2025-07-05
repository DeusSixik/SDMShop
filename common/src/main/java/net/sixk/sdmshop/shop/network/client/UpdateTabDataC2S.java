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
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.utils.ShopDebugUtils;
import net.sixk.sdmshop.utils.ShopNetworkUtils;

public class UpdateTabDataC2S implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateTabDataC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "update_tab"));
    public static final StreamCodec<FriendlyByteBuf, UpdateTabDataC2S> STREAM_CODEC;
    private final Tag tag;

    public UpdateTabDataC2S(Tag tag) {
        this.tag = tag;
    }

    public static void handle(UpdateTabDataC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            ShopDebugUtils.log("[UpdateTabDataC2S::handle]: {} | IsCompound {}", message.tag, message.tag instanceof CompoundTag);

            TovarTab.SERVER.deserializeNBT((CompoundTag) message.tag, context.registryAccess());

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
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.TAG, UpdateTabDataC2S::getTag, UpdateTabDataC2S::new);
    }
}
