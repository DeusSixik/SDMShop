package net.sdm.sdmshopr.api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISpecialEntryCondition extends INBTSerializable<CompoundTag> {

    boolean isConditionSuccess();

    void getConfig(ConfigGroup group);

    @Override
    default CompoundTag serializeNBT(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("conditionID", getID());
        return nbt;
    }

    default String getModID(){
        return "minecraft";
    }

    String getID();
}

