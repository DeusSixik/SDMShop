package net.sdm.sdmshoprework.common.shop.condition;

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
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryCondition;

import java.util.ArrayList;
import java.util.List;

public class ShopGameStagesCondition extends AbstractShopEntryCondition {

    public List<String> stages = new ArrayList<>();

    public ShopGameStagesCondition(){

    }

    protected ShopGameStagesCondition(List<String> stages){
        this.stages = stages;
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
    public AbstractShopEntryCondition copy() {
        return new ShopGameStagesCondition(stages);
    }

    @Override
    public void getConfig(ConfigGroup config) {
        config.addList("gameStages", stages, new StringConfig(null), "");
    }

    @Override
    public String getModId() {
        return "gamestages";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        ListTag d1 = new ListTag();
        for (String gameStage : stages) {
            d1.add(StringTag.valueOf(gameStage));
        }
        nbt.put("gameStages", d1);
        return nbt;
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

    @Override
    public String getId() {
        return "gamestagesConditions";
    }


    public static class Constructor implements IConstructor<AbstractShopEntryCondition> {
        @Override
        public AbstractShopEntryCondition createDefaultInstance() {
            return new ShopGameStagesCondition(new ArrayList<>());
        }
    }
}
