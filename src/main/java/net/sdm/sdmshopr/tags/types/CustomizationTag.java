package net.sdm.sdmshopr.tags.types;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.sdm.sdmshopr.api.tags.ITag;
import net.sdm.sdmshopr.themes.ShopTheme;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomizationTag implements ITag {

    public String id;
    public List<Drawable> drawables = new ArrayList<>();

    public CustomizationTag(){

    }

    public static class Drawable{
        public int width;
        public int height;
        public int posX;
        public int posY;
        public String color;
        public boolean isReact = false;

        public Drawable(int width, int height, int posX, int posY, String color, boolean isReact){
            this.width = width;
            this.height = height;
            this.posX = posX;
            this.posY = posY;
            this.color = color;
            this.isReact = isReact;
        }

        public void executeClient(GuiGraphics graphics, ShopTheme theme, int x, int y, int w, int h) {
           Color4I d1 = Color4I.fromString(color);
           if(d1 != Color4I.empty()){
               if(isReact) {
                   GuiHelper.drawHollowRect(graphics,x + posX, y + posY, width, height, d1, false);
               } else {
                   d1.draw(graphics, x + posX, y + posY, width, height);
               }
           }
        }
    }

    public CustomizationTag(String id){
        this.id = id;
    }

    public CustomizationTag add(Drawable drawable){
        drawables.add(drawable);
        return this;
    }

    @Override
    public ITag create() {
        return new CustomizationTag();
    }

    @Override
    public boolean isGlobalTag() {
        return false;
    }

    @Override
    public String getID() {
        return "customizationTag";
    }

    @Override
    public boolean isOnlyClient() {
        return true;
    }

    @Override
    public void executeClient(GuiGraphics graphics, ShopTheme theme, int x, int y, int w, int h) {
        for (Drawable drawable : drawables) {
            drawable.executeClient(graphics, theme, x, y, w, h);
        }
    }
}
