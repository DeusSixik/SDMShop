package net.sixik.sdmshop.client.screen_new.components.filters;

import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopFiltersComponentModalPanel extends ModalPanel {

    public static ShopFiltersComponentModalPanel openCentered(
            Panel panel
    ) {
        final var modal = openDefault(panel);
        modal.center = true;

        final int sw = panel.getWidth();
        final int sh = panel.getHeight();

        final int w = modal.getWidth();
        final int h = modal.getHeight();

        modal.setPos((sw - w) / 2, (sh - h) / 2);
        return modal;
    }

    public static ShopFiltersComponentModalPanel openDefault(final Panel panel) {
        final BaseScreen gui = panel.getGui();
        final ShopFiltersComponentModalPanel modal = new ShopFiltersComponentModalPanel(panel);
        modal.setWidth(gui.width * 3 / 5);
        modal.setHeight(gui.height * 4 / 4);
        gui.pushModalPanel(modal);

        if(MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalOpen(modal);

        return modal;
    }

    protected boolean center = false;
    protected ShopFiltersComponentTypePanel typePanel;
    protected PanelScrollBar typePanelScroll;

    protected ShopFiltersComponentConfigPanel configPanel;
    protected PanelScrollBar configPanelScroll;

    protected ShopFiltersComponentModalPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(typePanel = new ShopFiltersComponentTypePanel(this));
        add(typePanelScroll = new PanelScrollBar(this, typePanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(configPanel = new ShopFiltersComponentConfigPanel(this));
        add(configPanelScroll = new PanelScrollBar(this, configPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });
    }

    @Override
    public void alignWidgets() {
        typePanel.width = this.width / 3;
        typePanel.height = this.height - 2;
        typePanel.posX = this.width - typePanel.width;

        typePanelScroll.setPosAndSize(
                typePanel.getPosX() + typePanel.getWidth() - 2,
                typePanel.getPosY(),
                2,
                typePanel.getHeight()
        );

        typePanel.clearWidgets();
        typePanel.addWidgets();
        typePanel.alignWidgets();

        configPanel.width = this.width - typePanel.width - 2;
        configPanel.height = typePanel.height;

        configPanelScroll.setPosAndSize(
                configPanel.getPosX() + configPanel.getWidth() - 2,
                configPanel.getPosY(),
                2,
                configPanel.getHeight()
        );

        configPanel.clearWidgets();
        configPanel.addWidgets();
        configPanel.alignWidgets();
    }

    @Override
    public void onClosed() {

        if(MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalClose(this);

        super.onClosed();
    }

    public void onSelected(final ShopFiltersComponentTypePanel.Button button) {
        configPanel.onSelected(button);
    }
}
