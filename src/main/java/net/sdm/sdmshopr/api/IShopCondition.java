package net.sdm.sdmshopr.api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IShopCondition {

    IShopCondition create();
    IShopCondition copy();
    boolean isLocked();
    void getConfig(ConfigGroup config);
    default String getModID(){
        return "minecraft";
    }
    String getID();

    void serializeNBT(CompoundTag nbt);
    void deserializeNBT(CompoundTag nbt);
}
