package net.sixk.sdmshop.shop.Tovar.TovarType;

import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerHelper;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.api.IConstructor;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import net.sixk.sdmshop.utils.item.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class TovarItem extends AbstractTovar {

    private ItemStack item;
    private boolean byTag;
    private TagKey tag;

    public TovarItem(ItemStack item, boolean byTag, TagKey tag){

        this.item = item;
        this.byTag = byTag;
        this.tag = tag;

    }

    @Override
    public void buy(Player player, Tovar tovar, long count) {

        long currency = CurrencyHelper.getMoney(player, tovar.currency);
        if((tovar.limit < count && tovar.limit != -1) || currency < tovar.cost * count ) return;
        CurrencyHelper.addMoney(player, tovar.currency,-(tovar.cost * count));
        for (int w = 0; w < count; w++) {
            ItemHandlerHelper.giveItemToPlayer(player, item.copy());;
        }
        if(tovar.limit != -1) tovar.limit -= count;

    }

    @Override
    public void sell(Player player, Tovar tovar, long count) {

        if(byTag){
            List<ItemStack> stackList = new ArrayList<>();

            for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
                if(player.getInventory().getItem(index).is(tag)){
                    stackList.add(player.getInventory().getItem(index));
                }
            }

            int amountItems = 0;
            for (ItemStack item : stackList){
                amountItems += item.getCount();
            }



            int amount = amountItems >= item.getCount() * count ? (int) (item.getCount() * count) : 0;
            if(amountItems == 0 || amount == 0) return;

            if ((tovar.limit < count && tovar.limit != -1)) return;

            if (amount <= 0) return;
            if(sellItem(player, amount, tag)) CurrencyHelper.addMoney(player, tovar.currency, tovar.cost * count);
            if (tovar.limit != -1) tovar.limit -= count;
        }
        else
        {
            if ((tovar.limit < count && tovar.limit != -1)) return;
            CurrencyHelper.addMoney(player, tovar.currency, tovar.cost * count);
            sellItem(player, (int) (count * item.getCount()), item);
            if (tovar.limit != -1) tovar.limit -= count;
        }

    }

    @Override
    public String getTitel() {

        return item.getDisplayName().getString().replace("[","").replace("]", "");

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

    public static boolean sellItem(Player p, int amm, TagKey<Item> item) {
        int totalamm = 0; //общее количество вещей в инвентаре
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) { //считаем эти вещи
            if (p.getInventory().getItem(a)!=null){
                /*весь ItemStack можно описать тремя параметрами. item.getData, item.getItemMeta и item.getAmmaount.
                 *При item.equas(item2)ammount тоже сравнивается, поэтому видим такое сравнение
                 */
                if (p.getInventory().getItem(a).is(item)){
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
            if (p.getInventory().getItem(a).is(item)) {
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


    @Override
    public Icon getIcon() {
        return ItemIcon.getItemIcon(item);
    }

    @Override
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public TagKey getTag() {
        return tag;
    }

    @Override
    public AbstractTovar copy() {
        return null;
    }

    @Override
    public String getID() {
        return "ItemType";
    }

    @Override
    public boolean getisXPLVL() {
        return byTag;
    }


    @Override
    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();


        SDMSerializerHelper.serializeItemStack(data,"item", item ,provider);
        data.put("id",getID());
        data.put("tag",tag.location().toString());
        data.put("byTag", IData.valueOf(byTag?1:0));

        return data;
    }

    @Override
    public void deserialize(KeyData data, HolderLookup.Provider provider) {
        String t1 = data.getData("tag").asString();
        item = SDMSerializerHelper.deserializeItemStack(data, "item", provider);
        for (Pair<TagKey<Item>, HolderSet.Named<Item>> t : BuiltInRegistries.ITEM.getTags().toList()) {
            if(t.getFirst().location().toString().equals(t1)) {
                tag = t.getFirst();
                break;
            }
        }

        byTag = data.getData("byTag").asInt()==1;
    }

    public static class Constructor implements IConstructor<AbstractTovar> {
        @Override
        public AbstractTovar create() {
            return new TovarItem(ItemStack.EMPTY,false,null);
        }
    }

}
