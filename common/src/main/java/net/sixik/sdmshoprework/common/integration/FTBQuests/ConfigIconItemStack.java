package net.sixik.sdmshoprework.common.integration.FTBQuests;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ImageConfig;
import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.SelectImageScreen;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ConfigIconItemStack extends ItemStackConfig {

    public ConfigIconItemStack() {
        super(false, true);
    }

    public void onClicked(MouseButton button, ConfigCallback callback) {
        if (this.getCanEdit()) {
            if (button.isRight()) {
                ImageConfig imageConfig = new ImageConfig();
                (new SelectImageScreen(imageConfig, (b) -> {
                    if (b) {
                        if (!((String)imageConfig.value).isEmpty()) {
                            ItemStack stack = new ItemStack((ItemLike) FTBQuestsItems.CUSTOM_ICON.get());
                            stack.addTagElement("Icon", StringTag.valueOf((String)imageConfig.value));
                            this.setCurrentValue(stack);
                        } else {
                            this.setCurrentValue(ItemStack.EMPTY);
                        }
                    }

                    callback.save(b);
                })).openGui();
            } else {
                (new SelectItemStackScreen(this, callback)).openGui();
            }
        }

    }
}