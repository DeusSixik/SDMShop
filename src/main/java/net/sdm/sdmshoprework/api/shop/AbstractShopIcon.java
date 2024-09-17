package net.sdm.sdmshoprework.api.shop;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshoprework.api.IModIdentifier;
import net.sdm.sdmshoprework.api.register.ShopContentRegister;
import net.sdm.sdmshoprework.common.icon.ShopItemIcon;

public abstract class AbstractShopIcon implements INBTSerializable<CompoundTag>, IModIdentifier {



    @OnlyIn(Dist.CLIENT)
    public abstract void draw(GuiGraphics graphics, int x, int y, int width, int height);

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
