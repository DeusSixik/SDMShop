package net.sixk.sdmshop.shop.Tovar;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerHelper;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarTypeRegister;


public class Tovar {

    public AbstractTovar abstractTovar = null;
    public String tab;
    public String currency;
    public Integer cost;
    public long limit;
    public boolean toSell;



    public Tovar(String tab, String currency, Integer cost, long limit, boolean toSell){


        this.cost = cost;
        this.limit = limit;
        this.tab = tab;
        this.currency = currency;
        this.toSell = toSell;

    }

    public Tovar setAbstract(AbstractTovar abstractTovar){
        this.abstractTovar = abstractTovar;
        return  this;
    }

    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();
        if(abstractTovar != null)
            data.put("tovarType", abstractTovar.serialize(provider));
        data.put("tab", IData.valueOf(tab));
        data.put("cost", IData.valueOf(cost));
        data.put("limit", IData.valueOf(limit));
        data.put("currency", currency);
        data.put("toSell", IData.valueOf(toSell?1:0));

        return data;
    }


    public void deserialize(KeyData data, HolderLookup.Provider provider) {

        KeyData d1 = data.getData("tovarType").asKeyMap();
        abstractTovar = TovarTypeRegister.TOVAR_MAP.get(d1.getData("id").asString()).create();
        abstractTovar.deserialize(d1, provider);
        tab = data.getData("tab").asString();
        cost = data.getData("cost").asInt();
        limit = data.getData("limit").asLong();
        currency = data.getData("currency").asString();
        toSell = data.getData("toSell").asInt()==1;

    }
}
