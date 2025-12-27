package net.sixik.sdmshop.compat.ftbquests;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.conditions.integration.FTBQuestCondition;
import net.sixik.sdmshop.shop.entry_types.integration.FBTQuestEntryType;
import net.sixik.sdmshop.utils.ShopUtils;

public class FTBIntegrationHelper {

    public static boolean FTBQuestLoaded = false;

    public static void main(){
        FTBQuestLoaded = true;

        try {
            FTBMoneyTask.TYPE = TaskTypes.register(new ResourceLocation(SDMShop.MODID, "money"), FTBMoneyTask::new, () -> Icon.getIcon(ShopUtils.location("textures/icons/shop.png")));
            FTBMoneyReward.TYPE = RewardTypes.register(new ResourceLocation(SDMShop.MODID, "money"), FTBMoneyReward::new, () -> Icon.getIcon(ShopUtils.location("textures/icons/money.png")));
        } catch (NoClassDefFoundError error) {
            SDMShop.LOGGER.error("FAIL TO LOAD FTB Quests");
        }

        ShopContentRegister.registerEntryType("questType", FBTQuestEntryType::new);
        ShopContentRegister.registerCondition("questTypeCondition", FTBQuestCondition::new);
    }
}
