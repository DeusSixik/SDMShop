package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.shop.ShopEntry;

public class ShopEntrySellEventJS extends PlayerEventJS {

    private final Player player;
    private final ShopEntry entry;
    private int count;

    public ShopEntrySellEventJS(Player player, ShopEntry entry, int count) {
        this.player = player;
        this.entry = entry;
        this.count = count;
    }

    public ShopEntry getEntry() {
        return entry;
    }

    public int getCount() {
        return count;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public void setPrice(double price) {
        entry.setPrice(price);
    }

    public double getPrice() {
        return entry.getPrice();
    }

    public void setCount(int count) {
        this.count = count;
    }
}

