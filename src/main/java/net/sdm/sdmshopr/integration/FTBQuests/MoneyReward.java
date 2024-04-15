package net.sdm.sdmshopr.integration.FTBQuests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;

public class MoneyReward extends Reward {

    public static RewardType TYPE;

    public long value = 1L;
    public int randomBonus = 0;

    public MoneyReward( Quest q) {
        super(q);
    }

//    public MoneyReward(Quest quest) {
//        super(quest);
//    }

    @Override
    public RewardType getType() {
        return TYPE;
    }

    @Override
    public void writeData(CompoundTag nbt) {
        nbt.putLong("ftb_money", value);

        if (randomBonus > 0) {
            nbt.putInt("random_bonus", randomBonus);
        }
    }

    @Override
    public void readData(CompoundTag nbt) {
        value = nbt.getLong("ftb_money");
        randomBonus = nbt.getInt("random_bonus");
    }

    @Override
    public void writeNetData(FriendlyByteBuf buf) {
        super.writeNetData(buf);
        buf.writeVarLong(value);
        buf.writeVarInt(randomBonus);
    }

    @Override
    public void readNetData(FriendlyByteBuf buf) {
        super.readNetData(buf);
        value = buf.readVarLong();
        randomBonus = buf.readVarInt();
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE).setNameKey("ftbquests.reward.sdmshop.money");
        config.addInt("random_bonus", randomBonus, v -> randomBonus = v, 0, 0, Integer.MAX_VALUE).setNameKey("ftbquests.reward.random_bonus");
    }


    @Override
    public void claim(ServerPlayer player, boolean notify) {
        long money = SDMShopR.getMoney(player);
        long added = value + player.getLevel().random.nextInt(randomBonus + 1);
        SDMShopR.setMoney(player, money + added);

        if (notify) {
            new DisplayRewardToastMessage(id, Component.literal(SDMShopR.moneyString(added)), Icon.getIcon("sdmshop:textures/icon.png")).sendTo(player);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        if (randomBonus > 0) {
            return Component.literal(SDMShopR.moneyString(value) + " - " + SDMShopR.moneyString(value + randomBonus)).withStyle(ChatFormatting.GOLD);
        }

        return Component.literal(SDMShopR.moneyString(value));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getButtonText() {
        if (randomBonus > 0) {
            return randomBonus + "-" + Long.toUnsignedString(value + randomBonus);
        }

        return Long.toUnsignedString(value);
    }
}
