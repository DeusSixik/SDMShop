package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.shop.Tovar.AddTovar.SelectTypeTovarPanel;

public class ModernSelectTypeTovarPanel extends SelectTypeTovarPanel {

    public ModernSelectTypeTovarPanel(Panel panel) {
        super(panel);
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        RenderHelper.drawRoundedRect(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));

    }

    @Override
    public void addWidgets() {
        super.addWidgets();


   }
}
