package net.sdm.sdmshoprework.common.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class NBTUtils {

    public static void putItemStack(CompoundTag nbt, String key, ItemStack itemStack) {
        nbt.put(key, itemStack.save(new CompoundTag()));
    }

    public static ItemStack getItemStack(CompoundTag nbt, String key){
        return ItemStack.of(nbt.getCompound(key));
    }
}
