package net.sixik.sdmshop.client.screen.modern.create_entry;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen.base.create_entry.AbstractCreateEntryPanel;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernCreateEntryPanel extends AbstractCreateEntryPanel {
    public ModernCreateEntryPanel(Panel panel) {
        super(panel);
    }

    public void addWidgets() {
    }

    public void alignWidgets() {
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectUp(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 127));
    }
}

