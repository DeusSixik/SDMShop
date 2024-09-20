package net.sixik.sdmshoprework.common.register;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.sixik.sdmshoprework.SDMShopRework;

public class ItemsRegister {

    public static final DeferredRegister<Item> ITEMS;
    public static final RegistrySupplier<CustomIconItem> CUSTOM_ICON;


    static {
        ITEMS = DeferredRegister.create(SDMShopRework.MODID, Registries.ITEM);
        CUSTOM_ICON = ITEMS.register("custom_icon", CustomIconItem::new);
    }
}
