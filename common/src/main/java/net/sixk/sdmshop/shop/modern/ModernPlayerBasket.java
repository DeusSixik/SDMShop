package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.shop.PlayerBasket;

public class ModernPlayerBasket extends PlayerBasket {

    @Override
    public boolean onInit() {
        return super.onInit();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShapesRenderHelper.drawRoundedRect(graphics,x,y,w,h,5, RGBA.create(0,0,0, 255/2));
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        //blit(ResourceLocation texture, int x, int y, int width, int height, int textureX, int textureY, int textureW, int textureH, int textureSizeX, int textureSizeY)
        graphics.blit(info.getSkin().texture(), x + 2 , y + 2, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(info.getSkin().texture(), x + 2, y + 2, 15, 15, 40.0f, 8, 8, 8, 64, 64);
        ShapesRenderHelper.drawRoundedRect(graphics,x + 4,y + 24,width/2 - 19, h - 29,2,RGBA.create(0,0,0, 180));
        ShapesRenderHelper.drawRoundedRect(graphics,x + width/2 - 11 ,y + 24,width/2 + 6, h - 29,2, RGBA.create(0,0,0, 180));
    }

    @Override
    public void addWidgets() {
        super.addWidgets();
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
    }
}
