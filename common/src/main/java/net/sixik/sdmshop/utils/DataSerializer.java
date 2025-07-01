package net.sixik.sdmshop.utils;

import net.minecraft.nbt.Tag;

public interface DataSerializer<T extends Tag> {

    T serialize();
    void deserialize(T tag);
}
