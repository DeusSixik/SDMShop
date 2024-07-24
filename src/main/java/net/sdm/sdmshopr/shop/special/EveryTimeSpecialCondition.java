package net.sdm.sdmshopr.shop.special;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.sdm.sdmshopr.api.ISpecialEntryCondition;

import java.time.LocalTime;
import java.util.Date;

public class EveryTimeSpecialCondition implements ISpecialEntryCondition {

    public int houseStart;
    public int minutesStart;
    public int secondsStart;

    public int houseEnd;
    public int minutesEnd;
    public int secondsEnd;

    public EveryTimeSpecialCondition(){
        this.houseStart = 0;
        this.minutesStart = 0;
        this.secondsStart = 0;
        this.houseEnd = 1;
        this.minutesEnd = 0;
        this.secondsEnd = 0;
    }

    @Override
    public boolean isConditionSuccess() {
        LocalTime dateStart = LocalTime.of(houseStart, minutesStart, minutesEnd);
        LocalTime dataEnd = LocalTime.of(houseEnd, minutesEnd, secondsEnd);
        LocalTime now = LocalTime.now();

        return isTimeInRange(now, dateStart, dataEnd);
    }

    public boolean isTimeInRange(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(endTime)) {
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        } else {
            return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
        }
    }

    @Override
    public String getID() {
        return "everyTime";
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addInt("houseStart", houseStart, v -> houseStart = v, 0, 0, 23);
        group.addInt("minutesStart", minutesStart, v -> minutesStart = v, 0, 0, 59);
        group.addInt("secondsStart", secondsStart, v -> secondsStart = v, 0, 0, 59);

        group.addInt("houseEnd", houseEnd, v -> houseStart = v, 0, 0, 23);
        group.addInt("minutesEnd", minutesEnd, v -> minutesEnd = v, 0, 0, 59);
        group.addInt("secondsEnd", secondsEnd, v -> secondsEnd = v, 0, 0, 59);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = ISpecialEntryCondition.super.serializeNBT();

        nbt.putInt("houseStart", houseStart);
        nbt.putInt("minutesStart", minutesStart);
        nbt.putInt("secondsStart", secondsStart);
        nbt.putInt("houseEnd", houseEnd);
        nbt.putInt("minutesEnd", minutesEnd);
        nbt.putInt("secondsEnd", secondsEnd);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.houseStart = nbt.getInt("houseStart");
        this.minutesStart = nbt.getInt("minutesStart");
        this.secondsStart = nbt.getInt("secondsStart");
        this.houseEnd = nbt.getInt("houseEnd");
        this.minutesEnd = nbt.getInt("minutesEnd");
        this.secondsEnd = nbt.getInt("secondsEnd");
    }
}
