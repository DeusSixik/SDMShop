package net.sdm.sdmshopr.shop.condition;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.api.IShopCondition;

import java.util.ArrayList;
import java.util.List;

public class FTBQuestsCondition implements IShopCondition {
    protected List<String> questID = new ArrayList<>();
    public FTBQuestsCondition(){

    }

    protected FTBQuestsCondition(List<String> questID){
        this.questID = questID;
    }

    @Override
    public IShopCondition copy() {
        return new FTBQuestsCondition(questID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        TeamData data = TeamData.get(Minecraft.getInstance().player);
        for (String s : questID) {
            Quest quest = FTBQuests.PROXY.getClientQuestFile().getQuest(ClientQuestFile.parseCodeString(s));
            if (quest != null) {
                return !data.isCompleted(quest);
            } else return false;
        }
        return false;
    }

    @Override
    public void getConfig(ConfigGroup config) {
        config.addList("questID", questID, new StringConfig(null), "");
    }

    @Override
    public String getModID() {
        return "ftbquests";
    }

    @Override
    public String getID() {
        return "ftbquestConditions";
    }

    @Override
    public void serializeNBT(CompoundTag nbt) {
        ListTag d1 = new ListTag();
        for (String gameStage : questID) {
            d1.add(StringTag.valueOf(gameStage));
        }
        nbt.put("questID", d1);
    }

    @Override
    public IShopCondition create() {
        return new FTBQuestsCondition();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("questID")) {
            questID.clear();
            ListTag d1 = (ListTag) nbt.get("questID");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                questID.add(f1.getAsString());
            }
        }
    }
}
