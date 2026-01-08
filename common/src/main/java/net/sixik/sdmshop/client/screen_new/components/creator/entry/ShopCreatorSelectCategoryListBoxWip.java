package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.ui.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.categories.ShopSelectCategoriesComponentModalPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.List;
import java.util.Objects;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopCreatorSelectCategoryListBoxWip extends Panel {

    /**
     * TODO: It makes sense to switch to Hash Map if there are 20+ categories on average.
     */
    protected final ObjectArrayList<ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton> availableCategoriesButton =
            new ObjectArrayList<>();
    protected final ShopCreatorSelectCategoryScreen selectedCategoryPanel;
    public int elementW;
    public int borderOffest;
    public int spacing;

    public ShopCreatorSelectCategoryListBoxWip(
            ShopCreatorSelectCategoryScreen panel,
            int borderOffest,
            int spacing
    ) {
        super(panel);
        this.selectedCategoryPanel = panel;
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

            final var button = new ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton(this, category, (b, tab) -> {
                ShopCreatorComponentData.Data.Entry.selectedTab = category;
                selectedCategoryPanel.onSelected.run();
                getGui().closeGui();
            });

            add(button);
            button.width = elementW;
            button.selected = ShopCreatorComponentData.Data.Entry.selectedTab == category;
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

    private String lastSearch = "";

    public void onSearch(String text) {
        final String q = ShopUtils.normalize(text);

        if (Objects.equals(lastSearch, q)) return;

        final boolean showAll = q.isEmpty();

        for (int i = 0; i < availableCategoriesButton.size(); i++) {
            final ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton button = availableCategoriesButton.get(i);

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
