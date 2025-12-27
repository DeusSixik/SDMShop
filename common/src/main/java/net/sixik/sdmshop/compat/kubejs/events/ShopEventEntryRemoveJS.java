package net.sixik.sdmshop.compat.kubejs.events;

import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventEntryRemoveJS extends ShopEventEntryJS{

    public ShopEventEntryRemoveJS(ShopBase shop, ShopEntry entry, ShopTab tab) {
        super(shop, entry, tab);
    }
}
