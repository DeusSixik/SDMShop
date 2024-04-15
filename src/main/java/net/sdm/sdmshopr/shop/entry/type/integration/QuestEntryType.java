package net.sdm.sdmshopr.shop.entry.type.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.entry.type.IEntryType;
import org.apache.logging.log4j.spi.CopyOnWrite;

import java.util.Date;

public class QuestEntryType implements IEntryType {

    public String questID = "";
    private String iconPath = "minecraft:item/barrier";
    private boolean useIconFromQuest = true;

    public QuestEntryType(String questID){
        this.questID = questID;
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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", "questType");
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
