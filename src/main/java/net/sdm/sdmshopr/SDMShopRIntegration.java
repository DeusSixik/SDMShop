package net.sdm.sdmshopr;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.integration.FTBQuests.MoneyReward;
import net.sdm.sdmshopr.integration.FTBQuests.MoneyTask;

import static net.sdm.sdmshopr.SDMShopR.MODID;

public class SDMShopRIntegration {

    public static boolean FTBQuestLoaded = false;
    public static boolean GameStagesLoaded = false;
    public static boolean PSTLoaded = false;

    public static void init(){

        GameStagesLoaded = ModList.get().isLoaded("gamestages");
        if(ModList.get().isLoaded("ftbquests")){
            FTBQuests();
        }
        PSTLoaded = ModList.get().isLoaded("skilltree");

    }


    public static void FTBQuests(){
        FTBQuestLoaded = true;

        try {
            MoneyTask.TYPE = TaskTypes.register(new ResourceLocation(MODID, "money"), MoneyTask::new, () -> Icon.getIcon("sdmshop:textures/icon.png"));
            MoneyReward.TYPE = RewardTypes.register(new ResourceLocation(MODID, "money"), MoneyReward::new, () -> Icon.getIcon("sdmshop:textures/icon.png"));
        } catch (NoClassDefFoundError error) {
            SDMShopR.LOGGER.error("FAIL TO LOAD FTB Quests");
        }
    }
}
