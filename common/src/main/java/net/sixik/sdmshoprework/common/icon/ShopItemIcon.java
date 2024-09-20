package net.sixik.sdmshoprework.common.icon;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopIcon;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

public class ShopItemIcon extends AbstractShopIcon {

    private ItemStack itemStack;
    private Icon icon;

    public ShopItemIcon(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.icon = ItemIcon.getItemIcon(itemStack);
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height) {
        icon.draw(graphics, x, y, width, height);
    }

    @Override
    public AbstractShopIcon copy() {
        return new ShopItemIcon(itemStack);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        NBTUtils.putItemStack(tag, "item", itemStack);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.itemStack = NBTUtils.getItemStack(nbt, "item");
        this.icon = ItemIcon.getItemIcon(itemStack);
    }

    @Override
    public String getId() {
        return "itemIcon";
    }

    @Override
    public String toString() {
        return "ShopItemIcon{" +
                "itemStack=" + itemStack +
                '}';
    }

    public static class ShopItemIconC implements IConstructor<AbstractShopIcon> {
        @Override
        public AbstractShopIcon createDefaultInstance() {
            return new ShopItemIcon(Items.BARRIER.getDefaultInstance());
        }
    }
}
