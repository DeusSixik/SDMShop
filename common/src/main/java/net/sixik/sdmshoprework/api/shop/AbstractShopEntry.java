package net.sixik.sdmshoprework.api.shop;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import jdk.jfr.Description;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.common.icon.ShopItemIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractShopEntry {

    public UUID entryUUID;

    public String title = "";

    public long entryPrice = 0;
    public int entryCount = 1;

    public boolean isSell = false;

//    @Deprecated
//    @Description("I haven't figured out the best way to do it yet")
//    public AbstractShopIcon entryIcon = new ShopItemIcon(Items.BARRIER.getDefaultInstance());

    public ItemStack icon = Items.BARRIER.getDefaultInstance();

    public List<String> descriptionList = new ArrayList<>();

    private AbstractShopEntryType entryType = null;
    private AbstractShopTab shopTab;

    private final List<AbstractShopEntryLimiter> entryLimiters = new ArrayList<>();
    private final List<AbstractShopEntryCondition> entryConditions = new ArrayList<>();


    public AbstractShopEntry(AbstractShopTab shopTab) {
        this.shopTab = shopTab;
        this.entryUUID = UUID.randomUUID();

        for (IConstructor<AbstractShopEntryCondition> value : ShopContentRegister.SHOP_ENTRY_CONDITIONS.values()) {
            AbstractShopEntryCondition condition = value.createDefaultInstance();
            if(Platform.isModLoaded(condition.getModId())) {
                entryConditions.add(condition);
            }
        }
    }

    public void setEntryPrice(long entryPrice, int entryCount){
        this.entryPrice = entryPrice;
        this.entryCount = entryCount;
    }

    public void setEntryType(AbstractShopEntryType entryType) {
        this.entryType = entryType;
        this.entryType.setShopEntry(this);
    }

//    public void setEntryIcon(AbstractShopIcon entryIcon) {
//        this.entryIcon = entryIcon;
//    }

    public AbstractShopTab getShopTab() {
        return shopTab;
    }

    public AbstractShopEntryType getEntryType() {
        return entryType;
    }

    public List<AbstractShopEntryLimiter> getEntryLimiters() {
        return entryLimiters;
    }

    public List<AbstractShopEntryCondition> getEntryConditions() {
        return entryConditions;
    }

    public void getConfig(ConfigGroup config) {

        config.addString("title", title, v -> title = v, "");
        if(entryType.isCountable())
            config.addInt("count", entryCount, v -> entryCount = v, 1, 1, Integer.MAX_VALUE);
        config.addLong("price", entryPrice, v -> entryPrice = v, 1, 0, Long.MAX_VALUE);

        if(entryType.getSellType() == AbstractShopEntryType.SellType.BOTH)
            config.addBool("isSell", isSell, v -> isSell = v, false);

        config.addList("description", descriptionList, new StringConfig(null), "");

       ConfigGroup entryGroup = config.getOrCreateSubgroup("type");
       if(entryType != null) {
           entryType.getConfig(entryGroup);
       }

       ConfigGroup dependenciesGroup = config.getOrCreateSubgroup("dependencies");
        for (AbstractShopEntryCondition entryCondition : entryConditions) {
            entryCondition.getConfig(dependenciesGroup);
        }
    }

    public int getIndex() {
        return shopTab.getTabEntry().indexOf(this);
    }

    public boolean isLocked() {

        for (AbstractShopEntryCondition entryCondition : entryConditions) {
            if(entryCondition.isLocked()) return true;
        }

        return false;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("entryPrice", entryPrice);
        nbt.putInt("entryCount", entryCount);
//        nbt.put("entryIcon", entryIcon.serializeNBT());
        nbt.putUUID("entryUUID", entryUUID);
        CompoundTag d = new CompoundTag();
        icon.save(d);
        nbt.put("icon", d);
        nbt.putString("title", title);
        nbt.putBoolean("isSell", isSell);

        if(entryType != null)
            nbt.put("entryType", entryType.serializeNBT());

        ListTag tagEntryLimiter = new ListTag();
        for (AbstractShopEntryLimiter entryLimiter : entryLimiters) {
            tagEntryLimiter.add(entryLimiter.serializeNBT());
        }
        nbt.put("entryLimiter", tagEntryLimiter);

        ListTag tagEntryCondition = new ListTag();
        for (AbstractShopEntryCondition entryCondition : entryConditions) {
            tagEntryCondition.add(entryCondition.serializeNBT());
        }
        nbt.put("entryCondition", tagEntryCondition);

        ListTag tagDescription = new ListTag();
        for (String s : descriptionList) {
            tagDescription.add(StringTag.valueOf(s));
        }
        nbt.put("description", tagDescription);

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        entryPrice = nbt.getLong("entryPrice");
        entryCount = nbt.getInt("entryCount");
//        entryIcon = ShopItemIcon.from(nbt.getCompound("entryIcon"));
        this.entryUUID = nbt.getUUID("entryUUID");
        this.icon = ItemStack.of(nbt.getCompound("icon"));
        this.title = nbt.getString("title");
        this.isSell = nbt.getBoolean("isSell");

        if(nbt.contains("entryType"))
            setEntryType(AbstractShopEntryType.from(nbt.getCompound("entryType")));

        ListTag tagEntryLimiter = nbt.getList("entryLimiter", 10);
        entryLimiters.clear();
        for (int i = 0; i < tagEntryLimiter.size(); i++) {
            entryLimiters.add(AbstractShopEntryLimiter.from(tagEntryLimiter.getCompound(i)));
        }

        ListTag tagEntryCondition = nbt.getList("entryCondition", 10);
        entryConditions.clear();
        for (int i = 0; i < tagEntryCondition.size(); i++) {
            AbstractShopEntryCondition condition = AbstractShopEntryCondition.from(tagEntryCondition.getCompound(i));
            if(condition == null) continue;
            entryConditions.add(condition);
        }

        descriptionList.clear();
        ListTag tagDescription = nbt.getList("description", 8);
        for (Tag tag : tagDescription) {
            descriptionList.add(tag.getAsString());
        }
    }

    @Override
    public String toString() {
        return "AbstractShopEntry{" +
                "entryUUID=" + entryUUID +
                ", entryPrice=" + entryPrice +
                ", entryCount=" + entryCount +
//                ", entryIcon=" + entryIcon +
                ", entryType=" + entryType +
                ", shopTab=" + shopTab +
                ", entryLimiters=" + entryLimiters +
                ", entryConditions=" + entryConditions +
                '}';
    }
}
