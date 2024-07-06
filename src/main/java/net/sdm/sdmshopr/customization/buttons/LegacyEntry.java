package net.sdm.sdmshopr.customization.buttons;

import dev.ftb.mods.ftblibrary.ui.Panel;
import net.sdm.sdmshopr.api.customization.APIShopEntry;
import net.sdm.sdmshopr.api.customization.APIShopEntryButton;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

public class LegacyEntry extends APIShopEntry {

    public LegacyEntry(){

    }

    @Override
    public String getID() {
        return "LEGACY";
    }

    @Override
    public APIShopEntryButton create(Panel panel, ShopEntry<?> entry) {
        return new LegacyEntryButton(panel,entry);
    }
}
