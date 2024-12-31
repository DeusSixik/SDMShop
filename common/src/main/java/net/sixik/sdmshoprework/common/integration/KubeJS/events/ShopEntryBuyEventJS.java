package net.sixik.sdmshoprework.common.integration.KubeJS.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;

public class ShopEntryBuyEventJS extends PlayerEventJS {

    private final Player player;
    private final AbstractShopEntry entry;
    private int count;

    public ShopEntryBuyEventJS(Player player, AbstractShopEntry entry, int count) {
        this.player = player;
        this.entry = entry;
        this.count = count;
    }

    public AbstractShopEntry getEntry() {
        return entry;
    }

    public int getCount() {
        return count;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public void setPrice(int price) {
        entry.entryPrice = price;
    }

    public long getPrice() {
        return entry.entryPrice;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
