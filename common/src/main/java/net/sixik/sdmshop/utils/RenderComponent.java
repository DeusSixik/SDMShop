package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.api.ConfigSupport;
import net.sixik.sdmshop.api.IconRenderSupport;
import net.sixik.sdmshop.registers.ShopItemRegisters;
import net.sixik.sdmshop.utils.config.ConfigIconItemStack;

public class RenderComponent implements IconRenderSupport, ConfigSupport, DataSerializerCompoundTag {

    public static final String KEY = "render_component";

    protected ItemStack icon = ItemStack.EMPTY;


    public RenderComponent() {

    }

    public RenderComponent updateIcon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public Icon getIcon() {
        if(icon.is(ShopItemRegisters.CUSTOM_ICON.get()))
            return ConfigIconItemStack.CustomIconItem.getIcon(icon);
        return ItemIcon.getItemIcon(icon);
    }

    @Override
    public void getConfig(ConfigGroup group) {
        ConfigGroup renderGroup = group.getOrCreateSubgroup("render").setNameKey("sdm.shop.render");
        ConfigValue<ItemStack> value = renderGroup.add("icon", new ConfigIconItemStack(), icon, v -> icon = v, Items.BARRIER.getDefaultInstance())
                .setNameKey("sdm.shop.render.icon");
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        if(icon != ItemStack.EMPTY)
            ShopNBTUtils.putItemStack(nbt, "icon", icon);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if(tag.contains("icon"))
            icon = ShopNBTUtils.getItemStack(tag, "icon");
    }
}
