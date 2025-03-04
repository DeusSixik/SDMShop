package net.sixik.sdmshoprework.common.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

public class SerializerControl {

    private static final String VERSION = "1.0.0";

    public static void serializeVersion(CompoundTag nbt) {
        nbt.putString("sdmversion", VERSION);
    }

    public static void deserializeVersion(CompoundTag nbt, ShopBase shop) {
        String version = nbt.getString("sdmversion");
        if (!VERSION.equals(version)) {
            if(version.equals("0.0.1")) {

//                SDMShopRework.LOGGER.info("SDM LOAD !");


                ShopBase shopBase = new ShopBase();
                ListTag tabs = (ListTag) nbt.get("tabs");
                for (Tag tab : tabs) {
                    CompoundTag shopTabData = (CompoundTag) tab;

                    ShopTab shopTab = new ShopTab(shopBase);
                    shopTab.title = Component.translatable(shopTabData.getString("title"));
                    shopTab.icon = NBTUtils.getItemStack(shopTabData, "icon");



                    ListTag entries = (ListTag) shopTabData.get("entries");
                    for (Tag entry : entries) {
                        CompoundTag entryData = (CompoundTag) entry;
                        ShopEntry shopEntry = new ShopEntry(shopTab);
                        shopEntry.entryPrice = entryData.getInt("price");
                        shopEntry.entryCount = entryData.getInt("count");
                        shopEntry.isSell = entryData.getBoolean("isSell");
                        shopEntry.title = entryData.getString("tittle");
                        shopEntry.entryUUID = entryData.getUUID("entryID");

                        AbstractShopEntryType entryType = AbstractShopEntryType.fromOld(entryData.getCompound("type"));
                        if(entryType != null) {
                            shopEntry.setEntryType(entryType);
                        }

                        shopTab.getTabEntry().add(shopEntry);
                    }
                    shopBase.getShopTabs().add(shopTab);

                }

                ShopBase.SERVER.deserializeNBT(shopBase.serializeNBT());


            } else {
//                SDMShopRework.LOGGER.error("Unsupported SDM Shop version: {}. Expected version: 1.0.0", version);
            }
        }
    }

    public static boolean isOldVersion(CompoundTag nbt) {
        if(!nbt.contains("sdmversion")) return true;
        return isOldVersion(nbt.getString("sdmversion"));
    }

    public static boolean isOldVersion(String version) {
        return !VERSION.equals(version);
    }
}
