package net.sixik.sdmshop.compat;

import dev.architectury.platform.Platform;
import net.sixik.sdmshop.compat.ftbquests.FTBIntegrationHelper;
import net.sixik.sdmshop.compat.kubejs.SDMShopKubeJS;

public class SDMShopIntegration {

    private static boolean gameStagesLoaded = false;
    private static boolean pstLoaded = false;
    private static boolean kubejsLoaded = false;

    public static void init(){

        gameStagesLoaded = Platform.isModLoaded("gamestages");
        kubejsLoaded = Platform.isModLoaded("kubejs");
        if(Platform.isModLoaded("ftbquests")){
            FTBIntegrationHelper.init();
        }
        pstLoaded = Platform.isModLoaded("skilltree");

        if(kubejsLoaded) {
            SDMShopKubeJS.initPlugin();
        }
    }

    public static boolean isFtbQuestLoaded() {
        return FTBIntegrationHelper.FTBQuestLoaded;
    }

    public static boolean isGameStagesLoaded() {
        return gameStagesLoaded;
    }

    public static boolean isKubeJSLoaded() {
        return kubejsLoaded;
    }
}
