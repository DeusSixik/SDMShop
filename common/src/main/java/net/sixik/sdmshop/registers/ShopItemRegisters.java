package net.sixik.sdmshop.registers;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.utils.config.ConfigIconItemStack;

public class ShopItemRegisters {

    public static final DeferredRegister<Item> ITEMS;
    public static final RegistrySupplier<ConfigIconItemStack.CustomIconItem> CUSTOM_ICON;

    static {
        ITEMS = DeferredRegister.create(SDMShop.MODID, Registries.ITEM);
        CUSTOM_ICON = ITEMS.register("custom_icon", ConfigIconItemStack.CustomIconItem::new);
    }
}
