package net.sixik.sdmshop.shop.conditions.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.FTBQuestsAPIImpl;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.util.ConfigQuestObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;

public class FTBQuestCondition extends AbstractShopCondition {

    protected long questID;

    public FTBQuestCondition() {
        this(0L);
    }

    public FTBQuestCondition(long id) {
        this.questID = id;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isLocked(ShopObject shopObject) {
        TeamData data = TeamData.get(Minecraft.getInstance().player);
        Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(true).getQuest(questID);
        if (quest == null) return true;
        return !data.isCompleted(quest);
    }

    @Override
    public AbstractShopCondition copy() {
        return new FTBQuestCondition(questID);
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {
        configGroup.add("quest_id", new ConfigQuestObject((v) -> v instanceof Quest obj), FTBQuestsAPIImpl.INSTANCE.getQuestFile(false).get(questID), v -> {
            if(v == null) return; questID = v.id;
                }, null)
                .setNameKey("sdm.shop.conditions.quest_id");
    }

    @Override
    public String getId() {
        return "questTypeCondition";
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("quest_id", questID);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.questID = tag.getLong("questID");
    }
}
