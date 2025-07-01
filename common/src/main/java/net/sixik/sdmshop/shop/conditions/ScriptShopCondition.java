package net.sixik.sdmshop.shop.conditions;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.api.shop.AbstractShopCondition;
import net.sixik.sdmshop.api.shop.ShopObject;

@Deprecated
public class ScriptShopCondition extends AbstractShopCondition {



    @Override
    public boolean isLocked(ShopObject shopObject) {
        return false;
    }

    @Override
    public AbstractShopCondition copy() {
        return null;
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {

    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public CompoundTag serialize() {
        return null;
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }
}
