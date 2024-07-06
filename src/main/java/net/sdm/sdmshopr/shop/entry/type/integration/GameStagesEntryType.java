package net.sdm.sdmshopr.shop.entry.type.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class GameStagesEntryType implements IEntryType {
    public String gameStage;
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
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
        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("gameStage", gameStage, v -> gameStage = v, "");
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
    }

    @Override
    public String getModID() {
        return "gamestages";
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdmr.shop.entry.creator.type.stageType.description"));
        return list;
    }

    @Override
    public String getModNameForContextMenu() {
        return "Game Stages";
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
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        gameStage = nbt.getString("gameStage");
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
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


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return false;
        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return 0;

        long playerMoney = SDMShopR.getClientMoney();
        return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
    }
}
