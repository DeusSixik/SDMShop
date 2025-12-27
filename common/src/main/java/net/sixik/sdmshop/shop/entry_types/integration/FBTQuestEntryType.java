package net.sixik.sdmshop.shop.entry_types.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.FTBQuestsAPIImpl;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.util.ConfigQuestObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FBTQuestEntryType extends AbstractEntryType {
    protected long questID;

    public FBTQuestEntryType(ShopEntry shopEntry) {
        this(shopEntry, 0);
    }

    public FBTQuestEntryType(ShopEntry shopEntry, long questID) {
        super(shopEntry);
        this.questID = questID;
    }

    @Override
    public AbstractEntryType copy() {
        return new FBTQuestEntryType(shopEntry, questID);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(false).getQuest(questID);
        if (quest == null) return false;
        if (!data.isCompleted(quest)) {
            data.setCompleted(questID, new Date(System.currentTimeMillis()));
            return true;
        }

        return false;
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(false).getQuest(questID);
        if (quest == null) return false;

        if (data.isCompleted(quest)) {
            data.setCompleted(questID, null);
            return true;
        }

        return false;
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(player.isLocalPlayer()).getQuest(questID);
        if (quest != null) {
            if (data.isCompleted(quest)) return false;
        }

        double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
        return playerMoney >= entry.getPrice() * countBuy;
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        TeamData data = TeamData.get(player);
        Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(player.isLocalPlayer()).getQuest(questID);
        if (quest == null) return 0;
        if (entry.getType().isSell()) {
            if (!data.isCompleted(quest)) {
                return 1;
            }
        } else {
            if (data.isCompleted(quest)) return 0;


            double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
            if (entry.getPrice() == 0) return 1;
            return (int) (playerMoney / entry.getPrice()) >= 1 ? 1 : 0;
        }

        return 0;
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.integration.quest");
    }

    @Override
    public String getModNameForContextMenu() {
        return "FTB Quests";
    }

    @Override
    public String getModId() {
        return "ftbquests";
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdm.shop.entry.creator.type.questType.description"));
        list.add(Component.translatable("sdm.shop.entry.creator.type.questType.description_1"));
        return list;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("quest_id", new ConfigQuestObject((v) -> v instanceof Quest obj), FTBQuestsAPIImpl.INSTANCE.getQuestFile(false).get(questID), v -> questID = v.id, null);
    }

    @Override
    public String getId() {
        return "questType";
    }

    @Override
    public boolean isSearch(String search) {
        return false;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("questID", questID);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        questID = tag.getLong("questID");
    }


    @Override
    public EntryTypeProperty getProperty() {
        return EntryTypeProperty.DEFAULT;
    }


}
