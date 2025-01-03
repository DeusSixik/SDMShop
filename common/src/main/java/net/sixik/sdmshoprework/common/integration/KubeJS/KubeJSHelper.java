package net.sixik.sdmshoprework.common.integration.KubeJS;

import dev.architectury.platform.Platform;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.integration.KubeJS.events.ShopEntryBuyEventJS;
import net.sixik.sdmshoprework.common.integration.KubeJS.events.ShopEntrySellEventJS;

public class KubeJSHelper {

    public static void postEvent(Player player, AbstractShopEntry entry, int count, EventType type) {
        switch (type) {
            case BUY -> ShopJSEvents.BUY_ENTRY.post(new ShopEntryBuyEventJS(player, entry, count));
            case SELL -> ShopJSEvents.SELL_ENTRY.post(new ShopEntrySellEventJS(player, entry, count));
        }

    }


    public enum EventType {
        SELL,
        BUY
    }
}
