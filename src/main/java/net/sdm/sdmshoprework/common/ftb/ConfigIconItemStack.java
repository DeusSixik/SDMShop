package net.sdm.sdmshoprework.common.ftb;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ImageResourceConfig;
import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectImageResourceScreen;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sdm.sdmshoprework.common.register.ItemsRegister;

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
                        if (!((ResourceLocation)imageConfig.getValue()).equals(ImageResourceConfig.NONE)) {
                            ItemStack stack = new ItemStack((ItemLike) ItemsRegister.CUSTOM_ICON.get());
                            stack.addTagElement("Icon", StringTag.valueOf(((ResourceLocation)imageConfig.getValue()).toString()));
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
}
