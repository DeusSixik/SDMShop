package net.sixk.sdmshop.shop.Tab;

import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmcore.impl.utils.serializer.data.ListData;
import net.sixik.sdmcore.impl.utils.serializer.data.basic.StringData;


import java.util.ArrayList;
import java.util.List;

public class TovarTab implements SDMSerializer<KeyData> {

    public  List<String> tabList = new ArrayList<>();
    public static TovarTab SERVER;
    public static TovarTab CLIENT = new TovarTab();


    public TovarTab(){


    }


    @Override
    public KeyData serialize() {

        KeyData data = new KeyData();
        ListData<StringData> tabList = new ListData<>();

        for (String tab : this.tabList) {
            tabList.addValue((StringData) IData.valueOf(tab));
        }

        data.put("tabName", tabList);

        return data;
    }

    @Override
    public void deserialize(KeyData data) {

        this.tabList.clear();

        ListData<IData> tabList = data.getData("tabName").asList();

        for (IData tab : tabList.data) this.tabList.add(tab.asString());

    }
}
