package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.shop.Tab.AddTabPanel;
import net.sixk.sdmshop.shop.Tab.Tab;
import net.sixk.sdmshop.shop.modern.widgets.ModerTextBox;

public class ModernAddTabPanel extends AddTabPanel {

    public ModernAddTabPanel() {
        icon = null;
    }

    public ModernAddTabPanel(Tab tab) {
        this.tab = tab;
        icon = ItemIcon.getItemIcon(tab.item);

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShapesRenderHelper.drawRoundedRect(graphics,w / 2 - 76,h / 2 - 38,152,77,8, RGBA.create(0,0,0, 255/2));
        ShapesRenderHelper.drawRoundedRect(graphics,w / 2 - 70,h / 2 - 12,24,24,8, RGBA.create(0,0,0, 255/2));
        if(icon != null) icon.draw(graphics,w / 2 - 68,h/2 - 10,20,20);
    }

    @Override
    public void addWidgets() {
        name = new ModerTextBox(this);
        super.addWidgets();
    }
}
