package net.sixik.sdmshop.client.screen_new.components.categories;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.Iterator;
import java.util.List;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopSelectCategorySelectedListBox extends Panel {

    /**
     * TODO: It makes sense to switch to Hash Map if there are 20+ categories on average.
     */
    protected final ObjectArrayList<ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton> availableCategoriesButton =
            new ObjectArrayList<>();
    protected final ShopSelectCategoriesComponentModalPanel modalPanel;
    protected final List<ShopTab> selectedCategories;
    public int elementW;
    public int borderOffest;
    public int spacing;

    public ShopSelectCategorySelectedListBox(
            ShopSelectCategoriesComponentModalPanel panel,
            List<ShopTab> selectedCategories,
            int borderOffest,
            int spacing
    ) {
        super(panel);
        this.modalPanel = panel;
        this.selectedCategories = selectedCategories;
        this.borderOffest = borderOffest;
        this.spacing = spacing;
    }

    @Override
    public void addWidgets() {
        availableCategoriesButton.clear();
        this.elementW = this.width / 3;

        final List<ShopTab> list = selectedCategories;
        for (int i = 0; i < list.size(); i++) {
            final ShopTab category = list.get(i);

            final var button = new ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton(this, category) {
                @Override
                public void onClicked(MouseButton button) {
                    final Iterator<ShopTab> iterator = selectedCategories.iterator();
                    boolean find = false;

                    while (iterator.hasNext()) {
                        ShopTab element = iterator.next();
                        if (element == null) continue;
                        if (element.getId().equals(tab.getId())) {
                            iterator.remove();
                            find = true;
                            break;
                        }
                    }

                    if (!find) selectedCategories.add(tab);
                    modalPanel.updateSelectedList();
                }
            };

            add(button);
            button.width = elementW;
            availableCategoriesButton.add(button);
        }
    }

    /*
        TODO: Move this to a separate method or widget.
     */
    @Override
    public void alignWidgets() {
        final int zoneW = this.width - borderOffest * 2;
        final int startX = borderOffest;
        final int startY = borderOffest;

        final var list = availableCategoriesButton;
        if (list.isEmpty() || zoneW <= 0) return;

        int y = startY;
        int i = 0;

        while (i < list.size()) {
            int rowStart = i;
            int rowW = 0;
            int rowH = 0;

            while (i < list.size()) {
                final var w = list.get(i);

                final int wW = org.joml.Math.clamp(w.width, 1, zoneW);
                final int wH = Math.max(1, w.height);

                final int add = (rowW == 0) ? wW : (spacing + wW);
                if (rowW > 0 && (rowW + add) > zoneW) break;

                rowW += add;
                rowH = Math.max(rowH, wH);
                i++;
            }

            int offsetX = Math.max(0, (zoneW - rowW) / 2);
            int x = startX + offsetX;

            for (int j = rowStart; j < i; j++) {
                final var w = list.get(j);

                final int wW = org.joml.Math.clamp(w.width, 1, zoneW);
                w.posX = x;
                w.posY = y;

                x += wW + spacing;
            }

            y += rowH + spacing;
        }
    }

    public void updateStats() {

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }
}
