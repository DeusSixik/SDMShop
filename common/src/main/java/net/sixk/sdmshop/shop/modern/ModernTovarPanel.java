package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.TovarPanel;

public class ModernTovarPanel extends TovarPanel {

    public ModernTovarPanel(Panel panel, AbstractTovar tovar) {
        super(panel, tovar);
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShapesRenderHelper.drawRoundedRect(graphics,x,y,28,28,8, RGBA.create(0,0,0, 255/2));
        ShapesRenderHelper.drawRoundedRect(graphics,x,y + 31,28,10,5, RGBA.create(0,0,0, 255/2));
        drawIcon(graphics, x + 4 , y + 4,25,25);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();
        openBuy.setConsumer( (simpleButton, mouseButton) -> new ModernBuyingWindow(tovar.uuid).openGui() );
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
    }
}
