package net.sdm.sdmshopr;

import net.minecraftforge.fml.ModList;

public class SDMShopRIntegration {

    public static boolean FTBQuestLoaded = false;
    public static boolean GameStagesLoaded = false;

    public static void init(){

        GameStagesLoaded = ModList.get().isLoaded("gamestages");
        FTBQuestLoaded = ModList.get().isLoaded("ftbquests");
    }
}
