package net.sdm.sdmshoprework.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sdm.sdmshoprework.SDMShopRework;

public class ItemsRegister {

    public static final DeferredRegister<Item> ITEMS;
    public static final RegistryObject<CustomIconItem> CUSTOM_ICON;


    static {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SDMShopRework.MODID);
        CUSTOM_ICON = ITEMS.register("custom_icon", CustomIconItem::new);
    }
}
