package net.sixik.sdmshoprework.api.shop;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshoprework.api.IModIdentifier;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractShopEntryCondition  implements IModIdentifier {

    public abstract boolean isLocked();
    public abstract AbstractShopEntryCondition copy();
    public abstract void getConfig(ConfigGroup config);

    @Nullable
    public static AbstractShopEntryCondition from(CompoundTag nbt) {
        try {
            String id = nbt.getString("entryConditionID");
            AbstractShopEntryCondition condition = ShopContentRegister.SHOP_ENTRY_CONDITIONS.getOrDefault(id, null).createDefaultInstance();
            if(condition == null) return null;

            if(Platform.isModLoaded(condition.getModId())) {
                condition.deserializeNBT(nbt);
                return condition;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("entryConditionID", getId());
        return nbt;
    }

    public abstract void deserializeNBT(CompoundTag nbt);
}
