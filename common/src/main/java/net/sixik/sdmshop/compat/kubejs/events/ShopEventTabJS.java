package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventTabJS extends EventJS {

    private final ShopBase shop;
    private final ShopTab tab;

    public ShopEventTabJS(final ShopBase shop, final ShopTab tab) {
        this.shop = shop;
        this.tab = tab;
    }

    public ShopBase getShop() {
        return shop;
    }

    public ShopTab getTab() {
        return tab;
    }
}
