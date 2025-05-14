package net.sixk.sdmshop.shop.Tab;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerHelper;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.api.IItemSerializer;

public class Tab implements IItemSerializer {

    public String name;
    public ItemStack item;

    public Tab(String name, ItemStack item){

        this.name = name;
        this.item = item;

    }

    @Override
    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();

        data.put("name", IData.valueOf(name));
        SDMSerializerHelper.serializeItem(data,"item", item.getItem(), provider);

        return data;
    }

    @Override
    public void deserialize(KeyData data, HolderLookup.Provider provider) {

        name = data.getData("name").asString();
        item = SDMSerializerHelper.deserializeItemStack(data, "item", provider);

    }


}
