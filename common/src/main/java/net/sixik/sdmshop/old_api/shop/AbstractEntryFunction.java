package net.sixik.sdmshop.old_api.shop;

import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.ModObjectIdentifier;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public abstract class AbstractEntryFunction implements ModObjectIdentifier, ConfigSupport, DataSerializerCompoundTag, ShopObject {


    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.ENTRY_FUNCTION;
    }
}
