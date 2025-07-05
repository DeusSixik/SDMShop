package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.shop.BuyingWindow;

import java.util.UUID;

public class ModernBuyingWindow extends BuyingWindow {

    public GuiGraphics graphics;
    public Theme theme;

    public ModernBuyingWindow(UUID uuid) {
        super(uuid);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        this.graphics = graphics;
        this.theme = theme;

        ShapesRenderHelper.drawRoundedRect(graphics,x,y,w,h,5, RGBA.create(0,0,0, 180));

    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3,y + 3,27,27,8, RGBA.create(0,0,0,100) );

        tovar.getIcon().draw(graphics, x + 7, y + 7, 20, 20);

        ShapesRenderHelper.drawRoundedRect(graphics,x + 32, y + 3, w - 34, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 32, y + 17, w - 34, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3, y + 33, w / 2, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 5 + w / 2, y + 33, w / 2 - 7, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3, y + 47, w / 2, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 5 + w / 2, y + 47, w / 2 - 7, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3, y + 61, w - 5, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3, y + 75, w - 5, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3, y + 89,w - 5, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 3, y + 103, w / 2, 12,6, RGBA.create(0,0,0,100) );

        ShapesRenderHelper.drawRoundedRect(graphics,x + 5 + w / 2, y + 103, w / 2 - 7, 12,6, RGBA.create(0,0,0,100) );

    }

    @Override
    public void addWidgets() {
        super.addWidgets();
    }
}
