package net.sixk.sdmshop.shop.Tab;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.sixk.sdmshop.api.DataSerializerCompound;
import net.sixk.sdmshop.utils.ShopNBTUtils;

import java.util.ArrayList;
import java.util.List;

public class TovarTab implements DataSerializerCompound {
    public List<Tab> tabList = new ArrayList();
    public static TovarTab SERVER;
    public static TovarTab CLIENT = new TovarTab();

    public TovarTab() {
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ShopNBTUtils.putList(nbt, "tabName", this.tabList, s -> s.serializeNBT(provider));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        this.tabList = ShopNBTUtils.getList(nbt, "tabName", (tag) -> {
            Tab w1 = new Tab("", null);
            w1.deserializeNBT((CompoundTag) tag, provider);
            return w1;
        });
    }
}
