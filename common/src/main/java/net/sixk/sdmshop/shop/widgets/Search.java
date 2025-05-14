package net.sixk.sdmshop.shop.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.data.config.ConfigFile;

public class Search extends TextBox {

    public static String searchContent = "";

    public Search(Panel panel) {
        super(panel);
    }

    @Override
    public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(ConfigFile.CLIENT.style) ShapesRenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(0, 0, 0, 255 / 2));
        else super.drawTextBox(graphics, theme, x, y, w, h);
    }

    @Override
    public void onTextChanged() {
        searchContent = this.getText();
    }

    @Override
    public void onEnterPressed() {
        getGui().refreshWidgets();
    }
}
