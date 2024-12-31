package net.sixik.sdmshoprework.common.integration.KubeJS;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.sixik.sdmshoprework.common.integration.KubeJS.events.ShopEntryBuyEventJS;
import net.sixik.sdmshoprework.common.integration.KubeJS.events.ShopEntrySellEventJS;

public interface ShopJSEvents {
    EventGroup GROUP = EventGroup.of("SDMShopEvents");
    EventHandler SELL_ENTRY = GROUP.server("sellEntry", () -> ShopEntrySellEventJS.class);
    EventHandler BUY_ENTRY = GROUP.server("buyEntry", () -> ShopEntryBuyEventJS.class);
}
