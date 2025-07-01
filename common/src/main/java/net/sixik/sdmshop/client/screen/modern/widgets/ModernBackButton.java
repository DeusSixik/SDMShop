package net.sixik.sdmshop.client.screen.modern.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen.base.create_entry.AbstractCreateEntryScreen;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.RenderHelper;

public class ModernBackButton extends AbstractCreateEntryScreen.AbstractBackButton {
    public ModernBackButton(Panel panel) {
        super(panel);
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawHollowRect(graphics, x, y, w, h, RGBA.create(100, 100, 100, 100).withAlpha(100), false);
        RGBA.create(0, 0, 0, 85).draw(graphics, x, y, w, h);
    }
}
