package net.sixk.sdmshop.shop.Tab;

import net.minecraft.core.HolderLookup;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmcore.impl.utils.serializer.data.ListData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TovarTab {
    public List<Tab> tabList = new ArrayList();
    public static TovarTab SERVER;
    public static TovarTab CLIENT = new TovarTab();

    public TovarTab() {
    }

    public KeyData serialize(HolderLookup.Provider provider) {
        KeyData data = new KeyData();
        ListData<IData> tabList = new ListData();
        Iterator var4 = this.tabList.iterator();

        while(var4.hasNext()) {
            Tab tab = (Tab)var4.next();
            tabList.addValue(tab.serialize(provider));
        }

        data.put("tabName", tabList);
        return data;
    }

    public void deserialize(KeyData data, HolderLookup.Provider provider) {
        this.tabList.clear();
        ListData<IData> tabList = data.getData("tabName").asList();
        Iterator var4 = tabList.data.iterator();

        while(var4.hasNext()) {
            IData tab = (IData)var4.next();
            Tab w1 = new Tab("", null);
            w1.deserialize(tab.asKeyMap(), provider);
            this.tabList.add(w1);
        }

    }
}
