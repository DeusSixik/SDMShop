package net.sdm.sdmshopr.api;

import net.sdm.sdmshopr.shop.condition.FTBQuestsCondition;
import net.sdm.sdmshopr.shop.condition.GameStagesCondition;
import net.sdm.sdmshopr.shop.condition.ManaAndArtifice.MNAFactionCondition;
import net.sdm.sdmshopr.shop.condition.ManaAndArtifice.MNALevelCondition;
import net.sdm.sdmshopr.shop.condition.ManaAndArtifice.MNATierCondition;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ConditionRegister {

    Map<String, IShopCondition> CONDITIONS = new LinkedHashMap();

    static IShopCondition register(IShopCondition provider) {
        return (IShopCondition) CONDITIONS.computeIfAbsent(provider.getID(), (id) -> {
            return provider;
        });
    }

    IShopCondition GAMESTAGES = register(new GameStagesCondition());
    IShopCondition FTBQUESTS = register(new FTBQuestsCondition());
    IShopCondition MNAFACTION = register(new MNAFactionCondition());
    IShopCondition MNALEVEL = register(new MNALevelCondition());
    IShopCondition MNATIER = register(new MNATierCondition());

    static void init(){

    }
}
