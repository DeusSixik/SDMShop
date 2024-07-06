package net.sdm.sdmshopr.api.limiter.condition;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.sdm.sdmshopr.api.register.IRegister;

public interface ILimiterCondition extends IRegister<ILimiterCondition> {

    boolean isSuccess();

    default boolean isGlobal(){
        return false;
    }

    void getConfig(ConfigGroup group);

    int getCount();
}
