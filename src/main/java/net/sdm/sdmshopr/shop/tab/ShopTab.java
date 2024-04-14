package net.sdm.sdmshopr.shop.tab;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class ShopTab implements INBTSerializable<CompoundTag> {
    public Shop shop;
    public Component title = Component.empty();
    public ItemStack icon = ItemStack.EMPTY;
    public int lock = 0;
    public List<ShopEntry<?>> shopEntryList = new ArrayList<>();

    public ShopTab(Shop shop){
        this.shop = shop;
    }


    public CompoundTag serializeSettings(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("title", title.getString());
        NBTUtils.putItemStack(nbt, "icon", icon);
        return nbt;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = serializeSettings();


        ListTag entry = new ListTag();
        for (ShopEntry<?> shopEntry : shopEntryList) {
            entry.add(shopEntry.serializeNBT());
        }
        nbt.put("entries", entry);
        return nbt;
    }

    public void deserializeSettings(CompoundTag nbt){
        title = Component.translatable(nbt.getString("title"));
        icon = NBTUtils.getItemStack(nbt, "icon");
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        shopEntryList.clear();
        deserializeSettings(nbt);

        ListTag entries = (ListTag) nbt.get("entries");
        for (Tag entry : entries) {
            ShopEntry<?> d1 = new ShopEntry<>(this);
            d1.deserializeNBT((CompoundTag) entry);
            shopEntryList.add(d1);
        }

    }

    public int getIndex(){
        return shop.shopTabs.indexOf(this);
    }

    public void getConfig(ConfigGroup config){
        config.addString("title", title.getString(), v -> title = Component.translatable(v), "");
        config.addItemStack("icon", icon, v -> icon = v, ItemStack.EMPTY, true, true);
    }
}
