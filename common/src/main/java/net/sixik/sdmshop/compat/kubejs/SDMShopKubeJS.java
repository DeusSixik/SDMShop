package net.sixik.sdmshop.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class SDMShopKubeJS extends KubeJSPlugin {

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
