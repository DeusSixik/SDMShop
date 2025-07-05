package net.sixk.sdmshop.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

public interface DataSerializer<T extends Tag> {

    T serializeNBT(HolderLookup.Provider provider);
    void deserializeNBT(T nbt, HolderLookup.Provider provider);
}
