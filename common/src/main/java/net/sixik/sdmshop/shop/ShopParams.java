package net.sixik.sdmshop.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public class ShopParams implements DataSerializerCompoundTag, ConfigSupport {

    protected static final String SHOW_TITLE_KEY = "show_title";
    protected static final String CHANGE_ICON_SPEED_KEY = "change_icon_speed";
    protected static final String SHOW_CANT_BUY_KEY = "show_cant_buy";

    protected CompoundTag data;

    public ShopParams() {
        this(new CompoundTag());
    }

    public ShopParams(CompoundTag nbt) {
        this.data = nbt;
    }

    @Override
    public CompoundTag serialize() {
        return data;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.data = tag;
    }

    public CompoundTag getData() {
        return data;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        ConfigGroup shopParamGroup = group.getOrCreateSubgroup("shop_param");

        shopParamGroup.addBool(SHOW_TITLE_KEY, isShowTitle(), v -> data.putBoolean(SHOW_TITLE_KEY, v), false);

        shopParamGroup.addInt(CHANGE_ICON_SPEED_KEY, getChangeIconSpeed(), v -> data.putInt(CHANGE_ICON_SPEED_KEY, v), 10, 1, Integer.MAX_VALUE);

        shopParamGroup.addBool(SHOW_CANT_BUY_KEY, showEntryWitchCantBuy(), v -> data.putBoolean(SHOW_CANT_BUY_KEY, v), false);
    }

    public void getClientConfig(ConfigGroup group) {
        ConfigGroup shopParamGroup = group.getOrCreateSubgroup("shop_param");
        shopParamGroup.addBool(SHOW_CANT_BUY_KEY, showEntryWitchCantBuy(), v -> data.putBoolean(SHOW_CANT_BUY_KEY, v), false);
    }

    public boolean isShowTitle() {
        return data.contains(SHOW_TITLE_KEY) && data.getBoolean(SHOW_TITLE_KEY);
    }

    public int getChangeIconSpeed() {
        return data.contains(CHANGE_ICON_SPEED_KEY) ? data.getInt(CHANGE_ICON_SPEED_KEY) : 10;
    }

    public boolean showEntryWitchCantBuy() {
        return data.contains(SHOW_CANT_BUY_KEY) && data.getBoolean(SHOW_CANT_BUY_KEY);
    }
}
