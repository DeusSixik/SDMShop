package net.sixk.sdmshop.shop.network.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.Tovar;
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

            Tovar tovar = TovarList.SERVER.tovarList.get(message.index);
            if (tovar == null) return;
            if((tovar.limit < message.count && tovar.limit != -1)) return;
            CurrencyHelper.addMoney(context.getPlayer(), tovar.currency,tovar.cost * message.count);
            sellItem(context.getPlayer(), message.getCount() * tovar.item.getCount(), tovar.item);
            if(tovar.limit != -1) tovar.limit -= message.count;

            NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new SendShopDataS2C(TovarList.SERVER.serialize(context.registryAccess()).asNBT(), TovarTab.SERVER.serialize().asNBT()));
            NetworkManager.sendToServer(new UpdateServerDataC2S(new CompoundTag()));
        });
    }

    public static boolean sellItem(Player p, int amm, ItemStack item) {


        int totalamm = 0; //общее количество вещей в инвентаре
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) { //считаем эти вещи
            if (p.getInventory().getItem(a)!=null){
                /*весь ItemStack можно описать тремя параметрами. item.getData, item.getItemMeta и item.getAmmaount.
                 *При item.equas(item2)ammount тоже сравнивается, поэтому видим такое сравнение
                 */
                if(ItemStack.isSameItem(p.getInventory().getItem(a), item) && ItemStack.isSameItemSameComponents(item,p.getInventory().getItem(a))) {
                    totalamm += p.getInventory().getItem(a).getCount();
                }
            }
        }
        if (totalamm==0) {
            return false;
        }
        if (totalamm<amm) {
            return false;
        }
        int ammountleft =amm; //эта переменная не очень нужна, но мне с ней удобнее
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) {
            if (ammountleft==0){return true;}
            if (p.getInventory().getItem(a)==null) continue;

            if(ItemStack.isSameItem(p.getInventory().getItem(a), item) && ItemStack.isSameItemSameComponents(item,p.getInventory().getItem(a))){
                if (p.getInventory().getItem(a).getCount()<ammountleft) {
                    ammountleft-=p.getInventory().getItem(a).getCount();
                    p.getInventory().setItem(a, ItemStack.EMPTY);
                }
                if (p.getInventory().getItem(a)!=null&&p.getInventory().getItem(a).getCount()==ammountleft) {
                    p.getInventory().setItem(a, ItemStack.EMPTY);
                    return true;
                }

                if (p.getInventory().getItem(a).getCount()>ammountleft&&p.getInventory().getItem(a)!=null) {
                    p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount()-ammountleft);
                    return true;
                }
            }
        }
        return false;
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
