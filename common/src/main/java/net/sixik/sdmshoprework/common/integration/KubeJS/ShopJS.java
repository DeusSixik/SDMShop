package net.sixik.sdmshoprework.common.integration.KubeJS;

import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;

import java.util.UUID;

public interface ShopJS {

    default void setMoney(Player player, long count) {
        SDMShopR.setMoney(player, count);
    }

    default void addMoney(Player player, long count) {
        SDMShopR.addMoney(player, count);
    }

    default long getMoney(Player player) {
        return SDMShopR.getMoney(player);
    }

    default void buyEntry(Player player, String entryID, int count, boolean takeMoney) {
        UUID uuid = UUID.fromString(entryID);

        for (ShopTab shopTab : ShopBase.SERVER.getShopTabs()) {
            AbstractShopEntry entry = shopTab.getShopEntry(uuid);
            if(entry == null) continue;

            if(entry.isSell) return;

            if(!takeMoney) {
                long money = entry.entryPrice * count;
                addMoney(player, money);
            }
            entry.getEntryType().buy(player, count, entry);
            return;
        }
    }

    default AbstractShopTab getShopTab(String tabID) {
        UUID uuid = UUID.fromString(tabID);
        return ShopBase.SERVER.getShopTab(uuid);
    }

    default AbstractShopEntry getShopEntry(String entryID) {
        UUID uuid = UUID.fromString(entryID);
        for (ShopTab shopTab : ShopBase.SERVER.getShopTabs()) {
            AbstractShopEntry entry = shopTab.getShopEntry(uuid);
            if(entry != null) return entry;
        }

        return null;
    }
}
