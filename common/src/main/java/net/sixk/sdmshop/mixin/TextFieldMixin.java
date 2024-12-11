package net.sixk.sdmshop.mixin;

import dev.ftb.mods.ftblibrary.ui.TextField;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(TextField.class)
public interface TextFieldMixin {

    @Accessor("rawText")
    Component getRawText();

}


