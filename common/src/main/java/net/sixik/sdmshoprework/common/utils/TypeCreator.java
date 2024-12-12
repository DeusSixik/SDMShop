package net.sixik.sdmshoprework.common.utils;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.network.server.create.SendCreateShopEntryC2S;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeCreator {

    public static List<ContextMenuItem> createContext(AbstractShopScreen screen){
        List<ContextMenuItem> contextMenu = new ArrayList<>();

        for (Map.Entry<String, IConstructor<AbstractShopEntryType>> d1 : ShopContentRegister.SHOP_ENTRY_TYPES.entrySet()) {
            AbstractShopEntryType shopEntryType =  d1.getValue().createDefaultInstance();
            if(Platform.isModLoaded(shopEntryType.getModId()) && SDMShopClient.creator.favoriteCreator.contains(d1.getKey())) {
                contextMenu.add(new ContextMenuItem(shopEntryType.getTranslatableForCreativeMenu(), shopEntryType.getCreativeIcon(), () -> {
                    ShopEntry entry = new ShopEntry(screen.selectedTab);
                    entry.setEntryType(shopEntryType);
                    screen.selectedTab.getTabEntry().add(entry);
                    new SendCreateShopEntryC2S(screen.selectedTab.shopTabUUID, entry.serializeNBT()).sendToServer();
                }));
            }
        }

        contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.creator.contextmenu.info"), Icons.BOOK, screen::openCreateScreen));
        return contextMenu;
    }
}
