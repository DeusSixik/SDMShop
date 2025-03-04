package net.sixik.sdmshoprework.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.api.shop.*;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.common.shop.sellerType.MoneySellerType;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class ShopSerializerHandler {


    public static List<CompoundTag> serializeShopTabs(ShopBase shop, int bit) {
        List<CompoundTag> list = new ArrayList<>();
        for (ShopTab shopTab : shop.getShopTabs()) {
            list.add(shopTab.serializeNBT(bit));
        }
        return list;
    }

    /**
     * @param bit {@link SDMSerializeParam}
     */
    public static CompoundTag serializeShopEntry(AbstractShopEntry entry, int bit) {
        CompoundTag nbt = new CompoundTag();

        if((bit & SDMSerializeParam.SERIALIZE_PARAMS) != 0 ) {
            nbt.putUUID("entryUUID", entry.entryUUID);
            CompoundTag d = new CompoundTag();
            entry.icon.save(d);
            nbt.put("icon", d);
            nbt.putLong("entryPrice", entry.entryPrice);
            nbt.putInt("entryCount", entry.entryCount);
            if(!entry.title.isEmpty())
                nbt.putString("title", entry.title);
            nbt.putBoolean("isSell", entry.isSell);
            if(entry.getEntryType() != null)
                nbt.put("entryType", entry.getEntryType().serializeNBT());

            if(!entry.descriptionList.isEmpty()) {
                ListTag tagDescription = new ListTag();
                for (String s : entry.descriptionList) {
                    tagDescription.add(StringTag.valueOf(s));
                }
                nbt.put("description", tagDescription);
            }

            nbt.put("shopSeller", entry.shopSellerType.serializeNBT());
        }

        if((bit & SDMSerializeParam.SERIALIZE_LIMIT) != 0 ) {
            if(entry.limit != 0) {
                nbt.putInt("limit", entry.limit);
                nbt.putBoolean("globalLimit", entry.globalLimit);
            }
        }

        if((bit & SDMSerializeParam.SERIALIZE_CONDITIONS) != 0 ) {
            if(!entry.getEntryConditions().isEmpty()) {
                ListTag tagEntryCondition = new ListTag();
                for (AbstractShopEntryCondition entryCondition : entry.getEntryConditions()) {
                    tagEntryCondition.add(entryCondition.serializeNBT());
                }
                nbt.put("entryCondition", tagEntryCondition);
            }
        }

        return nbt;
    }

    /**
     * @param bit {@link SDMSerializeParam}
     */
    public static void deserializeShopEntry(AbstractShopEntry entry, CompoundTag nbt, int bit) {
        if((bit & SDMSerializeParam.SERIALIZE_PARAMS) != 0 ) {
            entry.entryPrice = nbt.getLong("entryPrice");
            entry.entryCount = nbt.getInt("entryCount");

            if(nbt.contains("title"))
                entry.title = nbt.getString("title");

            entry.entryUUID = nbt.getUUID("entryUUID");
            entry.icon = ItemStack.of(nbt.getCompound("icon"));
            entry.isSell = nbt.getBoolean("isSell");

            entry.descriptionList.clear();
            if(nbt.contains("description")) {
                ListTag tagDescription = nbt.getList("description", 8);
                for (Tag tag : tagDescription) {
                    entry.descriptionList.add(tag.getAsString());
                }
            }

            if(nbt.contains("entryType"))
                entry.setEntryType(AbstractShopEntryType.from(nbt.getCompound("entryType")));

            if(nbt.contains("shopSeller")) {
                CompoundTag d = nbt.getCompound("shopSeller");
                entry.sellerTypeID = d.getString("shopSellerTypeID");
                IConstructor<AbstractShopSellerType<?>> cons = ShopContentRegister.SELLER_TYPES.getOrDefault(entry.sellerTypeID, new MoneySellerType.Constructor());

                entry.shopSellerType = cons.createDefaultInstance();
                entry.shopSellerType.deserializeNBT(d);
            }
            else {
                entry.shopSellerType = new MoneySellerType();
                entry.sellerTypeID = entry.shopSellerType.getId();
            }
        }

        if((bit & SDMSerializeParam.SERIALIZE_LIMIT) != 0 ) {
            if(nbt.contains("limit")) {
                entry.limit = nbt.getInt("limit");
                entry.globalLimit = nbt.getBoolean("globalLimit");
            }
        }

        if((bit & SDMSerializeParam.SERIALIZE_CONDITIONS) != 0 ) {
            entry.getEntryConditions().clear();
            if(nbt.contains("entryCondition")) {
                ListTag tagEntryCondition = nbt.getList("entryCondition", 10);
                for (int i = 0; i < tagEntryCondition.size(); i++) {
                    AbstractShopEntryCondition condition = AbstractShopEntryCondition.from(tagEntryCondition.getCompound(i));
                    if (condition == null) continue;
                    entry.getEntryConditions().add(condition);
                }
            }
        }
    }

    /**
     * @param bit {@link SDMSerializeParam}
    */
    public static CompoundTag serializeShopTab(AbstractShopTab tab, int bit) {
        CompoundTag nbt = new CompoundTag();

        if((bit & SDMSerializeParam.SERIALIZE_PARAMS) != 0 ) {
            NBTUtils.putItemStack(nbt, "icon", tab.icon);
            nbt.putUUID("shopTabUUID", tab.shopTabUUID);
            nbt.putString("title", tab.title.getString());
            if(!tab.descriptionList.isEmpty()) {
                ListTag tagDescription = new ListTag();
                for (String s : tab.descriptionList) {
                    tagDescription.add(StringTag.valueOf(s));
                }
                nbt.put("description", tagDescription);
            }
        }

        if(((bit & SDMSerializeParam.SERIALIZE_LIMIT) != 0) ) {
            if(tab.limit != 0) {
                nbt.putInt("limit", tab.limit);
                nbt.putBoolean("globalLimit", tab.globalLimit);
            }
        }

        if(((bit & SDMSerializeParam.SERIALIZE_CONDITIONS) != 0) ) {
            if(!tab.getTabConditions().isEmpty()) {
                ListTag tagTabConditions = new ListTag();
                for (AbstractShopEntryCondition tabCondition : tab.getTabConditions()) {
                    tagTabConditions.add(tabCondition.serializeNBT());
                }
                nbt.put("tabCondition", tagTabConditions);
            }
        }

        if(((bit & SDMSerializeParam.SERIALIZE_ENTRIES) != 0) ) {
            ListTag tagTabEntries = new ListTag();
            for (AbstractShopEntry shopEntry : tab.getTabEntry()) {
                tagTabEntries.add(shopEntry.serializeNBT());
            }
            nbt.put("tabEntry", tagTabEntries);
        }

        return nbt;
    }


    /**
     * @param bit {@link SDMSerializeParam}
     */
    public static void deserializeShopTab(AbstractShopTab tab, CompoundTag nbt, int bit) {

        if((bit & SDMSerializeParam.SERIALIZE_PARAMS) != 0 ) {
            tab.shopTabUUID = nbt.getUUID("shopTabUUID");
            tab.title = Component.translatable(nbt.getString("title"));
            tab.icon = NBTUtils.getItemStack(nbt, "icon");

            tab.descriptionList.clear();
            if(nbt.contains("description")) {
                ListTag tagDescription = nbt.getList("description", 8);
                for (Tag tag : tagDescription) {
                    tab.descriptionList.add(tag.getAsString());
                }
            }
        }
        
        if((bit & SDMSerializeParam.SERIALIZE_LIMIT) != 0 ) {
            if(nbt.contains("limit")) {
                tab.limit = nbt.getInt("limit");
                tab.globalLimit = nbt.getBoolean("globalLimit");
            }
        }

        if((bit & SDMSerializeParam.SERIALIZE_CONDITIONS) != 0 ) {
            tab.getTabConditions().clear();
            if(nbt.contains("tabCondition")) {
                ListTag tagTabConditions = nbt.getList("tabCondition", 10);
                for (int i = 0; i < tagTabConditions.size(); i++) {
                    AbstractShopEntryCondition condition = AbstractShopEntryCondition.from(tagTabConditions.getCompound(i));
                    if(condition == null) continue;
                    tab.getTabConditions().add(condition);
                }
            }
        }
        
        if((bit & SDMSerializeParam.SERIALIZE_ENTRIES) != 0 ) {
            tab.getTabEntry().clear();
            if(nbt.contains("tabEntry")) {
                ListTag tagEntries = nbt.getList("tabEntry", 10);
                for (Tag tagEntry : tagEntries) {
                    ShopEntry shopEntry = new ShopEntry(tab);
                    shopEntry.deserializeNBT((CompoundTag) tagEntry);
                    tab.getTabEntry().add(shopEntry);
                }
            }
        }
    }

    public static List<CompoundTag> serializeTabEntry(ShopTab tab) {
        List<CompoundTag> list = new ArrayList<>();
        for (AbstractShopEntry entry : tab.getTabEntry()) {
            list.add(entry.serializeNBT());
        }
        return list;
    }

    public static List<AbstractShopEntry> deserializeTabEntry(List<CompoundTag> list, AbstractShopTab tab) {
        List<AbstractShopEntry> entries = new ArrayList<>();
        for (CompoundTag tag : list) {
            var optional = ShopEntry.create(tab, tag);
            if(optional.isEmpty()) continue;
            entries.add(optional.get());
        }
        return entries;
    }
}
