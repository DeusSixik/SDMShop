package net.sdm.sdmshoprework.common.icon;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopIcon;

public class ShopItemIcon extends AbstractShopIcon {

    private ItemStack itemStack;
    private Icon icon;

    public ShopItemIcon(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.icon = ItemIcon.getItemIcon(itemStack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
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
        tag.put("item", itemStack.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.itemStack = ItemStack.of(nbt.getCompound("item"));
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
