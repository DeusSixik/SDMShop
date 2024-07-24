package net.sdm.sdmshopr.shop.special;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.sdm.sdmshopr.api.ISpecialEntryCondition;

@Deprecated
public class EveryDaySpecialCondition implements ISpecialEntryCondition {

    public int days;


    @Override
    public boolean isConditionSuccess() {
        return false;
    }

    @Override
    public void getConfig(ConfigGroup group) {

    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
