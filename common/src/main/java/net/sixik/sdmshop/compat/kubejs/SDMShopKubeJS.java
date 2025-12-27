package net.sixik.sdmshop.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.sixik.sdmshop.api.ShopEvents;
import net.sixik.sdmshop.compat.kubejs.events.*;

public class SDMShopKubeJS extends KubeJSPlugin {

    public static void initPlugin() {
        ShopEvents.ENTRY_BUY_EVENT.register(((base, entry, tab, player, count) -> {
            ShopJSEvents.BUY_ENTRY.post(new ShopEventEntryBuyJS(base, entry, tab, player, count));
        }));
        ShopEvents.ENTRY_SELL_EVENT.register(((base, entry, tab, player, count) -> {
            ShopJSEvents.SELL_ENTRY.post(new ShopEventEntrySellJS(base, entry, tab, player, count));
        }));
        ShopEvents.SHOP_CHANGE_EVENT.register(((base) -> {
            ShopJSEvents.SHOP_CHANGE.post(new ShopEventChangeJS(base));
        }));
        ShopEvents.ENTRY_ADD_EVENT.register(((shop, entry, tab) -> {
            ShopJSEvents.ENTRY_ADD.post(new ShopEventEntryAddJS(shop, entry, tab));
        }));
        ShopEvents.ENTRY_REMOVE_EVENT.register(((shop, entry, tab) -> {
            ShopJSEvents.ENTRY_REMOVE.post(new ShopEventEntryRemoveJS(shop, entry, tab));
        }));
        ShopEvents.ENTRY_CHANGE_EVENT.register(((shop, entry, tab) -> {
            ShopJSEvents.ENTRY_CHANGE.post(new ShopEventEntryChangeJS(shop, entry, tab));
        }));
        ShopEvents.TAB_ADD_EVENT.register(((shop, tab) -> {
            ShopJSEvents.TAB_ADD.post(new ShopEventTabAddJS(shop, tab));
        }));
        ShopEvents.TAB_REMOVE_EVENT.register(((shop, tab) -> {
            ShopJSEvents.TAB_REMOVE.post(new ShopEventTabRemoveJS(shop, tab));
        }));
        ShopEvents.TAB_CHANGE_EVENT.register(((shop, tab) -> {
            ShopJSEvents.TAB_CHANGE.post(new ShopEventTabChangeJS(shop, tab));
        }));
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        if(event.getType().isServer()) {
            event.add("SDMShop", ShopServerJS.class);
        }

        if(event.getType().isClient()) {
            event.add("SDMShopClient", ShopClientJS.class);
        }
    }

    @Override
    public void registerEvents() {
        ShopJSEvents.GROUP.register();
    }
}
