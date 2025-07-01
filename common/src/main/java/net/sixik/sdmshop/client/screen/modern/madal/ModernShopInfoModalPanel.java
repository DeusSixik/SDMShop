package net.sixik.sdmshop.client.screen.modern.madal;

import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopInfoModalPanel extends ModalPanel {

    protected Vector2 position;

    public ModernShopInfoModalPanel(Panel panel) {
        super(panel);

        position = new Vector2(panel.width / 2, panel.height / 2);
        Vector2 size = new Vector2(panel.width / 8, panel.height / 8);

        setSize(position.x - size.x, position.y - size.y);
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int _x, int _y, int w, int h) {
        int x = position.x - w / 2;
        int y = position.y - h / 2;

        RGBA.create(0,0,0, 255/2).draw(graphics, x, y, w, h, 0);
    }
}
