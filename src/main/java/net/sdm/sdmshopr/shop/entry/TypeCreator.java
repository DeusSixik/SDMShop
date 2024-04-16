package net.sdm.sdmshopr.shop.entry;

import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.network.CreateShopEntry;
import net.sdm.sdmshopr.api.EntryTypeRegister;
import net.sdm.sdmshopr.api.IEntryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeCreator {

    public static List<ContextMenuItem> createContext(MainShopScreen screen){
        List<ContextMenuItem> contextMenu = new ArrayList<>();

        for (Map.Entry<String, IEntryType> d1 : EntryTypeRegister.TYPES.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())){
                contextMenu.add(new ContextMenuItem(d1.getValue().getTranslatableForContextMenu(), d1.getValue().getCreativeIcon(), () -> {
                    ShopEntry<IEntryType> create = new ShopEntry<>(screen.selectedTab, d1.getValue().copy(), 1,1,false);
                    screen.selectedTab.shopEntryList.add(create);
                    screen.refreshWidgets();
                    new CreateShopEntry(create).sendToServer();
                }));
            }
        }
        return contextMenu;
    }
}
