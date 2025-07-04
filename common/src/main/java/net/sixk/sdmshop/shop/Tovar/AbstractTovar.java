package net.sixk.sdmshop.shop.Tovar;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;

import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.api.IItemSerializer;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarTypeRegister;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractTovar implements IItemSerializer {

    public Icon icon;
    public String tab;
    public String currency;
    public Integer cost;
    public UUID uuid;
    public long limit;
    public boolean toSell;

    public AbstractTovar(UUID uuid, Icon icon, String tab, String currency, Integer cost, long limit, boolean toSell){

        this.uuid = uuid;
        this.icon = icon;
        this.cost = cost;
        this.limit = limit;
        this.tab = tab;
        this.currency = currency;
        this.toSell = toSell;

    }
    public abstract void buy(Player player, AbstractTovar tovar, long count);
    public abstract void sell(Player player, AbstractTovar tovar,long count);
    public abstract String getTitel();
    public abstract Icon getIcon();
    public abstract Object getItemStack();
    public abstract TagKey getTag();
    public abstract AbstractTovar copy();
    public abstract String getID();
    public abstract boolean getisXPLVL();
    public final void update(Consumer<AbstractTovar> func){
        func.accept(this);
    };

    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();

        data.put("uuid", uuid.toString());
        //data.put("icon", getIcon())
        data.put("tovarType", getID());
        data.put("tab", IData.valueOf(tab));
        data.put("cost", IData.valueOf(cost));
        data.put("limit", IData.valueOf(limit));
        data.put("currency", currency);
        data.put("toSell", IData.valueOf(toSell?1:0));

        return data;
    }


    public void deserialize(KeyData data, HolderLookup.Provider provider) {

        KeyData d1 = data.getData("tovarType").asKeyMap();

        uuid = data.contains("uuid") ? UUID.fromString(data.getData("uuid").asString()) : UUID.randomUUID();
        tab = data.getData("tab").asString();
        cost = data.getData("cost").asInt();
        limit = data.getData("limit").asLong();
        currency = data.getData("currency").asString();
        toSell = data.getData("toSell").asInt()==1;

    }
}
