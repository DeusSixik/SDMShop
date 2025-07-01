package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.api.shop.AbstractEntryType;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.entry_types.MissingEntryType;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;

import java.util.*;
import java.util.function.Function;

public class ShopEntryTypeCreator {

    public static Optional<AbstractEntryType> createEntryType(ShopEntry entry, CompoundTag nbt) {
        if(!nbt.contains("type_id")) return Optional.empty();

        String type_id = nbt.getString("type_id");

        try {
            Optional<Function<ShopEntry, AbstractEntryType>> opt = ShopContentRegister.getEntryType(type_id);
            if (opt.isEmpty()) {
                return Optional.of(new MissingEntryType(entry, nbt));
            }

            AbstractEntryType result = opt.get().apply(entry);
            result.deserialize(nbt);
            return Optional.of(result);
        } catch (java.lang.NoClassDefFoundError e) {
            return Optional.of(new MissingEntryType(entry, nbt));
        }
    }

    @Environment(EnvType.CLIENT)
    public static List<ContextMenuItem> createContext(AbstractShopScreen screen) {
        List<ContextMenuItem> contextMenu = new ArrayList<>();

        for (Map.Entry<String, Function<ShopEntry, AbstractEntryType>> entry : ShopContentRegister.getEntryTypes().entrySet()) {
            ShopEntry shopEntry = new ShopEntry(screen.currentShop, UUID.randomUUID(), screen.getCurrentTabUuid(), new MoneySellerType());
            AbstractEntryType entryType = entry.getValue().apply(shopEntry);

            if(entryType.isModLoaded() && SDMShopClient.userData.getCreator().contains(entry.getKey())) {
                contextMenu.add(new ContextMenuItem(entryType.getTranslatableForCreativeMenu(), entryType.getCreativeIcon(), (button -> {
                    ShopUtilsClient.addEntry(screen.currentShop, shopEntry);
                })));
            }
        }

        contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.creator.contextmenu.info"), Icons.BOOK,button -> {
            screen.openCreateEntryScreen();
        }));

        return contextMenu;
    }
}
