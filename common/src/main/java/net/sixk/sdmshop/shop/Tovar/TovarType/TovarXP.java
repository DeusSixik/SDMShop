package net.sixk.sdmshop.shop.Tovar.TovarType;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixk.sdmshop.api.IConstructor;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.Tovar;

public class TovarXP extends AbstractTovar {

    private int xpCount;
    private boolean isXPLVL;
    private Icon icon = ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);

    public TovarXP(int xpCount, boolean isXPLVL){
        this.xpCount = xpCount;
        this.isXPLVL = isXPLVL;
    }

    @Override
    public void buy(Player player, Tovar tovar, long count) {

        long playerMoney = EconomyAPI.getPlayerCurrencyServerData().getBalance(player, tovar.currency).value.longValue();
        long needMoney = tovar.cost * count;

        if ((tovar.limit < count && tovar.limit != -1)) return;

        if(isXPLVL){
            if (player instanceof ServerPlayer serverPlayer) {

                needMoney = tovar.cost * count;

                serverPlayer.setExperienceLevels((int) (player.experienceLevel + (xpCount * count)));

            }
        }
        else {
            int experience = (int) (getPlayerXP(player) + (xpCount * count));
            player.totalExperience = experience;
            player.experienceLevel = getLevelForExperience(experience);
            int expForLevel = getExperienceForLevel(player.experienceLevel);
            player.experienceProgress = (float) (experience - expForLevel) / (float) player.getXpNeededForNextLevel();
        }

        EconomyAPI.getPlayerCurrencyServerData().setCurrencyValue(player, tovar.currency, playerMoney - needMoney);
        if (tovar.limit != -1) tovar.limit -= count;

    }

    @Override
    public void sell(Player player, Tovar tovar, long count) {

        if ((tovar.limit < count && tovar.limit != -1)) return;

        if(isXPLVL){
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setExperienceLevels((int) (player.experienceLevel - (xpCount* count)));

            }
        }
        else {
            int experience = (int) (getPlayerXP(player) - (xpCount * count));
            player.totalExperience = experience;
            player.experienceLevel = getLevelForExperience(experience);
            int expForLevel = getExperienceForLevel(player.experienceLevel);
            player.experienceProgress = (float) (experience - expForLevel) / (float) player.getXpNeededForNextLevel();
        }

        EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, tovar.currency, tovar.cost * count);
        if (tovar.limit != -1) tovar.limit -= count;

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
    public String getTitel() {

        if(isXPLVL) return "Level : " + xpCount ;
        else return "Xp : " + xpCount;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public Object getItemStack() {
        return xpCount;
    }

    @Override
    public TagKey getTag() {
        return null;
    }

    @Override
    public AbstractTovar copy() {
        return null;
    }

    @Override
    public String getID() {
        return "XPType";
    }

    @Override
    public boolean getisXPLVL() {
        return isXPLVL;
    }

    @Override
    public KeyData serialize(HolderLookup.Provider provider) {

        KeyData data = new KeyData();

        data.put("id",getID());
        data.put("xpCount", xpCount);
        data.put("isXPLVL", IData.valueOf(isXPLVL?1:0));

        return  data;

    }


    @Override
    public void deserialize(KeyData data, HolderLookup.Provider provider) {

        xpCount = data.getData("xpCount").asInt();
        isXPLVL = data.getData("isXPLVL").asInt()==1 ;

    }


    public static class Constructor implements IConstructor<AbstractTovar>{
        @Override
        public AbstractTovar create() {
            return new TovarXP(0, true);
        }
    }
}
