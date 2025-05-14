package net.sixk.sdmshop.shop.Tab;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmcore.impl.utils.serializer.data.ListData;
import net.sixik.sdmcore.impl.utils.serializer.data.basic.StringData;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TovarTab  {

    public List<Tab> tabList = new ArrayList<>();
    public static TovarTab SERVER;
    public static TovarTab CLIENT = new TovarTab();


    public TovarTab(){


    }



    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();
        ListData<IData> tabList = new ListData<>();

        for (Tab tab : this.tabList) {
            tabList.addValue(tab.serialize(provider));
        }

        data.put("tabName", tabList);

        return data;
    }


    public void deserialize(KeyData data, HolderLookup.Provider provider) {

        this.tabList.clear();

        ListData<IData> tabList = data.getData("tabName").asList();

        for (IData tab : tabList.data) {
            Tab w1 = new Tab("", null);
            w1.deserialize(tab.asKeyMap(), provider);
            this.tabList.add(w1);
        }

    }
}
