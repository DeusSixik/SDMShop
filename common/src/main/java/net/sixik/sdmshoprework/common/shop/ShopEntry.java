package net.sixik.sdmshoprework.common.shop;

import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;

public class ShopEntry extends AbstractShopEntry {
    public ShopEntry(AbstractShopTab shopTab) {
        super(shopTab);
    }


    public ShopEntry copy() {
        ShopEntry entry = new ShopEntry(getShopTab());
        entry.entryCount = this.entryCount;
        entry.descriptionList = this.descriptionList;
        entry.entryPrice = this.entryPrice;
        entry.title = this.title;
        entry.setEntryType(getEntryType().copy());
        entry.isSell = this.isSell;
        entry.icon = this.icon;
        entry.getEntryConditions().addAll(getEntryConditions());
        return  entry;
    }
}
