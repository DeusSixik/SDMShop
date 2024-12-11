package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TovarTab;

public class UpdateTabDataC2S implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateTabDataC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "update_tab"));
    public static final StreamCodec<FriendlyByteBuf, UpdateTabDataC2S> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.TAG, UpdateTabDataC2S::getTag, UpdateTabDataC2S::new);

    public Tag tag;

    public UpdateTabDataC2S(Tag tag){
        this.tag = tag;
    }

    public static void handle(UpdateTabDataC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            TovarTab.SERVER.deserialize((KeyData) IData.valueOf(message.tag));
            SDMShop.saveData(context.getPlayer().getServer());
        });
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
