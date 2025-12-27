package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventEntryJS extends EventJS {

    private final ShopBase shop;
    private final ShopEntry entry;
    private final ShopTab tab;

    public ShopEventEntryJS(final ShopBase shop, final ShopEntry entry, final ShopTab tab) {
        this.shop = shop;
        this.entry = entry;
        this.tab = tab;
    }

    public ShopEntry getEntry() {
        return entry;
    }

    public ShopTab getTab() {
        return tab;
    }

    public ShopBase getShop() {
        return shop;
    }
}
