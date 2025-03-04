package net.sixik.sdmshoprework;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdm_economy.api.ICustomData;
import net.sixik.sdmshoprework.economy.EconomyManager;
import net.sixik.sdmshoprework.network.client.SendEditModeS2C;

public class SDMShopR {

    public static boolean isEditMode(Player player){
        if(((ICustomData) player).sdm$getCustomData().contains("edit_mode"))
            return ((ICustomData) player).sdm$getCustomData().getBoolean("edit_mode");
        else {
            ((ICustomData) player).sdm$getCustomData().putBoolean("edit_mode", false);
            return false;
        }
    }

    public static boolean isEditMode(){
        if(((ICustomData) Minecraft.getInstance().player).sdm$getCustomData().contains("edit_mode"))
            return ((ICustomData) Minecraft.getInstance().player).sdm$getCustomData().getBoolean("edit_mode");
        else {
            return false;
        }
    }

    public static void setEditMode(ServerPlayer player, boolean value){
        ((ICustomData) player).sdm$getCustomData().putBoolean("edit_mode", value);

        new SendEditModeS2C(value).sendTo(player);
    }

    public static void setMoney(Player player, long money) {
        if(player.isLocalPlayer()) {
            CurrencyHelper.setMoney(player, "basic_money", money);
        } else {
            EconomyManager.economy.set().accept(player, money);
        }
    }

    public static void addMoney(Player player, long money) {
        if(player.isLocalPlayer()) {
            CurrencyHelper.addMoney(player, "basic_money", money);
        }
        else {
            EconomyManager.economy.set().accept(player, EconomyManager.economy.get().apply(player) + money);
        }
    }

    public static long getMoney(Player player) {
        if(player.isLocalPlayer()) {
            return CurrencyHelper.getMoney(player, "basic_money");
        }
        return EconomyManager.economy.get().apply(player);
    }

    public static final boolean isMarketLoaded = Platform.isModLoaded("sdm_market");
}
