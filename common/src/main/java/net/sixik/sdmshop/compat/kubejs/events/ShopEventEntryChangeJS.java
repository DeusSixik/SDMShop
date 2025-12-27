package net.sixik.sdmshop.compat.kubejs.events;

import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventEntryChangeJS extends ShopEventEntryJS{

    public ShopEventEntryChangeJS(ShopBase shop, ShopEntry entry, ShopTab tab) {
        super(shop, entry, tab);
    }
}
