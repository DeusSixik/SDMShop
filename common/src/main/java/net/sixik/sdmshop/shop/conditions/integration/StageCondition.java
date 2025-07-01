package net.sixik.sdmshop.shop.conditions.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.sixik.sdmshop.api.shop.AbstractShopCondition;
import net.sixik.sdmshop.api.shop.ShopObject;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import net.sixik.sdmshop.utils.StagesUtils;

import java.util.ArrayList;
import java.util.List;

public class StageCondition extends AbstractShopCondition {

    protected List<String> stages;

    public StageCondition() {
        this(new ArrayList<>());
    }

    protected StageCondition(List<String> stages) {
        this.stages = stages;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isLocked(ShopObject shopObject) {
        return !stages.stream().allMatch(s -> StagesUtils.hasStage(Minecraft.getInstance().player, s));
    }

    @Override
    public AbstractShopCondition copy() {
        return new StageCondition(stages);
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {
        configGroup.addList("stages", stages, new StringConfig(), "").setNameKey("sdm.shop.conditions.stages");
    }

    @Override
    public String getId() {
        return "stageCondition";
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        ShopNBTUtils.putList(nbt, "stages", stages, StringTag::valueOf);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        ShopNBTUtils.getList(tag, "stages", Tag::getAsString, stages);
    }
}
