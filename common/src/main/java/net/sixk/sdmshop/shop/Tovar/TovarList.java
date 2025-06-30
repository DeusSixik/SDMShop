package net.sixk.sdmshop.shop.Tovar;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmcore.impl.utils.serializer.data.ListData;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarTypeRegister;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarXP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TovarList  {

    public  List<AbstractTovar> tovarList = new ArrayList<>();
    public static TovarList SERVER;
    public static TovarList CLIENT = new TovarList() ;

    public TovarList(){


    }


    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();

        ListData<IData> tovarList = new ListData<>();
        for (AbstractTovar value : this.tovarList) {
            tovarList.addValue(value.serialize(provider));
        }
        data.put("tovarList",tovarList);
        return data;

    }


    public void deserialize(KeyData data, HolderLookup.Provider provider) {

        tovarList.clear();

        ListData<IData> tovarList = data.getData("tovarList").asList();

        for (IData w : tovarList.data) {
            KeyData w1 = (KeyData) w;
            if(!w1.contains("tovarType")) continue;
            TovarTypeRegister.getType(w1.getData("tovarType").asString()).ifPresent(func ->{
                AbstractTovar w2 = func.apply(UUID.randomUUID(), "", "", 0, 0l, false);
                w2.deserialize(w1, provider);
                this.tovarList.add(w2);
            });
        }


    }
}
