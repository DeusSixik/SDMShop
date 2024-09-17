package net.sdm.sdmshoprework.common.shop.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.common.ftb.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.utils.NBTUtils;

public class ShopXPEntryType extends AbstractShopEntryType {

    public int xpCount;
    public ItemStack iconPath = Items.EXPERIENCE_BOTTLE.getDefaultInstance();

    public ShopXPEntryType(int xpCount){
        this.xpCount = xpCount;
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countBuy;

        int experience = getPlayerXP(player) + (xpCount * countBuy);
        player.totalExperience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float) (experience - expForLevel) / (float) player.getXpNeededForNextLevel();

        SDMShopR.setMoney(player, playerMoney - needMoney);
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        int experience = getPlayerXP(player) - (xpCount * countSell);
        player.totalExperience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float) (experience - expForLevel) / (float) player.getXpNeededForNextLevel();

        SDMShopR.addMoney(player, entry.entryPrice * (countSell));
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
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {

        if(isSell){
            return player.totalExperience > (countSell * xpCount);
        }

        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(isSell){
            if(player.totalExperience == 0 || xpCount == 0) return 0;
            return (int) (player.totalExperience / xpCount);
        } else {
            long playerMoney = SDMShopR.getMoney(player);
            if(entry.entryPrice == 0) return Byte.MAX_VALUE;
            return (int) (playerMoney / entry.entryPrice);
        }
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addInt("xp", xpCount, v -> xpCount = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopXPEntryType(xpCount);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.xptype");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("xp", xpCount);
        NBTUtils.putItemStack(nbt, "iconPath", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.xpCount = nbt.getInt("xp");
        this.iconPath = NBTUtils.getItemStack(nbt, "iconPath");
    }

    @Override
    public String getId() {
        return "xpType";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopXPEntryType(0);
        }
    }
}
