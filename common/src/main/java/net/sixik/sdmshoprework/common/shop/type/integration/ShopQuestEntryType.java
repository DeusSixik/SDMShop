package net.sixik.sdmshoprework.common.shop.type.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopQuestEntryType extends AbstractShopEntryType {
    public String questID = "";
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    private boolean useIconFromQuest = true;

    public ShopQuestEntryType(String questID){
        this.questID = questID;
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {

    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopQuestEntryType(questID);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.integration.quest");
    }

    @Override
    public String getModNameForContextMenu() {
        return "FTB Quests";
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdmr.shop.entry.creator.type.questType.description"));
        list.add(Component.translatable("sdmr.shop.entry.creator.type.questType.description_1"));
        return list;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public String getModId() {
        return "ftbquests";
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("questID", questID);
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        nbt.putBoolean("useIconFromQuest", useIconFromQuest);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        questID = nbt.getString("questID");
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
        useIconFromQuest = nbt.getBoolean("useIconFromQuest");
    }

    @Override
    public String getId() {
        return "questType";
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsClient.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));

        if(quest == null) return;
        if(data.isCompleted(quest)){
            data.setCompleted(QuestObjectBase.parseCodeString(questID), null);
            SDMShopR.setMoney(player, SDMShopR.getMoney(player) + entry.entryPrice);
        }
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsClient.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));

        if(quest == null) return;
        if(!data.isCompleted(quest)){
            data.setCompleted(QuestObjectBase.parseCodeString(questID), new Date(System.currentTimeMillis()));
            SDMShopR.setMoney(player, SDMShopR.getMoney(player) - entry.entryPrice);
        }
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsClient.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));
        if(quest != null){
            if(data.isCompleted(quest)) return false;
        }

        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsClient.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));
        if (quest == null) return 0;
        if (isSell) {
            if (!data.isCompleted(quest)) {
                return 1;
            }
        } else {
            if (quest != null) {
                if (data.isCompleted(quest)) return 0;
            }


            long playerMoney = SDMShopR.getMoney(player);
            if(entry.entryPrice == 0) return 1;
            return (int) (playerMoney / entry.entryPrice) >= 1 ? 1 : 0;
        }
        return 0;
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopQuestEntryType("");
        }
    }
}
