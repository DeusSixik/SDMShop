package net.sixik.sdmshop.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.sixik.sdmshop.compat.kubejs.events.ShopEntryBuyEventJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEntrySellEventJS;

public interface ShopJSEvents {

    EventGroup GROUP = EventGroup.of("SDMShopEvents");
    EventHandler BUY_ENTRY = GROUP.server("buyEntry", () -> ShopEntryBuyEventJS.class);
    EventHandler SELL_ENTRY = GROUP.server("sellEntry", () -> ShopEntrySellEventJS.class);
}
