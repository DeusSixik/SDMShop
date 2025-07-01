package net.sixik.sdmshop.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ISingleLongValueTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;
import net.sixik.sdmshop.utils.ShopUtils;

public class FTBMoneyTask extends Task implements ISingleLongValueTask {

    public static TaskType TYPE;

    public long value = 1L;
    protected String money_id = SDMCoin.getId();

    public FTBMoneyTask(long id, Quest quest) {
        super(id, quest);
    }


    @Override
    public TaskType getType() {
        return TYPE;
    }

    @Override
    public long getMaxProgress() {
        return value;
    }

    @Override
    public String formatMaxProgress() {
        return ShopUtils.moneyToString(value, money_id);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        return ShopUtils.moneyToString(progress, money_id);
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putString("money_id", money_id);
        nbt.putLong("value", value);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        money_id = nbt.getString("money_id");
        value = nbt.getLong("value");
    }

    @Override
    public void writeNetData(FriendlyByteBuf buf) {
        super.writeNetData(buf);
        buf.writeUtf(money_id);
        buf.writeVarLong(value);
    }

    @Override
    public void readNetData(FriendlyByteBuf buf) {
        super.readNetData(buf);
        money_id = buf.readUtf();
        value = buf.readVarLong();
    }

    @Override
    public void setValue(long v) {
        value = v;
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE);
        config.addEnum("money_id", money_id, (s) -> money_id = s, MoneySellerType.getList());
    }

    @Override
    public Component getAltTitle() {
        return Component.literal(ShopUtils.moneyToString(value, money_id));
    }

    @Override
    public boolean consumesResources() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        super.addMouseOverText(list, teamData);
        list.add(Component.translatable("sdmshop.balance").append(": ").append(Component.literal(ShopUtils.moneyToString(Minecraft.getInstance().player, money_id)).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        double money = ShopUtils.getMoney(player, money_id);
        double add = Math.min(money, value - teamData.getProgress(this));

        if (add > 0L) {
            ShopUtils.setMoney(player, money_id, money - add);
            teamData.addProgress(this, (long) add);
        }
    }
}
