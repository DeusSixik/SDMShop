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
import net.sixik.sdmshoprework.common.data.limiter.LimiterData;
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

    public int limit = 0;
    public boolean globalLimit = false;

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

    public void createShopEntry(CompoundTag nbt) {
        AbstractShopEntry entry = new ShopEntry(this);
        entry.deserializeNBT(nbt);
        tabEntry.add(entry);
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

    public boolean removeEntry(UUID uuid) {
       var it = getTabEntry().iterator();
       while (it.hasNext()) {
           AbstractShopEntry entry = it.next();
           if(entry.entryUUID.equals(uuid)) {
               it.remove();
               return true;
           }
       }
       return false;
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

        config.addInt("limit", limit, v -> limit = v, 0, 0, Integer.MAX_VALUE);
        config.addBool("globalLimit", globalLimit, v -> globalLimit = v, false);

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

        if(limit != 0) {
            nbt.putInt("limit", limit);
            nbt.putBoolean("globalLimit", globalLimit);
        }

        ListTag tagTabEntries = new ListTag();
        for (AbstractShopEntry shopEntry : tabEntry) {
            tagTabEntries.add(shopEntry.serializeNBT());
        }
        nbt.put("tabEntry", tagTabEntries);

        if(!tabConditions.isEmpty()) {
            ListTag tagTabConditions = new ListTag();
            for (AbstractShopEntryCondition tabCondition : tabConditions) {
                tagTabConditions.add(tabCondition.serializeNBT());
            }
            nbt.put("tabCondition", tagTabConditions);
        }

        if(!descriptionList.isEmpty()) {
            ListTag tagDescription = new ListTag();
            for (String s : descriptionList) {
                tagDescription.add(StringTag.valueOf(s));
            }
            nbt.put("description", tagDescription);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.shopTabUUID = nbt.getUUID("shopTabUUID");
        this.title = Component.translatable(nbt.getString("title"));
        this.icon = NBTUtils.getItemStack(nbt, "icon");

        if(nbt.contains("limit")) {
            this.limit = nbt.getInt("limit");
            this.globalLimit = nbt.getBoolean("globalLimit");
        }

        this.tabConditions.clear();
        if(nbt.contains("tabCondition")) {
            ListTag tagTabConditions = nbt.getList("tabCondition", 10);
            for (int i = 0; i < tagTabConditions.size(); i++) {
                AbstractShopEntryCondition condition = AbstractShopEntryCondition.from(tagTabConditions.getCompound(i));
                if(condition == null) continue;
                this.tabConditions.add(condition);
            }
        }

        this.tabEntry.clear();
        if(nbt.contains("tabEntry")) {
            ListTag tagEntries = nbt.getList("tabEntry", 10);
            for (Tag tagEntry : tagEntries) {
                ShopEntry shopEntry = new ShopEntry(this);
                shopEntry.deserializeNBT((CompoundTag) tagEntry);
                this.tabEntry.add(shopEntry);
            }
        }

        this.descriptionList.clear();
        if(nbt.contains("description")) {
            ListTag tagDescription = nbt.getList("description", 8);
            for (Tag tag : tagDescription) {
                this.descriptionList.add(tag.getAsString());
            }
        }
    }

    public boolean isLocked() {
        if (SDMShopR.isEditMode()) return false;

        if(limit != 0 && LimiterData.CLIENT.TAB_DATA.getOrDefault(shopTabUUID,0) >= limit ) return true;

        for (AbstractShopEntryCondition tabCondition : tabConditions) {
            if(tabCondition.isLocked()) return true;
        }

        return false;
    }
}
