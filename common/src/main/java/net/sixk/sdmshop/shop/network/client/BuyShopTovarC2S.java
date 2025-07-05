package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.utils.ShopNetworkUtils;

public class BuyShopTovarC2S implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BuyShopTovarC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "buy_tovar"));
    public static final StreamCodec<FriendlyByteBuf, BuyShopTovarC2S> STREAM_CODEC;
    public Integer index;
    public Integer count;

    public BuyShopTovarC2S(Integer index, Integer count) {
        this.index = index;
        this.count = count;
    }

    public static void handle(BuyShopTovarC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            (TovarList.SERVER.tovarList.get(message.index)).buy(context.getPlayer(), TovarList.SERVER.tovarList.get(message.index), (long)message.count);

            ShopNetworkUtils.sendShopDataS2C(context.getPlayer().getServer(), context.registryAccess());

            CurrencyHelper.syncPlayer((ServerPlayer)context.getPlayer());
            SDMShop.saveData(context.getPlayer().getServer());
        });
    }

    public Integer getIndex() {
        return this.index;
    }

    public Integer getCount() {
        return this.count;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, BuyShopTovarC2S::getIndex, ByteBufCodecs.INT, BuyShopTovarC2S::getCount, BuyShopTovarC2S::new);
    }
}
