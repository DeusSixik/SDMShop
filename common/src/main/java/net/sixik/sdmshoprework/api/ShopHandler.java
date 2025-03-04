package net.sixik.sdmshoprework.api;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopTab;

import java.util.Optional;
import java.util.UUID;

public class ShopHandler {

    public static Optional<ShopEntry> createShopEntry(CompoundTag nbt, AbstractShopTab tab) {
        return ShopEntry.create(tab, nbt);
    }

    public static Optional<ShopTab> createShopTab(CompoundTag nbt, boolean isClient) {
        if(isClient) {
            return ShopTab.create(ShopBase.CLIENT, nbt);
        }

        return ShopTab.create(ShopBase.SERVER, nbt);
    }

    public static Optional<ShopTab> getShopTab(UUID uuid, boolean isClient) {
        if(isClient) {
            return ShopBase.CLIENT.getShopTabs().stream().filter(s -> s.shopTabUUID.equals(uuid)).findFirst();
        }
        else {
            return ShopBase.SERVER.getShopTabs().stream().filter(s -> s.shopTabUUID.equals(uuid)).findFirst();
        }
    }

    public static Optional<AbstractShopEntry> getShopEntry(UUID uuid, boolean isClient) {
        if(isClient) {
            for (ShopTab shopTab : ShopBase.CLIENT.getShopTabs()) {
                Optional<AbstractShopEntry> obj = shopTab.getTabEntry().stream().filter(s -> s.entryUUID.equals(uuid)).findFirst();
                if(obj.isPresent()) {
                    return obj;
                }
            }
        }
        else {
            for (ShopTab shopTab : ShopBase.SERVER.getShopTabs()) {
                Optional<AbstractShopEntry> obj = shopTab.getTabEntry().stream().filter(s -> s.entryUUID.equals(uuid)).findFirst();
                if(obj.isPresent()) {
                    return obj;
                }
            }
        }
        return Optional.empty();
    }
}
