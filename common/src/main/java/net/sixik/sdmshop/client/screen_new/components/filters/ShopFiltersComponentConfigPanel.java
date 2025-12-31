package net.sixik.sdmshop.client.screen_new.components.filters;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.api.FilterPanelWidget;
import net.sixik.sdmshop.client.screen_new.api.FilterRefreshWidget;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopFiltersComponentConfigPanel extends Panel {

    protected final ShopFiltersComponentModalPanel modalPanel;
    protected @Nullable ShopFiltersComponentTypePanel.Button selectedButton;

    public ShopFiltersComponentConfigPanel(ShopFiltersComponentModalPanel panel) {
        super(panel);
        this.modalPanel = panel;
    }

    @Override
    public void addWidgets() {
        if(selectedButton == null) return;

        final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> list =
                selectedButton.getFilter();

        for (int i = 0; i < list.size(); i++) {
            final AbstractEntryTypeFilter<?> element = list.get(i);

            final TextField field = new TextField(this) {
                @Override
                public void addMouseOverText(TooltipList list) {
                    element.addTooltips(list);
                }
            };
            field.setText(element.getTitle());
            add(field);
            element.addWidget(this);
        }
    }

    @Override
    public void alignWidgets() {
        if (selectedButton == null) return;

        final int paddingX = 8;
        final int paddingY = 8;
        final int gap = 6;
        final int rowGap = 6;
        final int titleToPanelGap = 4;

        final int zoneW = this.width - paddingX * 2;
        int y = paddingY;

        final var ws = getWidgets();
        if (ws.isEmpty() || zoneW <= 0) return;

        // 1) вычисляем ширину колонки Title для "обычных" строк (не FilterPanelWidget)
        int labelW = 0;
        for (int i = 0; i + 1 < ws.size(); i += 2) {
            Widget elem = ws.get(i + 1);
            if (elem instanceof FilterPanelWidget) continue; // не учитываем "панельные" строки

            Widget label = ws.get(i);
            labelW = Math.max(labelW, Math.max(1, label.width));
        }
        labelW = Math.min(labelW, Math.max(80, (int) (zoneW * 0.45f)));

        final int elementW = Math.max(1, zoneW - labelW - gap);

        // 2) высота обычной строки (можно сделать константой)
        int rowH = 12;
        for (int i = 0; i + 1 < ws.size(); i += 2) {
            Widget elem = ws.get(i + 1);
            if (elem instanceof FilterPanelWidget) continue;

            Widget label = ws.get(i);
            rowH = Math.max(rowH, Math.max(label.height, elem.height));
        }

        // 3) раскладываем парами
        for (int i = 0; i + 1 < ws.size(); i += 2) {
            final Widget label = ws.get(i);
            final Widget elem  = ws.get(i + 1);

            if (elem instanceof FilterPanelWidget fpw) {
                // ---- режим: Title сверху, элемент снизу на всю ширину ----

                // Title
                label.setWidth(zoneW);
                label.setHeight(Math.max(12, label.height));
                label.posX = paddingX;
                label.posY = y;

                y += label.height + titleToPanelGap;

                // Panel element
                final int ph = Math.max(1, fpw.getPanelHeight(this.height));
                elem.setWidth(zoneW);
                elem.setHeight(ph);
                elem.posX = paddingX;
                elem.posY = y;

                // если нужно обновить внутреннюю раскладку после размеров
                if (elem instanceof FilterRefreshWidget refreshWidget) {
                    refreshWidget.updateWidget();
                } else if (elem instanceof Panel p) {
                    p.clearWidgets();
                    p.addWidgets();
                    p.alignWidgets(); // на случай, если это Panel без FilterRefreshWidget
                }

                y += ph + rowGap;
                continue;
            }

            // ---- режим: обычная строка (Title слева, элемент справа) ----
            label.setWidth(labelW);
            label.setHeight(rowH);
            label.posX = paddingX;
            label.posY = y + 2;

            elem.setWidth(elementW);
            elem.setHeight(rowH);
            elem.posX = paddingX + labelW + gap;
            elem.posY = y;

            if (elem instanceof FilterRefreshWidget refreshWidget) {
                refreshWidget.updateWidget();
            } else if (elem instanceof Panel p) {
                p.alignWidgets();
            }

            y += rowH + rowGap;
        }
    }


    public void onSelected(final ShopFiltersComponentTypePanel.Button button) {
        this.selectedButton = button;
        clearWidgets();
        addWidgets();
        alignWidgets();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }
}
