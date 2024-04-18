package net.sdm.sdmshopr.shop.condition;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import net.darkhax.gamestages.GameStageHelper;
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

public class GameStagesCondition implements IShopCondition {
    public List<String> stages = new ArrayList<>();

    public GameStagesCondition(){

    }

    protected GameStagesCondition(List<String> stages){
        this.stages = stages;
    }


    @Override
    public IShopCondition copy() {
        return new GameStagesCondition(stages);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        for (String gameStage : stages) {
            if(!gameStage.isEmpty() && !GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return true;
        }
        return false;
    }

    @Override
    public void getConfig(ConfigGroup config) {
        config.addList("gameStages", stages, new StringConfig(null), "");
    }

    @Override
    public String getModID() {
        return "gamestages";
    }

    @Override
    public String getID() {
        return "gamestagesConditions";
    }

    @Override
    public IShopCondition create() {
        return new GameStagesCondition();
    }

    @Override
    public void serializeNBT(CompoundTag nbt) {
        if(!stages.isEmpty()){
            ListTag d1 = new ListTag();
            for (String gameStage : stages) {
                d1.add(StringTag.valueOf(gameStage));
            }
            nbt.put("gameStages", d1);
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("gameStages")) {
            stages.clear();
            ListTag d1 = (ListTag) nbt.get("gameStages");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                stages.add(f1.getAsString());
            }
        }
    }
}
