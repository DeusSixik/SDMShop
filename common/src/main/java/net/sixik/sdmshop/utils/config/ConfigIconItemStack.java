package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ImageResourceConfig;
import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectImageResourceScreen;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sixik.sdmshop.registers.ShopItemRegisters;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigIconItemStack extends ItemStackConfig {

    public ConfigIconItemStack() {
        super(false, true);
    }

    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (this.getCanEdit()) {
            if (button.isRight()) {
                ImageResourceConfig imageConfig = new ImageResourceConfig();
                (new SelectImageResourceScreen(imageConfig, (accepted) -> {
                    if (accepted) {
                        if (!imageConfig.getValue().equals(ImageResourceConfig.NONE)) {
                            ItemStack stack = new ItemStack(ShopItemRegisters.CUSTOM_ICON.get());
                            stack.addTagElement("Icon", StringTag.valueOf(imageConfig.getValue().toString()));
                            this.setCurrentValue(stack);
                        } else {
                            this.setCurrentValue(ItemStack.EMPTY);
                        }
                    }

                    callback.save(accepted);
                })).openGui();
            } else {
                (new SelectItemStackScreen(this, callback)).openGui();
            }
        }

    }

    public static class CustomIconItem extends Item {

        public CustomIconItem() {
            super(new Properties().stacksTo(1));
        }


        public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
            tooltip.add(Component.translatable("item.ftbquests.custom_icon.tooltip").withStyle(ChatFormatting.GRAY));
            if (stack.hasTag() && stack.getTag().contains("Icon")) {
                tooltip.add(Component.literal(stack.getTag().getString("Icon")).withStyle(ChatFormatting.DARK_GRAY));
            } else {
                tooltip.add(Component.literal("-").withStyle(ChatFormatting.DARK_GRAY));
            }

        }

        public static Icon getIcon(ItemStack stack) {
            if (stack.getItem() instanceof CustomIconItem) {
                return stack.hasTag() && stack.getTag().contains("Icon") ? Icon.getIcon(stack.getTag().getString("Icon")) : Icon.getIcon("minecraft:textures/misc/unknown_pack.png");
            } else {
                return ItemIcon.getItemIcon(stack);
            }
        }
    }
}

