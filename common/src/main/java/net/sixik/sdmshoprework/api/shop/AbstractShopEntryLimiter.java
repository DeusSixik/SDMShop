package net.sixik.sdmshoprework.api.shop;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshoprework.api.IModIdentifier;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractShopEntryLimiter implements INBTSerializable<CompoundTag>, IModIdentifier {

    public abstract int getCount();

    public abstract AbstractShopEntryLimiter copy();

    public boolean isLimit() {
        return false;
    }

    public abstract boolean isEmpty();

    @Nullable
    public static AbstractShopEntryLimiter from(CompoundTag nbt) {
        try {
            String id = nbt.getString("entryLimiterID");
            AbstractShopEntryLimiter d1 = ShopContentRegister.SHOP_ENTRY_LIMITERS.getOrDefault(id, null).createDefaultInstance();
            d1.deserializeNBT(nbt);
            return d1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("entryLimiterID", getId());
        return nbt;
    }
}
