package net.sixik.sdmshoprework.common.shop;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;

import java.util.Optional;

public class ShopEntry extends AbstractShopEntry {
    public ShopEntry(AbstractShopTab shopTab) {
        super(shopTab);
    }

    public static Optional<ShopEntry> create(AbstractShopTab shopTab, CompoundTag nbt) {
        try {
            ShopEntry entry = new ShopEntry(shopTab);
            entry.deserializeNBT(nbt);
            return Optional.of(entry);
        } catch (Exception e) {
            SDMShopRework.printStackTrace("Failed to create shop entry: ", e);
        }

        return Optional.empty();
    }
}
