package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

import java.util.Iterator;

public class UpdateTovarDataC2S implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateTovarDataC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "update_tovar"));
    public static final StreamCodec<FriendlyByteBuf, UpdateTovarDataC2S> STREAM_CODEC;
    public Tag tag;

    public UpdateTovarDataC2S(Tag tag) {
        this.tag = tag;
    }

    public static void handle(UpdateTovarDataC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            TovarList.SERVER.deserialize((KeyData)IData.valueOf(message.tag), context.registryAccess());
            Iterator var2 = context.getPlayer().getServer().getPlayerList().getPlayers().iterator();

            while(var2.hasNext()) {
                ServerPlayer player = (ServerPlayer)var2.next();
                NetworkManager.sendToPlayer(player, new SendShopDataS2C(TovarList.SERVER.serialize(context.registryAccess()).asNBT(), TovarTab.SERVER.serialize(context.registryAccess()).asNBT()));
            }

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
