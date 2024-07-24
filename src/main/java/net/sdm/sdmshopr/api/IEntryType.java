package net.sdm.sdmshopr.api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.customization.APIShopEntryButton;
import net.sdm.sdmshopr.client.EntryButton;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

import java.util.ArrayList;
import java.util.List;

public interface IEntryType extends INBTSerializable<CompoundTag> {

    default boolean isOnlySell() {
        return false;
    }

    default boolean isCanBuy(){
        return true;
    }
    /*
        Can the product be sold
     */
    boolean isSellable();

    /*
        If the product has a quantity, an example of this is item
    */
    boolean isCountable();

    /*
        Checks if the item is hidden, it will not be shown
     */
    default boolean isLocked(){
        return false;
    }

    /*
        The icon that will be displayed in the store
     */
    Icon getIcon();


    /*
        NBT Icon need only if icon is item
     */
    default CompoundTag getIconNBT(){
        return new CompoundTag();
    }

    /*
        Config menu from FTB Library
     */
    void getConfig(ConfigGroup group);

    /*
    The icon that will be displayed in the context menu when adding the product
     */
    Icon getCreativeIcon();

    String getID();

    IEntryType copy();


    Component getTranslatableForContextMenu();

    default List<Component> getDescriptionForContextMenu(){

        return List.of(Component.translatable("sdmr.shop.entry.creator.type." + getID() + ".description"));
    }
    default String getModNameForContextMenu(){
        return "";
    }

    /*
        ID is the mod in which the product will become available
     */
    default String getModID(){
        return "minecraft";
    }

    @Override
    default CompoundTag serializeNBT(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", getID());
        return nbt;
    }

    default Icon getPreviewIcon(){
        if(getCreativeIcon().isEmpty()) return Icons.BARRIER;
        else return getCreativeIcon();
    }

    default void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry){}
    default void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {}

    @OnlyIn(Dist.CLIENT)
    default boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry){
        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }
    @OnlyIn(Dist.CLIENT)
    default int howMany(boolean isSell, ShopEntry<?> entry){
        return 1;
    }
}
