package net.sixik.sdmshop.compat.kubejs;

import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.compat.SDMShopIntegration;
import net.sixik.sdmshop.compat.kubejs.events.ShopEntryBuyEventJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEntrySellEventJS;
import net.sixik.sdmshop.shop.ShopEntry;

public class ShopKubeJSHelper {

    public static void postEvent(Player player, ShopEntry entry, int count) {
        if(!SDMShopIntegration.isKubeJSLoaded()) return;

        switch (entry.getType()) {
            case Buy -> ShopJSEvents.BUY_ENTRY.post(new ShopEntryBuyEventJS(player, entry, count));
            case Sell -> ShopJSEvents.SELL_ENTRY.post(new ShopEntrySellEventJS(player, entry, count));
        }

    }

}
