package net.sixk.sdmshop.shop.Tovar;

import net.minecraft.core.HolderLookup;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmcore.impl.utils.serializer.data.ListData;

import java.util.ArrayList;
import java.util.List;

public class TovarList  {

    public  List<Tovar> tovarList = new ArrayList<>();
    public static TovarList SERVER;
    public static TovarList CLIENT = new TovarList() ;

    public TovarList(){


    }


    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();

        ListData<IData> tovarList = new ListData<>();
        for (Tovar value : this.tovarList) {
            tovarList.addValue(value.serialize(provider));
        }
        data.put("tovarList",tovarList);
        return data;

    }


    public void deserialize(KeyData data,HolderLookup.Provider provider) {

        tovarList.clear();

        ListData<IData> tovarList = data.getData("tovarList").asList();

        for (IData w : tovarList.data) {

            Tovar w1 = new Tovar(null, "", null, 0, 0,false);
            w1.deserialize(w.asKeyMap(), provider);
            this.tovarList.add(w1);

        }


    }
}
