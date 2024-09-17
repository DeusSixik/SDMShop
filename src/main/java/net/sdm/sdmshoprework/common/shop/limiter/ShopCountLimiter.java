package net.sdm.sdmshoprework.common.shop.limiter;

import net.minecraft.nbt.CompoundTag;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryLimiter;

public class ShopCountLimiter extends AbstractShopEntryLimiter {

    public int count;

    public ShopCountLimiter(int count) {
        this.count = count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public AbstractShopEntryLimiter copy() {
        return new ShopCountLimiter(count);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("count", count);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.count = nbt.getInt("count");
    }

    @Override
    public String getId() {
        return "shopCountLimit";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryLimiter> {
        @Override
        public AbstractShopEntryLimiter createDefaultInstance() {
            return new ShopCountLimiter(0);
        }
    }
}
