package net.sixk.sdmshop.shop.Tab;

import dev.ftb.mods.ftblibrary.ui.Panel;

import java.util.ArrayList;
import java.util.List;

public class TabPanel extends Panel {

    public static String selectedTab = "All";

    public List<TabRender> tabRenderList = new ArrayList<>();
    Panel panel;
    public TabPanel(Panel panel) {
        super(panel);
        this.panel = panel;
        refreshWidgets();
    }


        @Override
        public void addWidgets() {
            for (int n = 0; n< TovarTab.CLIENT.tabList.size(); n++) {

                TabRender tabRender = new TabRender(this, TovarTab.CLIENT.tabList.get(n));
                add(tabRender);
                tabRenderList.add(tabRender);
                tabRender.setPos(0, 20 * n );

            }
            for (int n = 0; n < tabRenderList.size(); n++) {
                    tabRenderList.get(n).setSize(width - 1, 20);
            }
        }
        @Override
        public void alignWidgets() {

        }

}
