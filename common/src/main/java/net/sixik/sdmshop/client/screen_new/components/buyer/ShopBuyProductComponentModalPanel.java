package net.sixik.sdmshop.client.screen_new.components.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopBuyProductComponentModalPanel extends ModalPanel {

    public static ShopBuyProductComponentModalPanel openCentered(final Panel panel, final ShopEntry shopEntry) {
        final var modal = openDefault(panel, shopEntry);
        modal.center = true;

        final int sw = panel.getWidth();
        final int sh = panel.getHeight();

        final int w = modal.getWidth();
        final int h = modal.getHeight();

        modal.setPos((sw - w) / 2, (sh - h) / 2);
        return modal;
    }

    public static ShopBuyProductComponentModalPanel openDefault(final Panel panel, final ShopEntry shopEntry) {
        final BaseScreen gui = panel.getGui();

        final ShopBuyProductComponentModalPanel modal =
                new ShopBuyProductComponentModalPanel(panel, shopEntry);

        modal.setSize(gui.width / 2, (int) (gui.height / 1.5));

        gui.pushModalPanel(modal);

        if (MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalOpen(modal);

        return modal;
    }

    protected final ShopEntry shopEntry;
    protected final String moneyText;
    protected final int textL;
    protected final Icon icon;
    protected boolean center;

    protected int leftPanelW;
    protected int rightPanelW;
    protected int space = 4;

    protected ShopBuyProductComponentModalPanel(
            final Panel panel,
            final ShopEntry shopEntry
    ) {
        super(panel);
        this.shopEntry = shopEntry;

        this.moneyText = this.shopEntry.getEntrySellerType().moneyToString(this.shopEntry);
        this.textL = Theme.DEFAULT.getStringWidth(moneyText);
        this.icon = ShopRenderUtils.getIconFromEntry(this.shopEntry);
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void setWidth(int v) {
        super.setWidth(v);
        rightPanelW = this.width / 3 - space;
        leftPanelW = this.width / 2 + rightPanelW / 2;
    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void onClosed() {

        if (MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalClose(this);

        super.onClosed();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        drawDefaultPanel(graphics, x, y, leftPanelW, h);
        drawDefaultPanel(graphics, x + w - rightPanelW - space, y, rightPanelW, h);
    }

    protected void drawDefaultPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);
        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);
        ShopRenderingWrapper.endBatch();
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        final int iconSize = w / 7;
        final int iconSize3 = iconSize / 3;

        if(!icon.isEmpty())
            icon.draw(graphics, x + iconSize3, y + iconSize3, iconSize, iconSize);

    }
}
