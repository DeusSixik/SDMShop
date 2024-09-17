package net.sdm.sdmshoprework.api.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshoprework.api.IModIdentifier;
import net.sdm.sdmshoprework.api.register.ShopContentRegister;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractShopEntryCondition implements INBTSerializable<CompoundTag>, IModIdentifier {

    public abstract boolean isLocked();
    public abstract AbstractShopEntryCondition copy();
    public abstract void getConfig(ConfigGroup config);

    @Nullable
    public static AbstractShopEntryCondition from(CompoundTag nbt) {
        try {
            String id = nbt.getString("entryConditionID");
            AbstractShopEntryCondition condition = ShopContentRegister.SHOP_ENTRY_CONDITIONS.getOrDefault(id, null).createDefaultInstance();
            if(ModList.get().isLoaded(condition.getModId())) {
                condition.deserializeNBT(nbt);
                return condition;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("entryConditionID", getId());
        return nbt;
    }
}
