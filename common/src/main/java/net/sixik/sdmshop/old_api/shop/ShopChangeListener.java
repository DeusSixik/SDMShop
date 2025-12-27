package net.sixik.sdmshop.old_api.shop;

import net.sixik.sdmshop.shop.BaseShop;

@FunctionalInterface
public interface ShopChangeListener {

    void onShopChange(final BaseShop shop);
}
