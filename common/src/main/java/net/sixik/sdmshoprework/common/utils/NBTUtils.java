package net.sixik.sdmshoprework.common.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class NBTUtils {

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
}
