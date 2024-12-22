package net.sixk.sdmshop.api;

import net.minecraft.core.HolderLookup;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;

public interface IItemSerializer {

    KeyData serialize(HolderLookup.Provider provider);



    void deserialize(KeyData var1, HolderLookup.Provider provider);
}
