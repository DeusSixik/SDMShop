package net.sixk.sdmshop.shop.Tovar.AddTovar;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixk.sdmshop.shop.Tab.Tab;
import net.sixk.sdmshop.shop.Tab.TovarTab;

import java.util.ArrayList;
import java.util.List;

public class TabPanel extends Panel {

    public TabPanel(Panel panel) {
        super(panel);
    }


    SimpleTextButton test1;
    static List<SimpleTextButton> tesBL = new ArrayList<>();

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_0.draw(graphics, x, y, 40, 70);
        GuiHelper.drawHollowRect(graphics, x, y, 40, 70,NordColors.POLAR_NIGHT_4,false);

    }

    @Override
    public void addWidgets() {

        tesBL.clear();

        for (Tab w : TovarTab.CLIENT.tabList) {

            test1 = new SimpleTextButton(this, Component.literal(w.name), Icon.empty()) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    AddProperties.tabName = w.name;
                    AddTovarPanel gui = (AddTovarPanel) getGui();
                    gui.refreshWidgets();

                }

            };

            add(test1);
            tesBL.add(test1);

        }

        for (int n = 0; n < tesBL.size(); n++) {

            SimpleTextButton w = tesBL.get(n);
            w.setSize(36,9);
            w.setPos( 2,1 + 10 * n);
        }
    }

    @Override
    public void alignWidgets() {

    }

}
