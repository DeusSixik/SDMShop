package net.sixk.sdmshop.shop.Tab;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixk.sdmshop.shop.network.client.UpdateTabDataC2S;

import java.util.ArrayList;
import java.util.List;

import static dev.ftb.mods.ftblibrary.ui.misc.NordColors.*;

public class TabPanel extends Panel {    public static String selectedTab = "All";
    public List<TabRender> tabRenderList = new ArrayList();
    Panel panel;

    public TabPanel(Panel panel) {
        super(panel);
        this.panel = panel;
        this.refreshWidgets();
    }

    public void addWidgets() {
        int n;
        for(n = 0; n < TovarTab.CLIENT.tabList.size(); ++n) {
            TabRender tabRender = new TabRender(this, TovarTab.CLIENT.tabList.get(n));
            add(tabRender);
            tabRenderList.add(tabRender);
            tabRender.setPos(0, 20 * n);
        }

        for(n = 0; n < this.tabRenderList.size(); ++n) {
            (this.tabRenderList.get(n)).setSize(this.width - 1, 20);
        }

    }

    public void alignWidgets() {
    }
}