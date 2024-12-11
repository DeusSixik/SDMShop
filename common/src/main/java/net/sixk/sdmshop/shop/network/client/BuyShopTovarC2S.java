package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;
import net.sixk.sdmshop.utils.item.ItemHandlerHelper;

public class BuyShopTovarC2S implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BuyShopTovarC2S> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "buy_tovar"));
    public static final StreamCodec<FriendlyByteBuf, BuyShopTovarC2S> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, BuyShopTovarC2S::getIndex,ByteBufCodecs.INT,BuyShopTovarC2S::getCount, BuyShopTovarC2S::new);

    public Integer index;
    public Integer count;

    public BuyShopTovarC2S(Integer index, Integer count) {
        this.index = index;
        this.count = count;
    }

    public static void handle(BuyShopTovarC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {

            Tovar tovar = TovarList.SERVER.tovarList.get(message.index);
            if (tovar == null) return;
            long currency = CurrencyHelper.getMoney(context.getPlayer(), tovar.currency);
            if((tovar.limit < message.count && tovar.limit != -1) || currency < tovar.cost * message.count ) return;
            CurrencyHelper.addMoney(context.getPlayer(), tovar.currency,-(tovar.cost * message.count));
            for (int w = 0; w < message.count; w++) {
               ItemHandlerHelper.giveItemToPlayer(context.getPlayer(), tovar.item.copy());;
            }
            if(tovar.limit != -1) tovar.limit -= message.count;

            NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new SendShopDataS2C(TovarList.SERVER.serialize(context.registryAccess()).asNBT(), TovarTab.SERVER.serialize().asNBT()));
            NetworkManager.sendToServer(new UpdateServerDataC2S(new CompoundTag()));
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
