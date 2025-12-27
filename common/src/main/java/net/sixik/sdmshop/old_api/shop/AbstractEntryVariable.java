package net.sixik.sdmshop.old_api.shop;

import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.ModObjectIdentifier;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

@Deprecated
public abstract class AbstractEntryVariable implements ModObjectIdentifier, ConfigSupport, DataSerializerCompoundTag, ShopObject {

    /*
     Короче задумка такая. Имея переменные можнно сделать регулировку цен и т.п

     Допустим на сервере 10 игроков и в магазине появляется 2 новых товара
     Если за игровой день купили 20 товаров цена на него выросла и т.п

     */

    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.SHOP_VARIABLE;
    }
}
