package net.sixik.sdmshoprework.common.shop.condition;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryCondition;

import java.util.ArrayList;
import java.util.List;

public class ShopFTBQuestsCondition extends AbstractShopEntryCondition {

    protected List<String> questID = new ArrayList<>();
    public ShopFTBQuestsCondition(){

    }

    protected ShopFTBQuestsCondition(List<String> questID){
        this.questID = questID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isLocked() {
        TeamData data = TeamData.get(Minecraft.getInstance().player);
        for (String s : questID) {
            Quest quest = ClientQuestFile.INSTANCE.getQuest(ClientQuestFile.parseCodeString(s));
            if (quest != null) {
                return !data.isCompleted(quest);
            } else return false;
        }
        return false;
    }

    @Override
    public AbstractShopEntryCondition copy() {
        return new ShopFTBQuestsCondition(questID);
    }

    @Override
    public void getConfig(ConfigGroup config) {
        config.addList("questID", questID, new StringConfig(null), "");
    }

    @Override
    public String getModId() {
        return "ftbquests";
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        ListTag d1 = new ListTag();
        for (String gameStage : questID) {
            d1.add(StringTag.valueOf(gameStage));
        }
        nbt.put("questID", d1);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        questID.clear();
        ListTag d1 = (ListTag) nbt.get("questID");
        for (Tag tag : d1) {
            StringTag f1 = (StringTag) tag;
            questID.add(f1.getAsString());
        }
    }

    @Override
    public String getId() {
        return "ftbquestConditions";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryCondition> {

        @Override
        public AbstractShopEntryCondition createDefaultInstance() {
            return new ShopFTBQuestsCondition(new ArrayList<>());
        }
    }
}
