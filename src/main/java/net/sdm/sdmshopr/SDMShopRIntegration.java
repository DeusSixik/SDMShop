package net.sdm.sdmshopr;

import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.integration.FTBQuests.MoneyReward;
import net.sdm.sdmshopr.integration.FTBQuests.MoneyTask;

import java.util.function.Consumer;
import java.util.regex.Pattern;

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
            MoneyTask.TYPE = TaskTypes.register(new ResourceLocation(MODID, "money"), MoneyTask::new,
                    () -> Icon.getIcon("sdmshop:textures/icon.png"));
            MoneyReward.TYPE = RewardTypes.register(new ResourceLocation(MODID, "money"), MoneyReward::new,
                    () -> Icon.getIcon("sdmshop:textures/icon.png"));

//            MoneyReward.TYPE.setGuiProvider(new RewardType.GuiProvider() {
//                @Override
//                @OnlyIn(Dist.CLIENT)
//                public void openCreationGui(Runnable gui, Quest quest, Consumer<Reward> callback) {
//                    StringConfig money = new StringConfig(Pattern.compile("^\\d+(?:-\\d+)?$"));
//
//                    money.onClicked(MouseButton.LEFT, set -> {
//                        gui.run();
//                        if (set) {
//                            try {
//                                String[] s = money.getValue().split("-", 2);
//                                MoneyReward reward = new MoneyReward(999, quest);
//                                reward.value = Long.parseLong(s[0].trim());
//
//                                if (s.length == 2) {
//                                    long max = Long.parseLong(s[1].trim());
//
//                                    if (max - reward.value <= Integer.MAX_VALUE) {
//                                        reward.randomBonus = (int) (max - reward.value);
//                                    }
//                                }
//
//                                callback.accept(reward);
//                            } catch (Exception ex) {
//                            }
//                        }
//                    });
//                }
//            });
        } catch (NoClassDefFoundError error) {
            SDMShopR.LOGGER.error("FAIL TO LOAD FTB Quests");
        }
    }
}
