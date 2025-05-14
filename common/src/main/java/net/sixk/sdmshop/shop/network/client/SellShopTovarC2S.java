package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

public class SellShopTovarC2S implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SellShopTovarC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "sell_tovar"));
    public static final StreamCodec<FriendlyByteBuf, SellShopTovarC2S> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, SellShopTovarC2S::getIndex,ByteBufCodecs.INT,SellShopTovarC2S::getCount, SellShopTovarC2S::new);

    public Integer index;
    public Integer count;

    public SellShopTovarC2S(Integer index, Integer count) {
        this.index = index;
        this.count = count;
    }

    public static void handle(SellShopTovarC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {

            TovarList.SERVER.tovarList.get(message.index).abstractTovar.sell(context.getPlayer(),TovarList.SERVER.tovarList.get(message.index),message.count);

            NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new SendShopDataS2C(TovarList.SERVER.serialize(context.registryAccess()).asNBT(), TovarTab.SERVER.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
            SDMShop.saveData(context.getPlayer().getServer());
        });
    }



    public Integer getIndex() {
        return index;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
