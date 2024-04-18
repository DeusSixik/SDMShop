package net.sdm.sdmshopr.shop.entry.type.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestEntryType implements IEntryType {

    public String questID = "";
    private String iconPath = "minecraft:item/barrier";
    private boolean useIconFromQuest = true;

    public QuestEntryType(String questID){
        this.questID = questID;
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.integration.quest");
    }

    @Override
    public String getModNameForContextMenu() {
        return "FTB Quests";
    }

    public static QuestEntryType of(String questID){
        return new QuestEntryType(questID);
    }

    @Override
    public boolean isSellable() {
        return true;
    }

    @Override
    public boolean isCountable() {return false;}

    @Override
    public Icon getIcon() {
        if(useIconFromQuest) {
            QuestObject quest = FTBQuests.PROXY.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));
            if (quest == null) return Icons.BARRIER;
            return quest.getIcon();
        }
        Icon getted = Icon.getIcon(iconPath);
        if(getted.isEmpty()) return Icons.BARRIER;
        return getted;
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdmr.shop.entry.creator.type.questType.description"));
        list.add(Component.translatable("sdmr.shop.entry.creator.type.questType.description_1"));
        return list;
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("questid", questID, v -> questID = v, "");
        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
        group.addBool("useIconFromQuest", useIconFromQuest, v -> useIconFromQuest = v, true);
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("ftbquests:textures/item/book.png");
    }


    @Override
    public String getModID() {
        return "ftbquests";
    }

    @Override
    public IEntryType copy() {
        return new QuestEntryType(questID);
    }

    @Override
    public String getID() {
        return "questType";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", getID());
        nbt.putString("questID", questID);
        nbt.putString("iconPath", iconPath);
        nbt.putBoolean("useIconFromQuest", useIconFromQuest);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        questID = nbt.getString("questID");
        iconPath = nbt.getString("iconPath");
        useIconFromQuest = nbt.getBoolean("useIconFromQuest");
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuests.PROXY.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));

        if(quest == null) return;
        if(data.isCompleted(quest)){
            data.setCompleted(QuestObjectBase.parseCodeString(questID), null);
            SDMShopR.setMoney(player, SDMShopR.getMoney(player) + entry.price);
        }
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuests.PROXY.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));

        if(quest == null) return;
        if(!data.isCompleted(quest)){
            data.setCompleted(QuestObjectBase.parseCodeString(questID), new Date(System.currentTimeMillis()));
            SDMShopR.setMoney(player, SDMShopR.getMoney(player) - entry.price);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        TeamData data = TeamData.get(Minecraft.getInstance().player);
        Quest quest = FTBQuests.PROXY.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));
        if(quest != null){
            if(data.isCompleted(quest)) return false;
        }

        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        TeamData data = TeamData.get(Minecraft.getInstance().player);
        Quest quest = FTBQuests.PROXY.getClientQuestFile().getQuest(QuestObjectBase.parseCodeString(questID));
        if (quest == null) return 0;
        if (isSell) {
            if (!data.isCompleted(quest)) {
                return 1;
            }
        } else {
            if (quest != null) {
                if (data.isCompleted(quest)) return 0;
            }


            long playerMoney = SDMShopR.getClientMoney();
            return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
        }
        return 0;
    }
}
