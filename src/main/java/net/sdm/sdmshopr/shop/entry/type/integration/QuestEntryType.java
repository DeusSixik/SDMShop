package net.sdm.sdmshopr.shop.entry.type.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.entry.type.IEntryType;
import org.apache.logging.log4j.spi.CopyOnWrite;

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
            QuestObject quest = FTBQuestsClient.getClientQuestFile().get(QuestObjectBase.parseCodeString(questID));
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

    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {

    }
}
