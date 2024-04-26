package net.sdm.sdmshopr.shop.limiter;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.api.limiter.condition.ILimiterCondition;

public class DayLimiterCondition implements ILimiterCondition {
    public int maxCountOnDay;

    public DayLimiterCondition(){

    }

    public DayLimiterCondition(int maxCountOnDay){
        this.maxCountOnDay = maxCountOnDay;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isSuccess() {

        return false;
    }

    @Override
    public void getConfig(ConfigGroup group) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public String getID() {
        return "dayCondition";
    }

    @Override
    public ILimiterCondition create() {
        return new DayLimiterCondition();
    }

    @Override
    public ILimiterCondition copy() {
        return new DayLimiterCondition(maxCountOnDay);
    }
}
