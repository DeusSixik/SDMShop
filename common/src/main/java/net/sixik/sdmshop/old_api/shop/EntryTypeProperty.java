package net.sixik.sdmshop.old_api.shop;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.old_api.ShopEntrySellType;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public class EntryTypeProperty implements DataSerializerCompoundTag {

    public static final EntryTypeProperty DEFAULT_COUNTABLE = new EntryTypeProperty(true, ShopEntrySellType.BOTH);
    public static final EntryTypeProperty DEFAULT = new EntryTypeProperty(false, ShopEntrySellType.BOTH);
    public static final EntryTypeProperty ONLY_SELL_COUNTABLE = new EntryTypeProperty(true, ShopEntrySellType.ONLY_SELL);
    public static final EntryTypeProperty ONLY_SELL = new EntryTypeProperty(false, ShopEntrySellType.ONLY_SELL);
    public static final EntryTypeProperty ONLY_BUY_COUNTABLE = new EntryTypeProperty(true, ShopEntrySellType.ONLY_BUY);
    public static final EntryTypeProperty ONLY_BUY = new EntryTypeProperty(false, ShopEntrySellType.ONLY_BUY);

    public boolean isCountable;
    public ShopEntrySellType sellType;

    public EntryTypeProperty(boolean isCountable, ShopEntrySellType sellType) {
        this.isCountable = isCountable;
        this.sellType = sellType;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        nbt.putBoolean("countable", isCountable);
        nbt.putInt("sell_type", sellType.ordinal());
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.isCountable = tag.getBoolean("countable");
        this.sellType = ShopEntrySellType.values()[tag.getInt("sell_type")];
    }
}
