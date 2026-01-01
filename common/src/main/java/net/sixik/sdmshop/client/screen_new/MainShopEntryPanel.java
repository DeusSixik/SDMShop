package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.ui.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.mixin.accessors.PanelAccessor;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.mixin.WidgetPath;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.List;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class MainShopEntryPanel extends Panel {

    public static int PADDING = 8;
    public static int SPACING_X = 6;
    public static int SPACING_Y = 6;

    public static int MIN_ITEM_W = 72;

    public static int MAX_COLS = 6;

    public static int FIXED_ITEM_H = 72;

    protected final GUIShopMenu screen;

    protected final PanelAccessor panelAccessor;
    protected ObjectArrayList<MainShopEntryButton> entryButtons;

    public MainShopEntryPanel(GUIShopMenu screen) {
        super(screen.self());
        this.screen = screen;
        this.panelAccessor = (PanelAccessor) this;
    }

    @Override
    public void addWidgets() {
        clearWidgets();

        final List<ShopEntry> list = SDMShopClient.CurrentShop.getEntries();
        for (int i = 0; i < list.size(); i++) {
            final ShopEntry entry = list.get(i);

            if (!shouldShowEntry(entry)) continue;

            final MainShopEntryButton button = new MainShopEntryButton(this, entry);
            add(button);
        }
    }

    protected boolean shouldShowEntry(final ShopEntry entry) {
        return true;
    }

    @Override
    public void alignWidgets() {
        final int padding = PADDING;
        final int spacingX = SPACING_X;
        final int spacingY = SPACING_Y;

        final int zoneW = this.width - padding * 2;
        if (zoneW <= 0) return;

        final var ws = getWidgets();
        if (ws.isEmpty()) return;

        int itemH;
        if (FIXED_ITEM_H > 0) {
            itemH = FIXED_ITEM_H;
        } else {
            itemH = 0;
            for (int i = 0; i < ws.size(); i++) {
                itemH = Math.max(itemH, Math.max(1, ws.get(i).height));
            }
            if (itemH <= 0) itemH = 20;
        }

        final int minItemW = MIN_ITEM_W;
        int cols = Math.max(1, (zoneW + spacingX) / (minItemW + spacingX));
        cols = Math.min(cols, MAX_COLS);

        final int itemW = Math.max(1, (zoneW - (cols - 1) * spacingX) / cols);

        int x = padding;
        int y = padding;
        int col = 0;

        for (int i = 0; i < ws.size(); i++) {
            final Widget w = ws.get(i);

            w.setWidth(itemW);
            w.setHeight(itemH);

            w.posX = x;
            w.posY = y;

            col++;
            if (col >= cols) {
                col = 0;
                x = padding;
                y += itemH + spacingY;
            } else {
                x += itemW + spacingX;
            }
        }

        TextField empty = new TextField(this) {
            @Override public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) { }
        };
        empty.setHeight(20);
        empty.setWidth(this.width);

        if(widgets.isEmpty()) return;
        final var lastW = widgets.get(widgets.size() - 1);
        empty.posY = lastW.posY + lastW.height;
        add(empty);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w ,h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        final var renderInside = getOnlyRenderWidgetsInside();

        drawBackground(graphics, theme, x, y, w, h);

        if (renderInside) {
            GuiHelper.pushScissor(getScreen(), x, y, w, h);
        }

        setOffset(true);

        final var offsetX = panelAccessor.offsetX();
        final var offsetY = panelAccessor.offsetY();

        widgets.stream()
                .filter(widget -> ((WidgetPath)widget).sdm$shouldRenderInLayer(DrawLayer.BACKGROUND, x, y, w, h))
                .forEach(widget -> drawWidget(graphics, theme, widget, x + offsetX, y + offsetY, w, h));

        drawOffsetBackground(graphics, theme, x + offsetX, y + offsetY, w, h);

        if(MainShopScreen.Instance.shouldRenderWidgets) {
            ShopRenderingWrapper.beginBatch(MIN_ITEM_W, FIXED_ITEM_H, CORNER_SIZE, BORDER_WIDTH);
            for (int i = 0; i < widgets.size(); i++) {
                final var widget = widgets.get(i);
                if (!(widget instanceof MainShopEntryButton button)) continue;
                if (!((WidgetPath) widget).sdm$shouldRenderInLayer(DrawLayer.FOREGROUND, x, y, w, h)) continue;
                button.drawBatch(graphics, theme, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
            }
            ShopRenderingWrapper.endBatch();
        }

        widgets.stream()
                .filter(widget -> ((WidgetPath)widget).sdm$shouldRenderInLayer(DrawLayer.FOREGROUND, x, y, w, h))
                .forEach(widget -> drawWidget(graphics, theme, widget, x + offsetX, y + offsetY, w, h));

        setOffset(false);

        if (renderInside) {
            GuiHelper.popScissor(getScreen());
        }
    }
}
