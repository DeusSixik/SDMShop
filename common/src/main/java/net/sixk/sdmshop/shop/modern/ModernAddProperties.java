package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.shop.Tovar.AddTovar.AddProperties;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import net.sixk.sdmshop.shop.modern.widgets.ModerTextBox;

public class ModernAddProperties extends AddProperties {

    public ModernAddProperties(Panel panel, String tab) {
        super(panel, tab);
    }

    public ModernAddProperties(Panel panel, Tovar tovar) {
        super(panel, tovar);
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRect(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        RenderHelper.drawRoundedRect(graphics,x + 3,y + 3,26,26,10, RGBA.create(0,0,0, 255/2));

        if(icon != null)
            drawIcon(graphics, x + 6, y + 6, w, h);
    }

    @Override
    public void addWidgets() {
        this.itemLim = new ModerTextBox(this);
        this.itemCost = new ModerTextBox(this);
        super.addWidgets();

    }
}
