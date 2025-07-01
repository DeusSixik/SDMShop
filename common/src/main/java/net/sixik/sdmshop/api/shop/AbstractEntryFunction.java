package net.sixik.sdmshop.api.shop;

import net.sixik.sdmshop.api.ConfigSupport;
import net.sixik.sdmshop.api.ModObjectIdentifier;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public abstract class AbstractEntryFunction implements ModObjectIdentifier, ConfigSupport, DataSerializerCompoundTag, ShopObject {


    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.ENTRY_FUNCTION;
    }
}
