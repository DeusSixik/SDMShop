package net.sixk.sdmshop.shop.Tovar;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import net.sixk.sdmshop.api.IItemSerializer;

public abstract class AbstractTovar implements IItemSerializer {

    public abstract void buy(Player player, Tovar tovar, long count);
    public abstract void sell(Player player, Tovar tovar,long count);
    public abstract String getTitel();
    public abstract Icon getIcon();
    public abstract Object getItemStack();
    public abstract TagKey getTag();
    public abstract AbstractTovar copy();
    public abstract String getID();
    public abstract boolean getisXPLVL();
}
