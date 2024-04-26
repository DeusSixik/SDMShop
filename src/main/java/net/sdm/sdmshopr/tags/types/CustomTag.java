package net.sdm.sdmshopr.tags.types;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.api.tags.ITag;
import net.sdm.sdmshopr.themes.ShopTheme;

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
    public void executeClient(PoseStack graphics, ShopTheme theme, int x, int y, int w, int h) {

    }

}
