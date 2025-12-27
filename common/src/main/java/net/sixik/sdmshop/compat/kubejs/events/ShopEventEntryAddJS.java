package net.sixik.sdmshop.compat.kubejs.events;

import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventEntryAddJS extends ShopEventEntryJS {

    public ShopEventEntryAddJS(ShopBase shop, ShopEntry entry, ShopTab tab) {
        super(shop, entry, tab);
    }
}
