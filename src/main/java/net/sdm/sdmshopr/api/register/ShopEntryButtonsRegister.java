package net.sdm.sdmshopr.api.register;

import dev.ftb.mods.ftblibrary.config.NameMap;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.api.customization.APIShopEntry;
import net.sdm.sdmshopr.api.customization.APIShopEntryButton;
import net.sdm.sdmshopr.client.EntryButton;
import net.sdm.sdmshopr.customization.buttons.LegacyEntry;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ShopEntryButtonsRegister {
    Map<String, APIShopEntry> TYPES = new LinkedHashMap();

    static APIShopEntry register(APIShopEntry provider) {
        return (APIShopEntry) TYPES.computeIfAbsent(provider.getID(), (id) -> {
            return provider;
        });
    }

    APIShopEntry BASE = register(new APIShopEntry());
    APIShopEntry LEGACY = register(new LegacyEntry());

    static void init(){
    }
}
