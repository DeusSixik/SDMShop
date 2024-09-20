package net.sixik.sdmshoprework.api;

public interface INBTSerializable<T> {

    T serializeNBT();
    void deserializeNBT(T nbt);
}
