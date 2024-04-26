package net.sdm.sdmshopr.api.customization;

import dev.ftb.mods.ftblibrary.ui.Panel;
import net.sdm.sdmshopr.client.EntryButton;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

public class APIShopEntry {

    public APIShopEntry(){

    }

    public String getID(){
        return "BASE";
    }

    public APIShopEntryButton create(Panel panel, ShopEntry<?> entry){
        return new EntryButton(panel, entry);
    }
}
