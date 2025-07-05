package net.sixk.sdmshop.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmcore.impl.mixin.nbt.CompoundTagAccessor;

import java.util.*;
import java.util.function.Function;

public class ShopNBTUtils {

    public static <T> void putList(CompoundTag nbt, String id, Collection<T> collection, Function<T, Tag> func) {
        if(collection.isEmpty()) return;

        ListTag tags = new ListTag();
        for (T t : collection) {
            tags.add(func.apply(t));
        }
        nbt.put(id, tags);
    }

    public static <T> List<T> getList(CompoundTag nbt, String id, Function<Tag, T> func) {
        if(!nbt.contains(id)) return new ArrayList<>();
        List<T> list = new ArrayList<>();

        ListTag tags = (ListTag) nbt.get(id);

        for (Tag t : tags) {
            T f1 = func.apply(t);
            if(f1 == null) continue;
            list.add(f1);
        }

        return list;
    }

    public static <T> void getList(CompoundTag nbt, String id, Function<Tag, T> func, Collection<T> toAdd) {
        toAdd.addAll(getList(nbt, id, func));
    }

    public static <T> void getListWithClear(CompoundTag nbt, String id, Function<Tag, T> func, Collection<T> toAdd) {
        toAdd.clear();
        toAdd.addAll(getList(nbt, id, func));
    }


    public static void serializeItemStack(CompoundTag nbt, String id, ItemStack itemStack, HolderLookup.Provider provider) {
        CompoundTag itemData = new CompoundTag();

        try {
            CompoundTag itemTag = (CompoundTag)itemStack.save(provider);

            for(Map.Entry<String, Tag> arrayData : ((CompoundTagAccessor)itemTag).getTags().entrySet()) {
                itemData.put((String)arrayData.getKey(), (Tag)arrayData.getValue());
            }
        } catch (Exception var8) {
            itemData.putString("id", "minecraft:air");
        }

        itemData.putInt("count", itemStack.getCount());
        nbt.put(id, itemData);
    }

    public static ItemStack deserializeItemStack(CompoundTag nbt, String key, HolderLookup.Provider provider) {
        CompoundTag tag = nbt.getCompound(key);
        if (!tag.isEmpty()) {
            ItemStack stack = ItemStack.SINGLE_ITEM_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
            stack.setCount(tag.getInt("count"));
            return stack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static double getDouble(CompoundTag nbt, String key, double defaultValue) {
        return getDouble(nbt, key).orElse(defaultValue);
    }

    public static Optional<Double> getDouble(CompoundTag nbt, String key) {
        return Optional.of(nbt.getDouble(key));
    }

    public static int getInt(CompoundTag nbt, String key, int defaultValue) {
        return getInt(nbt, key).orElse(defaultValue);
    }

    public static Optional<Integer> getInt(CompoundTag nbt, String key) {
        return Optional.of(nbt.getInt(key));
    }

    public static float getFloat(CompoundTag nbt, String key, float defaultValue) {
        return getFloat(nbt, key).orElse(defaultValue);
    }

    public static Optional<Float> getFloat(CompoundTag nbt, String key) {
        return Optional.of(nbt.getFloat(key));
    }

    public static boolean getBoolean(CompoundTag nbt, String key, boolean defaultValue) {
        return getBoolean(nbt, key).orElse(defaultValue);
    }

    public static Optional<Boolean> getBoolean(CompoundTag nbt, String key) {
        return Optional.of(nbt.getBoolean(key));
    }

    public static String getString(CompoundTag nbt, String key, String defaultValue) {
        return getString(nbt, key).orElse(defaultValue);
    }

    public static Optional<String> getString(CompoundTag nbt, String key) {
        return Optional.of(nbt.getString(key));
    }

    public static long getLong(CompoundTag nbt, String key, long defaultValue) {
        return getLong(nbt, key).orElse(defaultValue);
    }

    public static Optional<Long> getLong(CompoundTag nbt, String key) {
        return Optional.of(nbt.getLong(key));
    }

    public static byte getByte(CompoundTag nbt, String key, byte defaultValue) {
        return getByte(nbt, key).orElse(defaultValue);
    }

    public static Optional<Byte> getByte(CompoundTag nbt, String key) {
        return Optional.of(nbt.getByte(key));
    }

    public static short getShort(CompoundTag nbt, String key, short defaultValue) {
        return getShort(nbt, key).orElse(defaultValue);
    }

    public static Optional<Short> getShort(CompoundTag nbt, String key) {
        return Optional.of(nbt.getShort(key));
    }
}
