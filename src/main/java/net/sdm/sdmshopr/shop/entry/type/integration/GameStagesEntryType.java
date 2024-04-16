package net.sdm.sdmshopr.shop.entry.type.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;

public class GameStagesEntryType implements IEntryType {
    public String gameStage;
    private String iconPath = "minecraft:item/barrier";
    public GameStagesEntryType(String gameStage){
        this.gameStage = gameStage;
    }

    @Override
    public boolean isSellable() {
        return true;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.integration.gamestage");
    }

    @Override
    public Icon getIcon() {
        Icon getted = Icon.getIcon(iconPath);
        if(getted.isEmpty()) return Icons.BARRIER;
        return getted;
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("gameStage", gameStage, v -> gameStage = v, "");
        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
    }

    @Override
    public String getModID() {
        return "gamestages";
    }

    @Override
    public IEntryType copy() {
        return new GameStagesEntryType(gameStage);
    }

    @Override
    public Icon getCreativeIcon() {
        return Icons.CONTROLLER;
    }

    @Override
    public String getID() {
        return "stageType";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", getID());
        nbt.putString("gameStage", gameStage);
        nbt.putString("iconPath", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        gameStage = nbt.getString("gameStage");
        iconPath = nbt.getString("iconPath");
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {

        long playerMoney = SDMShopR.getMoney(player);
        if(!GameStageHelper.hasStage(player, gameStage)) return;

        GameStageHelper.removeStage(player, gameStage);
        SDMShopR.setMoney(player, playerMoney + (entry.price));

    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        long playerMoney = SDMShopR.getMoney(player);

        if(playerMoney < entry.price) return;

        GameStageHelper.addStage(player, gameStage);
        SDMShopR.setMoney(player, playerMoney - (entry.price));
    }


    @Override
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return false;
        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return 0;

        long playerMoney = SDMShopR.getClientMoney();
        return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
    }
}
