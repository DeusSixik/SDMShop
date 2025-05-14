package net.sixk.sdmshop.shop.network.server;

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
import net.sixk.sdmshop.shop.Tovar.TovarList;

public class SendShopDataS2C implements CustomPacketPayload{

    public static final CustomPacketPayload.Type<SendShopDataS2C> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "send_data"));
    public static final StreamCodec<FriendlyByteBuf, SendShopDataS2C> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.TAG, SendShopDataS2C::getTovarTag, ByteBufCodecs.TAG, SendShopDataS2C::getTabTag, SendShopDataS2C::new);

    Tag tovarTag;
    Tag tabTag;

   public SendShopDataS2C(Tag tovarTag, Tag tabTeg){
       this.tovarTag = tovarTag;
       this.tabTag = tabTeg;
   }

    public static void handle(SendShopDataS2C message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            TovarList.CLIENT.deserialize((KeyData) IData.valueOf(message.tovarTag),context.registryAccess());
            TovarTab.CLIENT.deserialize((KeyData) IData.valueOf(message.tabTag), context.registryAccess());
        });
    }

    public Tag getTovarTag() {
        return tovarTag;
    }

    public Tag getTabTag() {
        return tabTag;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
