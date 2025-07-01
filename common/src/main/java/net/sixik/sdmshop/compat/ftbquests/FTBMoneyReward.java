package net.sixik.sdmshop.compat.ftbquests;

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
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Random;

public class FTBMoneyReward extends Reward {

    public static RewardType TYPE;

    protected double value = 1L;
    protected String money_id = SDMCoin.getId();

    protected double randomBonus = 0;

    public FTBMoneyReward(long id, Quest q) {
        super(id, q);
    }

    @Override
    public RewardType getType() {
        return TYPE;
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putString("money_id", money_id);
        nbt.putDouble("ftb_money", value);

        if (randomBonus > 0) {
            nbt.putDouble("random_bonus", randomBonus);
        }
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        value = nbt.getLong("ftb_money");
        randomBonus = nbt.getInt("random_bonus");
    }

    @Override
    public void writeNetData(FriendlyByteBuf buf) {
        super.writeNetData(buf);
        buf.writeUtf(money_id);
        buf.writeDouble(value);
        buf.writeDouble(randomBonus);
    }

    @Override
    public void readNetData(FriendlyByteBuf buf) {
        super.readNetData(buf);
        money_id = buf.readUtf();
        value = buf.readVarLong();
        randomBonus = buf.readVarInt();

    }


    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addDouble("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE);
        config.addDouble("random_bonus", randomBonus, v -> randomBonus = v, 0, 0, Integer.MAX_VALUE);
        config.addEnum("money_id", money_id, (s) -> money_id = s, MoneySellerType.getList());
    }

    @Override
    public void claim(ServerPlayer player, boolean notify) {
        double money = ShopUtils.getMoney(player, money_id);
        double added = value + new Random().nextDouble(randomBonus + 1);
        ShopUtils.setMoney(player, money_id, money + added);

        if (notify) {
            new DisplayRewardToastMessage(id, Component.literal(ShopUtils.moneyToString(added, money_id)), Icon.getIcon(ShopUtils.location("textures/icons/money.png"))).sendTo(player);
        }
    }

    @Override
    public Component getAltTitle() {
        if (randomBonus > 0) {
            return Component.literal(ShopUtils.moneyToString(value, money_id)).append(" - ").append(Component.literal(ShopUtils.moneyToString((value + randomBonus), money_id)).withStyle(ChatFormatting.GOLD));
        }

        return Component.literal(ShopUtils.moneyToString(value, money_id));
    }

    @Override
    public String getButtonText() {
        if (randomBonus > 0) {
            return randomBonus + "-" + (value + randomBonus);
        }

        return String.valueOf(value);
    }
}
