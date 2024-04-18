package net.sdm.sdmshopr.tags.types;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.api.tags.ITag;
import net.sdm.sdmshopr.themes.ShopTheme;
import org.jetbrains.annotations.Nullable;

public class CustomTag implements ITag {

    public CustomTag(){

    }

    @Override
    public ITag create() {
        return new CustomTag();
    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public boolean isGlobalTag() {
        return true;
    }

    @Override
    public boolean isOnlyClient() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void executeClient(GuiGraphics graphics, ShopTheme theme, int x, int y, int w, int h) {

    }

}
