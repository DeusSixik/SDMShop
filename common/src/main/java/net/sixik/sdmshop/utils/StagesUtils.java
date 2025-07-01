package net.sixik.sdmshop.utils;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.stages.Stages;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.compat.SDMShopIntegration;

public class StagesUtils {

    public static boolean addStage(Player player, String stage) {
        boolean value = false;

        if(Platform.isModLoaded("gamestages") && player instanceof ServerPlayer player1) {
            GameStageHelper.addStage(player1, stage);
            value = true;
        }

        if(SDMShopIntegration.isKubeJSLoaded()) {
            Stages.get(player).add(stage);
            value = true;
        }

        return value;
    }

    public static boolean removeStage(Player player, String stage) {
        boolean value = false;

        if(Platform.isModLoaded("gamestages") && player instanceof ServerPlayer player1) {
            GameStageHelper.removeStage(player1, stage);
            value = true;
        }

        if(SDMShopIntegration.isKubeJSLoaded()) {
            value = Stages.get(player).remove(stage);
        }

        return value;
    }

    public static boolean hasStage(Player player, String stage) {
        if(Platform.isModLoaded("gamestages"))
           return GameStageHelper.hasStage(player, stage);
        if(SDMShopIntegration.isKubeJSLoaded())
            return Stages.get(player).has(stage);

        return true;
    }
}
