package net.sixik.sdmshoprework.api.shop;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshoprework.api.IModIdentifier;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.common.icon.ShopItemIcon;

public abstract class AbstractShopIcon implements INBTSerializable<CompoundTag>, IModIdentifier {



    public abstract void draw(PoseStack graphics, int x, int y, int width, int height);

    public static AbstractShopIcon from(CompoundTag nbt) {
        try {
            String id = nbt.getString("shopIconID");
            AbstractShopIcon shopIcon = ShopContentRegister.SHOP_ICONS.getOrDefault(id, new ShopItemIcon.ShopItemIconC()).createDefaultInstance();
            shopIcon.deserializeNBT(nbt);
            return shopIcon;
        }
        catch (Exception e){
            e.printStackTrace();
            return new ShopItemIcon.ShopItemIconC().createDefaultInstance();
        }
    }

    public abstract AbstractShopIcon copy();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("shopIconID", getId());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}