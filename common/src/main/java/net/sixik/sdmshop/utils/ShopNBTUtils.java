package net.sixik.sdmshop.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
            list.add(func.apply(t));
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

    public static void putItemStack(CompoundTag nbt, String key, ItemStack itemStack) {
        nbt.put(key, itemStack.save(new CompoundTag()));
    }

    public static ItemStack getItemStack(CompoundTag nbt, String key){
        if(nbt.get(key) instanceof StringTag stringTag) {
            Item d1 = BuiltInRegistries.ITEM.get(new ResourceLocation(stringTag.getAsString()));
            if(d1 == null) return ItemStack.EMPTY;
            return d1.getDefaultInstance();
        }

        return ItemStack.of(nbt.getCompound(key));
    }

    public static <T> Optional<T> get(CompoundTag nbt, String id, Function<Tag, T> func) {
        if(!nbt.contains(id)) return Optional.empty();
        return Optional.ofNullable(func.apply(nbt.get(id)));
    }
}