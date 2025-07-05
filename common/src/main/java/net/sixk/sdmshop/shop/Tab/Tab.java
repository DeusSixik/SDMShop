package net.sixk.sdmshop.shop.Tab;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.sixk.sdmshop.api.DataSerializerCompound;
import net.sixk.sdmshop.utils.ShopNBTUtils;

public class Tab implements DataSerializerCompound {
    public String name;
    public ItemStack item;

    public Tab(String name, ItemStack item){
        this.name = name;
        this.item = item;
    }



    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", name);
        ShopNBTUtils.serializeItemStack(nbt, "item", item, provider);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        this.name = nbt.getString("name");
        this.item = ShopNBTUtils.deserializeItemStack(nbt, "item", provider);
    }
}
