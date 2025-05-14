package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.shop.Tab.TabPanel;
import net.sixk.sdmshop.shop.widgets.Search;

public class ModernSearchPanel extends Panel {

    public SimpleButton resetTab;
    public SimpleButton cancel;
    public Search search;

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectUp(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }

    public ModernSearchPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(resetTab = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.reset_tabs"), Icons.REFRESH,(simpleButton, mouseButton) -> {
            TabPanel.selectedTab = "All";
            Search.searchContent = "";
            getGui().refreshWidgets();
        }));

        add(search = new Search(this));
        search.setText(Search.searchContent);

        add(cancel = new SimpleButton(this, Component.translatable("sdm_shop.cancel"),Icons.CANCEL,((simpleButton, mouseButton) ->{
            closeGui();
        })));
    }

    @Override
    public void alignWidgets() {
        resetTab.setPosAndSize(4,5,height - 10,height - 10);

        search.setPosAndSize(resetTab.width + 5,5,90, height- 10);

        cancel.setPosAndSize(search.posX + search.width + 3,5,height - 10,height - 10);
    }
}
