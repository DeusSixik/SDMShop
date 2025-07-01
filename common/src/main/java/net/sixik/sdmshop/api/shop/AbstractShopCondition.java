package net.sixik.sdmshop.api.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.sixik.sdmshop.api.ModObjectIdentifier;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public abstract class AbstractShopCondition implements DataSerializerCompoundTag, ModObjectIdentifier, ShopObject {

    protected BaseShop shop;


    public final void setShop(BaseShop shop) {
        this.shop = shop;
    }

    public abstract boolean isLocked(ShopObject shopObject);

    public abstract AbstractShopCondition copy();

    public abstract void getConfig(ConfigGroup configGroup);

    public abstract String getId();

    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.SHOP_CONDITION;
    }
}
