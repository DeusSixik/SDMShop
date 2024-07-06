package net.sdm.sdmshopr.shop.entry.type.randomEntryType;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.utils.NBTUtils;

@Deprecated
public class RandomEntry implements INBTSerializable<CompoundTag> {
    public IEntryType entryType;
    public double probability;

    public RandomEntry(IEntryType entryType, double probability) {
        this.entryType = entryType;
        this.probability = probability;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("entryType", entryType.serializeNBT());
        nbt.putDouble("probability", probability);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.entryType = NBTUtils.getEntryType(nbt.getCompound("entryType"));
        this.probability = nbt.getDouble("probability");
    }
}
