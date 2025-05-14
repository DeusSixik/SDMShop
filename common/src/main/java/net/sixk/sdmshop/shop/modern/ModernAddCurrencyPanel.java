package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.shop.AddCurrencyPanel;
import net.sixk.sdmshop.shop.modern.widgets.ModerTextBox;


public class ModernAddCurrencyPanel extends AddCurrencyPanel {

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShapesRenderHelper.drawRoundedRect(graphics,w / 2 - 75 ,h / 2 - 37,150,75,5, RGBA.create(0,0,0, 180));
    }

    @Override
    public void addWidgets() {
        this.currencyName = new ModerTextBox(this);
        this.currencySign = new ModerTextBox(this);
        super.addWidgets();
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
    }
}
