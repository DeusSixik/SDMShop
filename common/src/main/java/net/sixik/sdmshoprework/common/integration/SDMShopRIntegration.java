package net.sixik.sdmshoprework.common.integration;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.common.integration.FTBQuests.MoneyReward;
import net.sixik.sdmshoprework.common.integration.FTBQuests.MoneyTask;


public class SDMShopRIntegration {

    public static boolean FTBQuestLoaded = false;
    public static boolean GameStagesLoaded = false;
    public static boolean PSTLoaded = false;

    public static void init(){

        GameStagesLoaded = Platform.isModLoaded("gamestages");
        if(Platform.isModLoaded("ftbquests")){
            FTBQuests();
        }
        PSTLoaded = Platform.isModLoaded("skilltree");

    }


    public static void FTBQuests(){
        FTBQuestLoaded = true;

        try {
            MoneyTask.TYPE = TaskTypes.register(new ResourceLocation("sdmshop", "money"), MoneyTask::new, () -> Icon.getIcon("sdmshoprework:textures/icons/shop.png"));
            MoneyReward.TYPE = RewardTypes.register(new ResourceLocation("sdmshop", "money"), MoneyReward::new, () -> Icon.getIcon("sdmshoprework:textures/icons/money.png"));
        } catch (NoClassDefFoundError error) {
            SDMShopRework.LOGGER.error("FAIL TO LOAD FTB Quests");
        }
    }
}
