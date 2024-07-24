package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

public class XPEntryType implements IEntryType {

    public int xpCount;
    public ItemStack iconPath = Items.EXPERIENCE_BOTTLE.getDefaultInstance();

    public XPEntryType(int xpCount){
        this.xpCount = xpCount;
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {

        long playerMoney = SDMShopR.getMoney(player);
        int needMoney = entry.price * countBuy;

        int experience = getPlayerXP(player) + (xpCount * countBuy);
        player.totalExperience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float) (experience - expForLevel) / (float) player.getXpNeededForNextLevel();

        SDMShopR.setMoney(player, playerMoney - needMoney);
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {

        int experience = getPlayerXP(player) - (xpCount * countSell);
        player.totalExperience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float) (experience - expForLevel) / (float) player.getXpNeededForNextLevel();

        SDMShopR.addMoney(player, entry.price * (countSell));
    }

    public static int getPlayerXP(Player player) {
        return (int) (getExperienceForLevel(player.experienceLevel) + (player.experienceProgress * player.getXpNeededForNextLevel()));
    }

    public static int getLevelForExperience(int targetXp) {
        int level = 0;

        while (true) {
            final int xpToNextLevel = xpBarCap(level);

            if (targetXp < xpToNextLevel) {
                return level;
            }

            level++;
            targetXp -= xpToNextLevel;
        }
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) {
            return 0;
        }

        if (level <= 15) {
            return sum(level, 7, 2);
        }

        if (level <= 30) {
            return 315 + sum(level - 15, 37, 5);
        }

        return 1395 + sum(level - 30, 112, 9);
    }

    public static int xpBarCap(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        }

        if (level >= 15) {
            return 37 + (level - 15) * 5;
        }

        return 7 + level * 2;
    }

    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        Player player = Minecraft.getInstance().player;

        if(isSell){
            return player.totalExperience > (countSell * xpCount);
        }

        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell){
            Player player = Minecraft.getInstance().player;
            if(player.totalExperience == 0 || xpCount == 0) return 0;
            return (int) (player.totalExperience / xpCount);
        } else {
            long playerMoney = SDMShopR.getClientMoney();
            return (int) (playerMoney / entry.price);
        }
    }

    @Override
    public boolean isSellable() {
        return true;
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addInt("xp", xpCount, v -> xpCount = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    public String getID() {
        return "xpType";
    }

    @Override
    public IEntryType copy() {
        return new XPEntryType(xpCount);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.xptype");
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();
        nbt.putInt("xp", xpCount);
        NBTUtils.putItemStack(nbt, "iconPath", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.xpCount = nbt.getInt("xp");
        this.iconPath = NBTUtils.getItemStack(nbt, "iconPath");
    }
}
