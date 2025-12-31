package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.ui.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.api.ShopScreenEvents;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class MainShopScreen extends BaseScreen implements GUIShopMenu {

    public static MainShopScreen Instance;

    private MainShopLeftPanel leftPanel;
    private MainShopEntryPanel entryPanel;
    private PanelScrollBar entryPanelScroll;

    private double entryPanelScrollSafe = 0;

    public ObjectArrayList<ShopScreenEvents.OnModalOpen> modalOpenEventListeners = new ObjectArrayList<>();
    public ObjectArrayList<ShopScreenEvents.OnModalClose> modalCloseEventListeners = new ObjectArrayList<>();

    public MainShopScreen() {
        SDMShopClient.shopFilters = GUIShopMenu.createFilters();

        getModalOpenListeners().add((s) -> {
            entryPanel.renderWidgets = false;
        });

        getModalCloseListeners().add((s) -> {
            entryPanel.renderWidgets = true;
        });
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

        if(entryPanelScroll != null) {
            entryPanelScrollSafe = entryPanelScroll.getValue();
        }

        Instance = this;

        return super.onInit();
    }

    @Override
    public void addWidgets() {
        add(this.leftPanel = new MainShopLeftPanel(this));
        add(this.entryPanel = new MainShopEntryPanel(this));
        add(this.entryPanelScroll = new PanelScrollBar(this, entryPanel) {
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
        leftPanel.setWidth(width / 4);
        leftPanel.setHeight(this.height);

        entryPanel.setWidth(this.width - leftPanel.width - 2);
        entryPanel.setHeight(this.height);
        entryPanel.posX = leftPanel.width + 2;

        entryPanelScroll.setPosAndSize(
                entryPanel.getPosX() + entryPanel.getWidth() - 2,
                entryPanel.getPosY(),
                2,
                entryPanel.getHeight()
        );

        entryPanelScroll.setValue(entryPanelScrollSafe);

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
    public ObjectArrayList<ShopScreenEvents.OnModalOpen> getModalOpenListeners() {
        return modalOpenEventListeners;
    }

    @Override
    public ObjectArrayList<ShopScreenEvents.OnModalClose> getModalCloseListeners() {
        return modalCloseEventListeners;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }

    public void onModalOpen(final ModalPanel panel) {
        for (int i = 0; i < modalOpenEventListeners.size(); i++) {
            modalOpenEventListeners.get(i).handle(panel);
        }
    }

    public void onModalClose(final ModalPanel panel) {
        for (int i = 0; i < modalCloseEventListeners.size(); i++) {
            modalCloseEventListeners.get(i).handle(panel);
        }
    }

    public static BaseShop getShop() {
        return SDMShopClient.CurrentShop;
    }
}
