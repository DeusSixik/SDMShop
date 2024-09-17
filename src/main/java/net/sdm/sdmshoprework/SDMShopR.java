package net.sdm.sdmshoprework;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmeconomy.api.CurrencyHelper;
import net.sdm.sdmshoprework.network.client.SendEditModeS2C;
import org.apache.logging.log4j.core.jmx.Server;

public class SDMShopR {

    public static boolean isEditMode(Player player){
        if(player.getPersistentData().contains("edit_mode"))
            return player.getPersistentData().getBoolean("edit_mode");
        else {
            player.getPersistentData().putBoolean("edit_mode", false);
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isEditMode(){
        if(Minecraft.getInstance().player.getPersistentData().contains("edit_mode"))
            return Minecraft.getInstance().player.getPersistentData().getBoolean("edit_mode");
        else {
            return false;
        }
    }

    public static void setEditMode(ServerPlayer player, boolean value){
        player.getPersistentData().putBoolean("edit_mode", value);

        new SendEditModeS2C(value).sendTo(player);
    }

    public static void setMoney(Player player, long money) {
        CurrencyHelper.setMoney(player, "basic_money", money);
    }

    public static void addMoney(Player player, long money) {
        CurrencyHelper.addMoney(player, "basic_money",money);
    }

    public static long getMoney(Player player) {
        return CurrencyHelper.getMoney(player, "basic_money");
    }
}
