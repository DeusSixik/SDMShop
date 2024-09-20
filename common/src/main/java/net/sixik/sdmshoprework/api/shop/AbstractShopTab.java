package net.sixik.sdmshoprework.api.shop;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractShopTab implements INBTSerializable<CompoundTag> {

    public UUID shopTabUUID;

    private ShopBase shop;

    public Component title = Component.empty();

    public ItemStack icon = Items.BARRIER.getDefaultInstance();
    private final List<AbstractShopEntry> tabEntry = new ArrayList<>();
    private final List<AbstractShopEntryLimiter> tabEntryLimits = new ArrayList<>();
    private final List<AbstractShopEntryCondition> tabConditions = new ArrayList<>();

    public List<String> descriptionList = new ArrayList<>();

    public AbstractShopTab(ShopBase shop){
        this.shop = shop;
        this.shopTabUUID = UUID.randomUUID();

        for (IConstructor<AbstractShopEntryCondition> value : ShopContentRegister.SHOP_ENTRY_CONDITIONS.values()) {
            AbstractShopEntryCondition condition = value.createDefaultInstance();
            if(Platform.isModLoaded(condition.getModId())) {
                tabConditions.add(condition);
            }
        }
    }

    public ShopBase getShop() {
        return shop;
    }

    public List<AbstractShopEntryLimiter> getTabEntryLimits() {
        return tabEntryLimits;
    }

    public List<AbstractShopEntryCondition> getTabConditions() {
        return tabConditions;
    }

    public List<AbstractShopEntry> getTabEntry() {
        return tabEntry;
    }

    public AbstractShopEntry getShopEntry(UUID uuid){
        for (AbstractShopEntry shopEntry : tabEntry) {
            if(Objects.equals(shopEntry.entryUUID, uuid))
                return shopEntry;
        }
        return null;
    }

    public void getConfig(ConfigGroup config){
        TooltipList list = new TooltipList();
        list.add(Component.translatable("sdmr.shop.tab.title.info"));

        config.addString("title", title.getString(), v -> title = Component.translatable(v), "");

        config.add("icon", new ConfigIconItemStack(), icon, v -> icon = v, Items.BARRIER.getDefaultInstance());

        config.addList("description", descriptionList, new StringConfig(null), "");


        ConfigGroup group = config.getOrCreateSubgroup("dependencies");

        for (AbstractShopEntryCondition tabCondition : tabConditions) {
            tabCondition.getConfig(group);
        }


    }

    public int getIndex() {
        return shop.getShopTabs().indexOf(this);
    }

    public Icon getIcon(){
        if(icon.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(icon);
        }
        return ItemIcon.getItemIcon(icon);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        NBTUtils.putItemStack(nbt, "icon", icon);
        nbt.putUUID("shopTabUUID", shopTabUUID);
        nbt.putString("title", title.getString());
        ListTag tagTabEntries = new ListTag();
        for (AbstractShopEntry shopEntry : tabEntry) {
            tagTabEntries.add(shopEntry.serializeNBT());
        }
        nbt.put("tabEntry", tagTabEntries);

        ListTag tagTabEntryLimits = new ListTag();
        for (AbstractShopEntryLimiter tabEntryLimit : tabEntryLimits) {
            tagTabEntryLimits.add(tabEntryLimit.serializeNBT());
        }
        nbt.put("tabLimit", tagTabEntryLimits);
        ListTag tagTabConditions = new ListTag();
        for (AbstractShopEntryCondition tabCondition : tabConditions) {
            tagTabConditions.add(tabCondition.serializeNBT());
        }
        nbt.put("tabCondition", tagTabConditions);

        ListTag tagDescription = new ListTag();
        for (String s : descriptionList) {
            tagDescription.add(StringTag.valueOf(s));
        }
        nbt.put("description", tagDescription);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.shopTabUUID = nbt.getUUID("shopTabUUID");
        title = Component.translatable(nbt.getString("title"));
        icon = NBTUtils.getItemStack(nbt, "icon");
        ListTag tagTabEntryLimits = nbt.getList("tabLimit", 10);
        tabEntryLimits.clear();
        for (int i = 0; i < tagTabEntryLimits.size(); i++) {
            tabEntryLimits.add(AbstractShopEntryLimiter.from(tagTabEntryLimits.getCompound(i)));
        }

        ListTag tagTabConditions = nbt.getList("tabCondition", 10);
        tabConditions.clear();
        for (int i = 0; i < tagTabConditions.size(); i++) {
            tabConditions.add(AbstractShopEntryCondition.from(tagTabConditions.getCompound(i)));
        }

        ListTag tagEntries = nbt.getList("tabEntry", 10);
        tabEntry.clear();
        for (Tag tagEntry : tagEntries) {
            ShopEntry shopEntry = new ShopEntry(this);
            shopEntry.deserializeNBT((CompoundTag) tagEntry);
            tabEntry.add(shopEntry);
        }

        descriptionList.clear();
        ListTag tagDescription = nbt.getList("description", 8);
        for (Tag tag : tagDescription) {
            descriptionList.add(tag.getAsString());
        }
    }

    public boolean isLocked() {
        if (SDMShopR.isEditMode()) return false;

        for (AbstractShopEntryCondition tabCondition : tabConditions) {
            if(tabCondition.isLocked()) return true;
        }

        return false;
    }
}
