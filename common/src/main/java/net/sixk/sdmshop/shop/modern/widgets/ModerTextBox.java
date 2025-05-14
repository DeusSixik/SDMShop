package net.sixk.sdmshop.shop.modern.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;


public class ModerTextBox extends TextBox {
    public ModerTextBox(Panel panel) {
        super(panel);
    }

    @Override
    public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShapesRenderHelper.drawRoundedRect(graphics, x, y, w, h, 5,RGBA.create(0, 0, 0, 255 / 2));
    }
}
