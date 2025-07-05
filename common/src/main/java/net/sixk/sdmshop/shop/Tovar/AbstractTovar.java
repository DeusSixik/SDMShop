package net.sixk.sdmshop.shop.Tovar;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.sixk.sdmshop.api.DataSerializerCompound;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractTovar implements DataSerializerCompound {

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

    @Override
    public final CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", getID());
        nbt.putUUID("uuid", uuid);
        nbt.putString("tovarType", getID());
        nbt.putString("tab", tab);
        nbt.putInt("cost", cost);
        nbt.putLong("limit", limit);
        nbt.putString("currency", currency);
        nbt.putBoolean("toSell", toSell);
        _serializeNBT(nbt, provider);
        return nbt;
    }

    public void  _serializeNBT(CompoundTag nbt, HolderLookup.Provider provider){}
    public void  _deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {}

    @Override
    public final void deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {

        if(nbt.contains("uuid")) uuid = nbt.getUUID("uuid");
        else                     uuid = UUID.randomUUID();

        tab = nbt.getString("tab");
        cost = nbt.getInt("cost");
        limit = nbt.getLong("limit");
        currency = nbt.getString("currency");
        toSell = nbt.getBoolean("toSell");

        _deserializeNBT(nbt, provider);
    }
}
