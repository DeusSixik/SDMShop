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
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;

import java.util.UUID;


public class TovarXP extends AbstractTovar {
    public int xpCount;
    public boolean isXPLVL;

    public TovarXP(UUID uuid, String tab, String currency, Integer cost, long limit, boolean toSell, int xpCount, boolean isXPLVL) {
        super(uuid, tab, currency, cost, limit, toSell);
        this.icon = ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
        this.xpCount = xpCount;
        this.isXPLVL = isXPLVL;
    }

    public TovarXP(UUID uuid, String tab, String currency, Integer cost, long limit, boolean toSell) {
        super(uuid, tab, currency, cost, limit, toSell);
    }

    public void buy(Player player, AbstractTovar tovar, long count) {
        long playerMoney = (EconomyAPI.getPlayerCurrencyServerData().getBalance(player, tovar.currency).value).longValue();
        long needMoney = (long)tovar.cost * count;
        if (tovar.limit >= count || tovar.limit == -1L) {
            if (this.isXPLVL) {
                if (player instanceof ServerPlayer serverPlayer) {
                    needMoney = (long)tovar.cost * count;
                    serverPlayer.setExperienceLevels((int)((long)player.experienceLevel + (long)this.xpCount * count));
                }
            } else {
                int experience = (int)((long)getPlayerXP(player) + (long)this.xpCount * count);
                player.totalExperience = experience;
                player.experienceLevel = getLevelForExperience(experience);
                int expForLevel = getExperienceForLevel(player.experienceLevel);
                player.experienceProgress = (float)(experience - expForLevel) / (float)player.getXpNeededForNextLevel();
            }

            EconomyAPI.getPlayerCurrencyServerData().setCurrencyValue(player, tovar.currency, (double)(playerMoney - needMoney));
            if (tovar.limit != -1L) {
                tovar.limit -= count;
            }

        }
    }

    public void sell(Player player, AbstractTovar tovar, long count) {
        if (tovar.limit >= count || tovar.limit == -1L) {
            if (this.isXPLVL) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.setExperienceLevels((int)((long)player.experienceLevel - (long)this.xpCount * count));
                }
            } else {
                int experience = (int)((long)getPlayerXP(player) - (long)this.xpCount * count);
                player.totalExperience = experience;
                player.experienceLevel = getLevelForExperience(experience);
                int expForLevel = getExperienceForLevel(player.experienceLevel);
                player.experienceProgress = (float)(experience - expForLevel) / (float)player.getXpNeededForNextLevel();
            }

            EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, tovar.currency, (double)((long)tovar.cost * count));
            if (tovar.limit != -1L) {
                tovar.limit -= count;
            }

        }
    }

    public static int getPlayerXP(Player player) {
        return (int)((float)getExperienceForLevel(player.experienceLevel) + player.experienceProgress * (float)player.getXpNeededForNextLevel());
    }

    public static int getLevelForExperience(int targetXp) {
        int level = 0;

        while(true) {
            int xpToNextLevel = xpBarCap(level);
            if (targetXp < xpToNextLevel) {
                return level;
            }

            ++level;
            targetXp -= xpToNextLevel;
        }
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) {
            return 0;
        } else if (level <= 15) {
            return sum(level, 7, 2);
        } else {
            return level <= 30 ? 315 + sum(level - 15, 37, 5) : 1395 + sum(level - 30, 112, 9);
        }
    }

    public static int xpBarCap(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    public String getTitel() {
        return this.isXPLVL ? "Level : " + this.xpCount : "Xp : " + this.xpCount;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public Object getItemStack() {
        return this.xpCount;
    }

    public TagKey getTag() {
        return null;
    }

    public AbstractTovar copy() {
        return null;
    }

    public String getID() {
        return "XPType";
    }

    public boolean getisXPLVL() {
        return this.isXPLVL;
    }

    public KeyData serialize(HolderLookup.Provider provider) {
        KeyData data = super.serialize(provider);
        data.put("id", this.getID());
        data.put("xpCount", this.xpCount);
        data.put("isXPLVL", IData.valueOf(this.isXPLVL ? 1 : 0));
        return data;
    }

    public void deserialize(KeyData data, HolderLookup.Provider provider) {
        this.xpCount = data.getData("xpCount").asInt();
        this.isXPLVL = data.getData("isXPLVL").asInt() == 1;
    }

}
