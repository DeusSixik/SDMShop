package net.sixik.sdmshoprework.common.shop;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;

import java.util.Optional;

public class ShopTab extends AbstractShopTab {

    public ShopTab(ShopBase shop) {
        super(shop);
    }

    public static Optional<ShopTab> create(ShopBase shop, CompoundTag nbt) {
        try {
            ShopTab tab = new ShopTab(shop);
            tab.deserializeNBT(nbt);
            return Optional.of(tab);
        } catch (Exception e) {
            SDMShopRework.printStackTrace("Failed to create shop tab: ", e);
            return Optional.empty();
        }
    }
}
