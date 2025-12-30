package net.sixik.sdmshop.client.screen_new.components.categories;

import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopWidgets;
import net.sixik.sdmshop.client.screen_new.components.categories.ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopSelectCategoryListBox extends Panel {

    /**
     * TODO: It makes sense to switch to Hash Map if there are 20+ categories on average.
     */
    protected final ObjectArrayList<SelectCategoriesButton> availableCategoriesButton =
            new ObjectArrayList<>();
    protected final ShopSelectCategoriesComponentModalPanel modalPanel;
    protected final List<ShopTab> selectedCategories;
    public int elementW;
    public int borderOffest;
    public int spacing;

    public ShopSelectCategoryListBox(
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

        final List<ShopTab> list = SDMShopClient.CurrentShop.getTabs();
        for (int i = 0; i < list.size(); i++) {
            final ShopTab category = list.get(i);

            final var button = new SelectCategoriesButton(this, category, (s) -> {
                final Iterator<ShopTab> iterator = selectedCategories.iterator();
                boolean find = false;

                while (iterator.hasNext()) {
                    ShopTab element = iterator.next();
                    if (element == null) continue;
                    if (element.getId().equals(s.getId())) {
                        iterator.remove();
                        find = true;
                        break;
                    }
                }

                if (!find) selectedCategories.add(s);

                modalPanel.updateSelectedList();
            });

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

        final List<Widget> row = new ObjectArrayList<>(16);

        for (int i = 0; i < list.size(); ) {
            row.clear();

            int rowW = 0;
            int rowH = 0;

            while (i < list.size()) {
                final var w = list.get(i);
                i++;

                if (!w.isEnabled()) continue;

                final int wW = org.joml.Math.clamp(w.width, 1, zoneW);
                final int wH = Math.max(1, w.height);

                final int add = row.isEmpty() ? wW : (spacing + wW);

                if (!row.isEmpty() && (rowW + add) > zoneW) {
                    i--;
                    break;
                }

                row.add(w);
                rowW += add;
                rowH = Math.max(rowH, wH);
            }

            if (row.isEmpty()) continue;

            int offsetX = Math.max(0, (zoneW - rowW) / 2);
            int x = startX + offsetX;

            for (int k = 0; k < row.size(); k++) {
                final var w = row.get(k);
                final int wW = org.joml.Math.clamp(w.width, 1, zoneW);

                w.posX = x;
                w.posY = y;

                x += wW + spacing;
            }

            y += rowH + spacing;
        }
    }


    public void updateStats() {
        for (int i = 0; i < availableCategoriesButton.size(); i++) {
            final SelectCategoriesButton button = availableCategoriesButton.get(i);
            button.selected = selectedCategories.contains(button.category);
        }
    }

    private String lastSearch = "";

    public void onSearch(String text) {
        final String q = ShopUtils.normalize(text);

        if (Objects.equals(lastSearch, q)) return;

        final boolean showAll = q.isEmpty();

        for (int i = 0; i < availableCategoriesButton.size(); i++) {
            final SelectCategoriesButton button = availableCategoriesButton.get(i);

            boolean visible;
            if (showAll) {
                visible = true;
            } else {
                final String name = ShopUtils.normalize(button.getTitle().getString());
                visible = ShopUtils.matchesQuery(name, q);
            }

            button.enabled = visible;
        }

        lastSearch = q;
        alignWidgets();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }
}
