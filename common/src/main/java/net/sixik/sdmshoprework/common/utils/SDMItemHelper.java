package net.sixik.sdmshoprework.common.utils;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SDMItemHelper {

    public static boolean sellItem(Player p, int amm, TagKey<Item> item) {
        Inventory inventory = p.getInventory();
        int totalAmount = 0;

        // Подсчёт предметов в инвентаре
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                totalAmount += stack.getCount();
            }
        }

        // Если предметов недостаточно — выход
        if (totalAmount < amm) {
            return false;
        }

        int amountLeft = amm; // Сколько ещё нужно удалить

        // Удаление предметов
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (amountLeft == 0) break; // Если всё удалили — выход

            ItemStack stack = inventory.getItem(i);

            if (!stack.isEmpty() && stack.is(item)) {
                int stackCount = stack.getCount();

                if (stackCount <= amountLeft) {
                    amountLeft -= stackCount;
                    inventory.setItem(i, ItemStack.EMPTY);
                } else {
                    stack.setCount(stackCount - amountLeft);
                    return true;
                }
            }
        }

        return true;
    }

    public static int countItems(Player p, ItemStack item) {
        boolean flag = item.hasTag();

        int totalamm = 0; //общее количество вещей в инвентаре
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) { //считаем эти вещи
            if (!p.getInventory().getItem(a).isEmpty()){
                /*весь ItemStack можно описать тремя параметрами. item.getData, item.getItemMeta и item.getAmmaount.
                 *При item.equas(item2)ammount тоже сравнивается, поэтому видим такое сравнение
                 */
                if(flag && ItemStack.matches(p.getInventory().getItem(a), item)) {
                    totalamm += p.getInventory().getItem(a).getCount();
                } else if(!flag && ItemStack.isSameItem(p.getInventory().getItem(a), item) || ItemStack.matches(p.getInventory().getItem(a), item)){
                    totalamm += p.getInventory().getItem(a).getCount();
                }
            }
        }

        return totalamm;
    }

    public static boolean sellItem(Player p, int amm, ItemStack item, boolean ignoreNBT) {
        int totalAmount = 0; // Общее количество предметов в инвентаре
        Inventory inventory = p.getInventory();

        // Подсчёт предметов в инвентаре
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (!stack.isEmpty() && (ignoreNBT ? ItemStack.isSameItem(stack, item) : ItemStack.matches(stack, item))) {
                totalAmount += stack.getCount();
            }
        }

        // Если предметов недостаточно — выход
        if (totalAmount < amm) {
            return false;
        }

        int amountLeft = amm; // Сколько ещё нужно удалить

        // Удаление предметов
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (amountLeft == 0) break; // Если всё удалили — выход

            ItemStack stack = inventory.getItem(i);

            if (!stack.isEmpty() && (ignoreNBT ? ItemStack.isSameItem(stack, item) : ItemStack.matches(stack, item))) {
                int stackCount = stack.getCount();

                if (stackCount <= amountLeft) {
                    amountLeft -= stackCount;
                    inventory.setItem(i, ItemStack.EMPTY);
                } else {
                    stack.setCount(stackCount - amountLeft);
                    return true;
                }
            }
        }

        return amountLeft != amm;
    }

//    public static boolean sellItem(Player p, int amm, ItemStack item, boolean ignoreNBT) {
//
//        boolean flag = item.hasTag();
//
//        int totalamm = 0; //общее количество вещей в инвентаре
//        for (int a = 0; a<p.getInventory().getContainerSize(); a++) { //считаем эти вещи
//            if(!ignoreNBT && (flag && ItemStack.matches(p.getInventory().getItem(a), item))) {
//                totalamm += p.getInventory().getItem(a).getCount();
//            } else if(!ignoreNBT && (!flag && ItemStack.isSameItem(p.getInventory().getItem(a), item) || ItemStack.matches(p.getInventory().getItem(a), item))){
//                totalamm += p.getInventory().getItem(a).getCount();
//            } else if(!ignoreNBT && (ItemStack.isSameItem(p.getInventory().getItem(a), item) || ItemStack.matches(p.getInventory().getItem(a), item))) {
//                totalamm += p.getInventory().getItem(a).getCount();
//            }
//        }
//        if (totalamm==0) {
//            return false;
//        }
//        if (totalamm<amm) {
//            return false;
//        }
//        int ammountleft =amm; //эта переменная не очень нужна, но мне с ней удобнее
//        for (int a = 0; a<p.getInventory().getContainerSize(); a++) {
//            if (ammountleft==0){return true;}
//
//
//            if(!ignoreNBT && ((flag && ItemStack.isSameItem(p.getInventory().getItem(a), item)) ||
//                    (!flag && (ItemStack.isSameItem(p.getInventory().getItem(a), item) ||
//                            ItemStack.matches(p.getInventory().getItem(a), item))))
//            ){
//                if (p.getInventory().getItem(a).getCount()<ammountleft) {
//                    ammountleft-=p.getInventory().getItem(a).getCount();
//                    p.getInventory().setItem(a, ItemStack.EMPTY);
//                }
//                p.getInventory().getItem(a);
//                if (p.getInventory().getItem(a).getCount() == ammountleft) {
//                    p.getInventory().setItem(a, ItemStack.EMPTY);
//                    return true;
//                }
//
//                if (p.getInventory().getItem(a).getCount() > ammountleft) {
//                    p.getInventory().getItem(a);
//                    p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount() - ammountleft);
//                    return true;
//                }
//            }
//
//            if(ignoreNBT && (ItemStack.isSameItem(p.getInventory().getItem(a), item) || ItemStack.matches(p.getInventory().getItem(a), item))) {
//                if (p.getInventory().getItem(a).getCount()<ammountleft) {
//                    ammountleft-=p.getInventory().getItem(a).getCount();
//                    p.getInventory().setItem(a, ItemStack.EMPTY);
//                }
//                if (p.getInventory().getItem(a)!=null&&p.getInventory().getItem(a).getCount()==ammountleft) {
//                    p.getInventory().setItem(a, ItemStack.EMPTY);
//                    return true;
//                }
//
//                if (p.getInventory().getItem(a).getCount()>ammountleft&&p.getInventory().getItem(a)!=null) {
//                    p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount()-ammountleft);
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
