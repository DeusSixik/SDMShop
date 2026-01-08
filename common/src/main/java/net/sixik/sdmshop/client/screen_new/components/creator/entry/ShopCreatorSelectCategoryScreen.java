package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import com.mojang.blaze3d.platform.Window;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopCreatorSelectCategoryScreen extends BaseScreen {

    protected ShopCreatorSelectCategoryListBoxWip selectCategoryListBox;

    public final Runnable onSelected;

    public ShopCreatorSelectCategoryScreen(Runnable onSelected) {
        this.onSelected = onSelected;
    }

    @Override
    public boolean onInit() {

        final Window panel = getScreen();

        final int sw = panel.getWidth();
        final int sh = panel.getHeight();

        final int w = this.getWidth();
        final int h = this.getHeight();

        this.setPos((sw - w) / 2, (sh - h) / 2);
        return super.onInit();
    }

    @Override
    public void addWidgets() {
        add(selectCategoryListBox = new ShopCreatorSelectCategoryListBoxWip(this, 2, 2));
    }

    @Override
    public void alignWidgets() {
        selectCategoryListBox.posY = 8;
        selectCategoryListBox.posX = 4;
        selectCategoryListBox.setWidth(this.width - 8);
        selectCategoryListBox.height = this.height - 16;

        selectCategoryListBox.clearWidgets();
        selectCategoryListBox.addWidgets();
        selectCategoryListBox.alignWidgets();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }
}
