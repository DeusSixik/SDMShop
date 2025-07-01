package net.sixik.sdmshop.compat.kubejs;

import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import org.jetbrains.annotations.Nullable;

public interface ShopClientJS {

    default @Nullable BaseShop getClientShop() {
        return SDMShopClient.CurrentShop;
    }

    default ShopLimiter getLimiter() {
        return SDMShopClient.shopLimiter;
    }
}
