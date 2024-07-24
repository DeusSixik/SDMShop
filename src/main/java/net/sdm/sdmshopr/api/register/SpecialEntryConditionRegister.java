package net.sdm.sdmshopr.api.register;

import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.api.ISpecialEntryCondition;
import net.sdm.sdmshopr.shop.special.EveryTimeSpecialCondition;

import java.util.LinkedHashMap;
import java.util.Map;

public interface SpecialEntryConditionRegister {

    Map<String, ISpecialEntryCondition> TYPES = new LinkedHashMap();


    static ISpecialEntryCondition register(ISpecialEntryCondition provider) {
        return TYPES.computeIfAbsent(provider.getID(), (id) -> provider);
    }

    ISpecialEntryCondition EVERY_TIME = register(new EveryTimeSpecialCondition());

    static void init(){

    }
}
