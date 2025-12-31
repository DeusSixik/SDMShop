package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopWidgets.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;

/**
 * Filter + Category + Search panel
 */
public class MainShopLeftPanel extends Panel {

    public TextBox field;

    public TextField categoryBoxTitle;
    public EditCategoryButton categoryBoxEditButton;
    public CategoryBox categoryBox;

    protected TextField priceTitle;
    protected TextBox priceBoxFrom;
    protected TextBox priceBoxTo;

    public MainShopLeftPanel(GUIShopMenu screen) {
        super(screen.self());
    }

    @Override
    public void addWidgets() {
        add(field = new SearchBox(this, (s) -> {}));
        add(categoryBoxTitle = new TextField(this));
        add(categoryBox = new CategoryBox(this, 4, 2));
        add(categoryBoxEditButton = new EditCategoryButton(this, Component.literal("Edit"), categoryBox));
        add(priceTitle = new TextField(this));
        add(priceBoxFrom = new TextBox(this));
        add(priceBoxTo = new TextBox(this));
        priceBoxFrom.ghostText = "From";
        priceBoxTo.ghostText = "To";
    }

    @Override
    public void alignWidgets() {
        final int elementSize = this.width - this.width / 6;
        final int centerPosElements = (this.width - elementSize) / 2;
        final int maxCategoryH = this.height / 3;
        final int fontH = Minecraft.getInstance().font.lineHeight;

        field.setWidth(elementSize);
        field.setHeight(12);
        field.posX = centerPosElements;
        field.posY += field.height / 6;

        categoryBoxTitle.setMaxWidth(elementSize);
        categoryBoxTitle.setText(Component.translatable("sdm.shop.gui.box.categories.title"));
        categoryBoxTitle.posX = 6;
        categoryBoxTitle.posY += field.posY + 4;

        categoryBoxEditButton.posX = this.width - categoryBoxEditButton.width - 4;
        categoryBoxEditButton.posY = categoryBoxTitle.posY;
        categoryBoxEditButton.height = categoryBoxTitle.height;

        categoryBox.setWidth(elementSize);
        categoryBox.setHeight(50);
        categoryBox.posX = centerPosElements;
        categoryBox.posY += categoryBoxTitle.posY - categoryBoxTitle.height / 3;

        categoryBox.addWidgets();
        categoryBox.alignWidgets();
        categoryBox.height = maxCategoryH;

        priceTitle.setWidth(elementSize);
        priceTitle.setText("Price");
        priceTitle.posX = 6;
        priceTitle.posY = categoryBox.posY + categoryBox.height + fontH;

        final int priceBW = this.width / 4;
        priceBoxFrom.setWidth(priceBW);
        priceBoxFrom.setHeight(12);
        priceBoxFrom.posY = priceTitle.posY;

        priceBoxTo.setWidth(priceBoxFrom.width);
        priceBoxTo.setHeight(priceBoxFrom.height);
        priceBoxTo.posY = priceBoxFrom.posY;

        final int priceWW = priceBoxFrom.width + priceBoxTo.width + (this.width / 10);
        priceBoxFrom.posX = this.width - priceWW;
        priceBoxTo.posX = priceBoxFrom.posX + priceBoxFrom.width + 2;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w ,h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }
}
