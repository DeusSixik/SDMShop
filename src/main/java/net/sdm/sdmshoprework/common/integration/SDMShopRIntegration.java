
package net.sdm.sdmshoprework.common.integration;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.common.integration.FTBQuests.MoneyReward;
import net.sdm.sdmshoprework.common.integration.FTBQuests.MoneyTask;

import static net.sdm.sdmshoprework.SDMShopRework.MODID;

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
            MoneyTask.TYPE = TaskTypes.register(new ResourceLocation("sdmshop", "money"), MoneyTask::new, () -> Icon.getIcon("sdmshoprework:textures/icons/shop.png"));
            MoneyReward.TYPE = RewardTypes.register(new ResourceLocation("sdmshop", "money"), MoneyReward::new, () -> Icon.getIcon("sdmshoprework:textures/icons/money.png"));
        } catch (NoClassDefFoundError error) {
            SDMShopRework.LOGGER.error("FAIL TO LOAD FTB Quests");
        }
    }
}
