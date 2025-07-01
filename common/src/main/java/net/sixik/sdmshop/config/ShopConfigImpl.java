package net.sixik.sdmshop.config;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public class ShopConfigImpl implements DataSerializerCompoundTag {

    protected static final String DISABLE_KEY = "disable_key_bind";
    protected static final String SEND_NOTIFY_KEY = "send_notify";

    protected boolean disableKeyBind;
    protected boolean sendNotify;

    public ShopConfigImpl() {}

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean(DISABLE_KEY, disableKeyBind);
        nbt.putBoolean(SEND_NOTIFY_KEY, sendNotify);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.disableKeyBind = tag.getBoolean(DISABLE_KEY);
        this.sendNotify = tag.getBoolean(SEND_NOTIFY_KEY);
    }
}
