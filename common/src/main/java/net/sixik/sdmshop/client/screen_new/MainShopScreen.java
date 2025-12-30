package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.shop.BaseShop;

public class MainShopScreen extends BaseScreen implements GUIShopMenu {

    private MainShopLeftPanel leftPanel;

    public MainShopScreen() {

    }

    @Override
    public boolean onInit() {
        if (getShop() == null) return false;

        final int sw = getScreen().getGuiScaledWidth();
        final int sh = getScreen().getGuiScaledHeight();

        final int margin = 10;

        final int availW = Math.max(1, sw - margin * 2);
        final int availH = Math.max(1, sh - margin * 2);

        final int w = (int) (availW * 0.95f);
        final int h = (int) (availH * 0.95f);

        setWidth(w);
        setHeight(h);

        setX(margin + (availW - w) / 2);
        setY(margin + (availH - h) / 2);

        closeContextMenu();
        return super.onInit();
    }

    @Override
    public void addWidgets() {
        add(this.leftPanel = new MainShopLeftPanel(this));
    }

    @Override
    public void alignWidgets() {
        leftPanel.setWidth(width / 4);
        leftPanel.setHeight(this.height);

        for (Widget widget : getWidgets()) {
            if(widget instanceof Panel panel)
                panel.alignWidgets();
        }
    }

    @Override
    public BaseScreen self() {
        return this;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }

    public static BaseShop getShop() {
        return SDMShopClient.CurrentShop;
    }
}
