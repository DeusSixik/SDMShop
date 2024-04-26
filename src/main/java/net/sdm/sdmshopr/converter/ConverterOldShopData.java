package net.sdm.sdmshopr.converter;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IShopCondition;
import net.sdm.sdmshopr.client.TabButton;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.condition.GameStagesCondition;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.entry.type.ItemEntryType;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConverterOldShopData {


    @Nullable
    public static CompoundTag convertToNewData(){
        SNBTCompoundTag nbt = SNBT.read(SDMShopR.getFile());
        if(nbt == null) return null;
        if(nbt.contains("sdmversion") || !nbt.contains("group")) return null;

        ListTag d1 = (ListTag) nbt.get("tabs");
        List<ShopTab> tabList = new ArrayList<>();

        Shop shop = new Shop();
        shop.shopTabs = tabList;
        for (Tag tag : d1) {
            ShopTab f1 = convertShopTab(SNBTCompoundTag.of(tag), shop);
            if(f1 != null) tabList.add(f1);
        }

        return shop.serializeNBT();
    }

    @Nullable
    public static ShopTab convertShopTab(SNBTCompoundTag nbt, Shop shop){
        try {
            String title = nbt.getString("title");
            ItemStack icon = NBTUtils.read(nbt, "icon");
            String stage = nbt.getString("stage");

            ShopTab tab = new ShopTab(shop);
            tab.icon = icon;
            tab.title = new TranslatableComponent(title);
            if(!stage.isEmpty()) {
                for (IShopCondition condition : tab.conditions) {
                    if (condition.getClass().equals(GameStagesCondition.class)) {
                        ((GameStagesCondition)condition).stages.add(stage);
                        break;
                    }
                }
            }

            List<ShopEntry<?>> shopEntryList = new ArrayList<>();

            ListTag entries = (ListTag) nbt.get("entries");
            for (Tag entry : entries) {
                ShopEntry<ItemEntryType> f1 = convertShopEntry(SNBTCompoundTag.of(entry), tab);
                if (f1 != null) shopEntryList.add(f1);
            }
            tab.shopEntryList = shopEntryList;

            return tab;

        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static ShopEntry<ItemEntryType> convertShopEntry(SNBTCompoundTag nbt, ShopTab tab){
        try {
            String stage = nbt.getString("stage");
            boolean isSell = nbt.getBoolean("selling");
            long price = nbt.getLong("price");
            ItemStack item = NBTUtils.read(nbt, "item");

            ShopEntry<ItemEntryType> d1 = new ShopEntry<>(tab);
            d1.tittle = "";
            d1.isSell = isSell;
            d1.price = (int) price;
            d1.count = item.getCount();
            if(!stage.isEmpty()) {
                for (IShopCondition condition : d1.conditions) {
                    if (condition.getClass().equals(GameStagesCondition.class)) {
                        ((GameStagesCondition)condition).stages.add(stage);
                        break;
                    }
                }
            }

            ItemStack f1 = item.copy();
            f1.setCount(1);
            d1.type = ItemEntryType.of(f1);

            return d1;
        } catch (Exception e){
            return null;
        }
    }
}
