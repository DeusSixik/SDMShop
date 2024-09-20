package net.sixik.sdmshoprework.forge.shop.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class ShopGameStagesEntryType extends AbstractShopEntryType {

    public String gameStage;
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public ShopGameStagesEntryType(String gameStage){
        this.gameStage = gameStage;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup group) {
        group.addString("gameStage", gameStage, v -> gameStage = v, "");
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopGameStagesEntryType(gameStage);
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.integration.gamestage");
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
    public Icon getCreativeIcon() {
        return Icons.CONTROLLER;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
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
    public String getId() {
        return "stageType";
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            long playerMoney = SDMShopR.getMoney(player);
            if (!GameStageHelper.hasStage(player, gameStage)) return;

            GameStageHelper.removeStage(serverPlayer, gameStage);
            SDMShopR.setMoney(player, playerMoney + (entry.entryPrice));
        }
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            long playerMoney = SDMShopR.getMoney(player);

            if (playerMoney < entry.entryPrice) return;

            GameStageHelper.addStage(serverPlayer, gameStage);
            SDMShopR.setMoney(player, playerMoney - (entry.entryPrice));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if(GameStageHelper.hasStage(player, gameStage)) return false;
        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public String getModId() {
        return "gamestages";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(GameStageHelper.hasStage(player, gameStage)) return 0;

        long playerMoney = SDMShopR.getMoney(player);
        if(entry.entryPrice == 0) return 1;
        return (int) (playerMoney / entry.entryPrice) >= 1 ? 1 : 0;
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopGameStagesEntryType("");
        }
    }
}
