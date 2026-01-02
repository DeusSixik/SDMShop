package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.filters.ShopFiltersComponentModalPanel;
import net.sixik.sdmshop.utils.rendering.widgets.EnumDropdownWidget;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

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
    protected EnumDropdownWidget<CategorySort> sortDropdown;
    protected MainShopToolPanel toolPanel;
    protected PanelScrollBar toolPanelScroll;

    protected Button moreFiltersButton;

    public MainShopLeftPanel(GUIShopMenu screen) {
        super(screen.self());
    }

    public enum CategorySort {
        PRICE_ASC,
        PRICE_DESC,
        NAME_ASC,
        NAME_DESC
    }

    @Override
    public void addWidgets() {
        add(field = new SearchBox(this, (s) -> {}));
        add(categoryBoxTitle = new TextField(this));
        add(categoryBox = new CategoryBox(this, 4, 2));
        add(categoryBoxEditButton = new EditCategoryButton(this, Component.translatable("sdm.shop.gui.box.categories.edit"), categoryBox));
        add(priceTitle = new TextField(this));
        add(priceBoxFrom = new TextBox(this));
        add(priceBoxTo = new TextBox(this));

        add(moreFiltersButton = new SimpleTextButton(this, Component.translatable("sdm.shop.gui.box.categories.filters.title"), Icons.SETTINGS) {
            @Override
            public void onClicked(MouseButton button) {
                ShopFiltersComponentModalPanel.openCentered(getGui());
            }
        });

        add(toolPanel = new MainShopToolPanel(this));
        add(toolPanelScroll = new PanelScrollBar(this, toolPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(sortDropdown = new EnumDropdownWidget<>(this, CategorySort.class, CategorySort.NAME_ASC)
                .setLabel(v -> switch (v) {
                    case NAME_ASC  -> Component.literal("Name: A → Z");
                    case NAME_DESC -> Component.literal("Name: Z → A");
                    case PRICE_ASC -> Component.literal("Price: Low → High");
                    case PRICE_DESC -> Component.literal("Price: High → Low");
                })
                .onChange(this::applySort));
        priceBoxFrom.ghostText = "From";
        priceBoxTo.ghostText = "To";
    }

    private void applySort(CategorySort mode) {}

    @Override
    public void alignWidgets() {
        final int elementSize = this.width - this.width / 6;
        final int centerPosElements = (this.width - elementSize) / 2;
        final int maxCategoryH = this.height / 3;
        final int fontH = Minecraft.getInstance().font.lineHeight;
        final int fontHD = fontH / 2;
        final int xOffset = 6;
        final int xOffsetM = xOffset * 2;

        field.setWidth(elementSize);
        field.setHeight(12);
        field.posX = centerPosElements;
        field.posY += field.height / 6;

        categoryBoxTitle.setMaxWidth(elementSize);
        categoryBoxTitle.setText(Component.translatable("sdm.shop.gui.box.categories.title"));
        categoryBoxTitle.posX = xOffset;
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

        final int priceTitleOffset = 2;
        priceTitle.setWidth(elementSize);
        priceTitle.setText(Component.translatable("sdm.shop.entry.seller_type.price"));
        priceTitle.posX = xOffset;
        priceTitle.posY = categoryBox.posY + categoryBox.height + fontH + priceTitleOffset;

        final int priceBW = this.width / 4;
        priceBoxFrom.setWidth(priceBW);
        priceBoxFrom.setHeight(12);
        priceBoxFrom.posY = priceTitle.posY - priceTitleOffset;

        priceBoxTo.setWidth(priceBoxFrom.width);
        priceBoxTo.setHeight(priceBoxFrom.height);
        priceBoxTo.posY = priceBoxFrom.posY;

        final int priceWW = priceBoxFrom.width + priceBoxTo.width + (this.width / 10);
        priceBoxFrom.posX = this.width - priceWW;
        priceBoxTo.posX = priceBoxFrom.posX + priceBoxFrom.width + 2;

        sortDropdown.setHeight(12);
        sortDropdown.setWidth(this.width - xOffsetM);

        sortDropdown.posX = xOffset;
        sortDropdown.posY = priceBoxTo.posY + priceBoxTo.height + fontHD;

        moreFiltersButton.setWidth(this.width - xOffsetM);
        moreFiltersButton.setHeight(12);
        moreFiltersButton.posX = (this.width - moreFiltersButton.width) / 2;
        moreFiltersButton.posY = sortDropdown.posY + sortDropdown.height + fontHD;

        toolPanel.width = categoryBox.width;
        toolPanel.height = categoryBox.height - categoryBox.height / 4;
        toolPanel.posX = categoryBox.posX;
        toolPanel.posY = moreFiltersButton.posY + moreFiltersButton.height + fontH;

        toolPanelScroll.setPosAndSize(
                toolPanel.getPosX() + toolPanel.getWidth() - 2,
                toolPanel.getPosY(),
                2,
                toolPanel.getHeight()
        );

        toolPanel.clearWidgets();
        toolPanel.addWidgets();
        toolPanel.alignWidgets();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w ,h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }
}
