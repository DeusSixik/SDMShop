package net.sixik.sdmshop.utils;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class ShopItemHelper {

    public static int countItem(final Container container, final ItemStack itemStack, boolean strictNbt, boolean ignoreDamage) {
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slotItem = container.getItem(i);
            if (matches(itemStack, slotItem, strictNbt, ignoreDamage)) {
                count += slotItem.getCount();
            }
        }
        return count;
    }

    public static int countItem(final Container container, final ItemStack itemStack, final boolean ignoreNbt) {
        return countItemByPredicate(container, v -> equals(v, itemStack, ignoreNbt));
    }

    public static int countItem(final Container container, final TagKey<Item> tagKey) {
        return countItemByPredicate(container, v -> v.is(tagKey));
    }

    public static int countItemByPredicate(final Container container, final Predicate<ItemStack> predicate) {
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack findItem = container.getItem(i);
            if(findItem.isEmpty() || !predicate.test(findItem)) continue;

            count += findItem.getCount();
        }

        return count;
    }

    public static boolean shrinkItem(final Container container, final ItemStack itemStack,
                                     final int amount, final boolean ignoreNbt) {
        return shrinkItemByPredicate(container, v -> equals(itemStack, v, ignoreNbt), amount);
    }

    public static boolean shrinkItemByTag(final Container container, final TagKey<Item> item, final int amount) {
        return shrinkItemByPredicate(container, v -> v.is(item), amount);
    }

    public static boolean shrinkItemByPredicate(final Container container, Predicate<ItemStack> itemStackPredicate, final int amount) {
        int localAmount = amount <= 0 ? 1 : amount;
        int remainingToRemove = localAmount;

        if (countItemByPredicate(container, itemStackPredicate) < localAmount) {
            return false;
        }

        for (int i = 0; i < container.getContainerSize() && remainingToRemove > 0; i++) {
            ItemStack findItem = container.getItem(i);

            if (findItem.isEmpty() || !itemStackPredicate.test(findItem)) {
                continue;
            }

            int itemCount = findItem.getCount();

            if (itemCount <= remainingToRemove) {
                remainingToRemove -= itemCount;
                container.setItem(i, ItemStack.EMPTY);
            } else {
                findItem.setCount(itemCount - remainingToRemove);
                container.setItem(i, findItem);
                remainingToRemove = 0;
            }
        }

        return true;
    }

    public static boolean equals(final ItemStack item1, final ItemStack item2, final boolean ignoreNbt) {
        if(item1 == null || item2 == null) return false;

        boolean equalsItem = Objects.equals(item1.getItem(), item2.getItem());
        boolean equalsNbt = ignoreNbt || equalsNbt(item1, item2);

        return equalsItem && equalsNbt;
    }

    public static boolean equalsNbt(final ItemStack item1, final ItemStack item2) {
        if(!item1.hasTag() || !item2.hasTag()) return false;
        return NbtUtils.compareNbt(item1.getTag(), item2.getTag(), true);
    }

    private static long distributeItems(final Container container, final ItemStack itemStack, long left) {
        int subtract = container instanceof Inventory ? 4 : 0;
        int maxStack = itemStack.getMaxStackSize();

        boolean ignoreNbt = !itemStack.hasTag();

        for (int i = 0; i < container.getContainerSize() - subtract && left > 0; i++) {
            ItemStack slotItem = container.getItem(i);
            if (equals(slotItem, itemStack, ignoreNbt) && slotItem.getCount() < maxStack) {
                int toAdd = (int) Math.min(maxStack - slotItem.getCount(), left);
                slotItem.grow(toAdd);
                container.setItem(i, slotItem);
                left -= toAdd;
            }
        }

        for (int i = 0; i < container.getContainerSize() - subtract && left > 0; i++) {
            ItemStack slotItem = container.getItem(i);
            if (slotItem.isEmpty()) {
                int toAdd = (int) Math.min(maxStack, left);
                container.setItem(i, itemStack.copyWithCount(toAdd));
                left -= toAdd;
            }
        }

        return left;
    }

    public static boolean giveItems(final Player player, final ItemStack itemStack, long amount) {
        if (itemStack == null || itemStack.isEmpty() || amount <= 0) return false;
        long left = distributeItems(player.getInventory(), itemStack, amount);
        if (left > 0) player.drop(itemStack.copyWithCount((int) left), true);
        return true;
    }

    public static boolean giveItems(final Container container, final ItemStack itemStack, long amount) {
        if (itemStack == null || itemStack.isEmpty() || amount <= 0) return false;
        distributeItems(container, itemStack, amount);
        return true;
    }

    public static boolean isSearch(final String search, final ItemStack itemStack) {
        return itemStack.getDisplayName().getString().contains(search) || BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString().contains(search);
    }

    public static boolean isSearch(String search, HolderSet.Named<Item> tag) {
        if(tag.size() == 0) return false;

        for (int i = 0; i < tag.size(); i++) {
            if(isSearch(search, tag.get(i).value().getDefaultInstance())) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkNbtRunning(ItemStack s1, ItemStack s2) {
        return ItemStack.isSameItemSameTags(s1, s2);
    }

    public static boolean matches(ItemStack shopItem, ItemStack playerItem, boolean strictNbt, boolean ignoreDamage) {
        if (playerItem.isEmpty() || shopItem.isEmpty()) return false;
        if (!playerItem.is(shopItem.getItem())) return false;

        // Если нам важен NBT (по умолчанию true для безопасности)
        if (strictNbt) {
            // Если у магазинного предмета нет NBT, то и у игрока не должно быть (иначе продадим зачарованный меч как палку)
            if (!shopItem.hasTag()) {
                return !playerItem.hasTag();
            }

            // Если NBT есть, проверяем совпадение
            // Для инструментов можно игнорировать Damage, если это настроено
            if (ignoreDamage && playerItem.isDamageableItem()) {
                return ItemStack.isSameItemSameTags(shopItem, playerItem) ||
                        (playerItem.getDamageValue() != shopItem.getDamageValue() && checkNbtRunning(shopItem, playerItem));
            }

            return ItemStack.isSameItemSameTags(shopItem, playerItem);
        }

        return true;
    }

    public static boolean shrinkItem(final Container container, final ItemStack itemStack, final int amount, boolean strictNbt, boolean ignoreDamage) {
        if (amount <= 0) return false;

        // Сначала проверяем, есть ли нужное количество (избегаем частичного удаления)
        if (countItem(container, itemStack, strictNbt, ignoreDamage) < amount) {
            return false;
        }

        int remainingToRemove = amount;

        for (int i = 0; i < container.getContainerSize() && remainingToRemove > 0; i++) {
            ItemStack slotItem = container.getItem(i);

            if (matches(itemStack, slotItem, strictNbt, ignoreDamage)) {
                int count = slotItem.getCount();
                if (count <= remainingToRemove) {
                    remainingToRemove -= count;
                    container.setItem(i, ItemStack.EMPTY);
                } else {
                    slotItem.shrink(remainingToRemove); // Используем нативный метод
                    remainingToRemove = 0;
                    // setItem не нужен, так как мы изменили объект по ссылке, но для надежности можно оставить
                    container.setChanged();
                }
            }
        }
        return remainingToRemove == 0;
    }


}
