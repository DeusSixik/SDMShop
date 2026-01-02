package net.sixik.sdmshop.client.screen_new.components.categories;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.api.GUIShopWidgets;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import java.util.List;
import java.util.function.Consumer;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopSelectCategoriesComponentModalPanel extends ModalPanel {

    protected static final int Offset = 8;

    /**
     * TODO: It makes sense to switch to Hash Map if there are 20+ categories on average.
     */
    protected final List<ShopTab> selectedCategories;
    protected final GUIShopWidgets.CategoryBox categoryBox;
    protected final GUIShopWidgets.EditCategoryButton button;
    protected final Panel gui;
    protected TextField titleField;
    protected int spacing = 2;
    protected int borderOffest = 4;
    protected boolean center;

    protected GUIShopWidgets.SearchBox listSearchBox;
    protected ShopSelectCategoryListBox listBox;
    protected PanelScrollBar listBoxScroll;

    protected TextField selectedListBoxTitle;
    protected Button selectedListBoxClearAllButton;
    protected ShopSelectCategorySelectedListBox selectedListBox;
    protected PanelScrollBar selectedListBoxScroll;

    protected Button closeButton;

    public static ShopSelectCategoriesComponentModalPanel openCentered(
            Panel panel,
            GUIShopWidgets.CategoryBox categoryBox,
            GUIShopWidgets.EditCategoryButton b
    ) {
        final var modal = openDefault(panel, categoryBox, b);
        modal.center = true;

        final int sw = panel.getWidth();
        final int sh = panel.getHeight();

        final int w = modal.getWidth();
        final int h = modal.getHeight();

        modal.setPos((sw - w) / 2, (sh - h) / 2);
        return modal;
    }

    public static ShopSelectCategoriesComponentModalPanel openDefault(
            Panel panel, GUIShopWidgets.CategoryBox categoryBox, GUIShopWidgets.EditCategoryButton button
    ) {
        final BaseScreen gui = panel.getGui();
        final ShopSelectCategoriesComponentModalPanel modal =
                new ShopSelectCategoriesComponentModalPanel(panel, categoryBox, button);
        modal.setWidth(gui.width * 2 / 6);
        modal.setHeight(gui.height * 4 / 4);
        gui.pushModalPanel(modal);

        if(MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalOpen(modal);

        return modal;
    }

    protected ShopSelectCategoriesComponentModalPanel(
            Panel panel,
            GUIShopWidgets.CategoryBox categoryBox,
            GUIShopWidgets.EditCategoryButton button
    ) {
        super(panel);
        this.gui = panel;
        this.categoryBox = categoryBox;
        this.button = button;
        this.selectedCategories = categoryBox.getSelectedCategories();
    }

    @Override
    public void addWidgets() {
        add(titleField = new TextField(this));
        add(listBox = new ShopSelectCategoryListBox(this, selectedCategories, borderOffest, spacing));
        add(listSearchBox = new GUIShopWidgets.SearchBox(this, this::onSearch));
        add(listBoxScroll = new PanelScrollBar(this, listBox) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(selectedListBoxTitle = new TextField(this));
        add(selectedListBoxClearAllButton = new SimpleTextButton(this, Component.translatable("sdm.shop.gui.box.categories.edit.selected_list.clear_button"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                selectedCategories.clear();
                updateSelectedList();
            }
        });
        add(selectedListBox = new ShopSelectCategorySelectedListBox(this, selectedCategories, borderOffest, spacing));
        add(selectedListBoxScroll = new PanelScrollBar(this, selectedListBox) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(closeButton = new SimpleTextButton(this, Component.translatable("sdm.shop.entry.creator.back"), Icons.BACK) {
            @Override
            public void onClicked(MouseButton button) {
                parent.getGui().popModalPanel();
            }
        });

        listSearchBox.ghostText = I18n.get("sdm.shop.gui.box.categories.edit.search");
    }

    @Override
    public void alignWidgets() {
        final int oW = this.width - Offset * 2;
        final int fontH = Minecraft.getInstance().font.lineHeight;

        titleField.setMaxWidth(this.width);
        titleField.setText(Component.translatable("sdm.shop.gui.box.categories.edit.title"));
        titleField.posY = 4;
        titleField.posX = (this.width - titleField.width) / 2;

        listSearchBox.width = oW;
        listSearchBox.height = 12;
        listSearchBox.posX = Offset;
        listSearchBox.posY = titleField.posY + fontH * 2;

        listBox.width = oW;
        listBox.height = (int) (this.height / 2.5);
        listBox.posX = Offset;
        listBox.posY = listSearchBox.posY + listSearchBox.height + fontH;

        updateList();

        selectedListBoxTitle.setWidth(oW);
        selectedListBoxTitle.setText(I18n.get("sdm.shop.gui.box.categories.edit.selected_list.title"));
        selectedListBoxTitle.posX = Offset;
        selectedListBoxTitle.posY = listBox.posY + listBox.height + fontH * 2;

        selectedListBox.width = oW;
        selectedListBox.height = this.height / 4;
        selectedListBox.posX = Offset;
        selectedListBox.posY = selectedListBoxTitle.posY + selectedListBoxTitle.height + 2;

        updateSelectedList();

        selectedListBoxClearAllButton.posX = this.width - (selectedListBoxClearAllButton.width + Offset);
        selectedListBoxClearAllButton.posY = selectedListBoxTitle.posY;
        selectedListBoxClearAllButton.height = selectedListBoxTitle.height;

        listBoxScroll.setPosAndSize(
                listBox.getPosX() + listBox.getWidth() - 2,
                listBox.getPosY(),
                2,
                listBox.getHeight()
        );

        selectedListBoxScroll.setPosAndSize(
                selectedListBox.getPosX() + selectedListBox.getWidth() - 2,
                selectedListBox.getPosY(),
                2,
                selectedListBox.getHeight()
        );

        final int freeStartY = selectedListBox.posY + selectedListBox.height + spacing;

        final int freeEndY = this.height - Offset;

        closeButton.posX = (this.width - closeButton.width) / 2;
        closeButton.posY = freeStartY + Math.max(0, (freeEndY - freeStartY - closeButton.height) / 2);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }

    @Override
    public void onClosed() {
        categoryBox.selectNewCategories(selectedCategories);

        if(MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalClose(this);


        super.onClosed();
    }

    public void updateList() {
        this.listBox.clearWidgets();
        this.listBox.addWidgets();
        this.listBox.alignWidgets();
        updateStatsOnLists();
    }

    public void updateSelectedList() {
        this.selectedListBox.clearWidgets();
        this.selectedListBox.addWidgets();
        this.selectedListBox.alignWidgets();
        updateStatsOnLists();
    }

    public void updateStatsOnLists() {
        this.listBox.updateStats();
        this.selectedListBox.updateStats();
    }

    public void onSearch(String text) {
        this.listBox.onSearch(text);
    }

    public static class AddedCategoriesButton extends SimpleTextButton {

        protected final ShopTab tab;

        public AddedCategoriesButton(Panel panel, ShopTab tab) {
            super(panel, tab.title, Icons.CLOSE);
            this.tab = tab;
        }

        @Override
        public void onClicked(MouseButton button) {

        }

        public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            drawBackground(graphics, theme, x, y, w, h);

            final int padding = 4;
            final int s = Math.max(8, h / 2);
            final int iconY = y + (h - s) / 2;

            FormattedText title = getTitle();

            int textX = x;
            int textY = y + (h - theme.getFontHeight() + 1) / 2;

            int sw = theme.getStringWidth(title);

            int mw = w - 6 - (hasIcon() ? (padding + s + padding) : 0);

            if (sw > mw) {
                sw = mw;
                title = theme.trimStringToWidth(title, mw);
            }

            if (renderTitleInCenter()) {
                textX += (mw - sw + 6) / 2;
            } else {
                textX += 4;
            }

            if (hasIcon()) {
                final int iconX = x + w - padding - s;
                drawIcon(graphics, theme, iconX, iconY, s, s);
            }

            theme.drawString(graphics, title, textX, textY,
                    theme.getContentColor(getWidgetType()), Theme.SHADOW);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.pose(), x, y, w, h, 5, GUIShopMenu.INPUT_BOX_INT);
        }
    }

    public static class SelectCategoriesButton extends GUIShopWidgets.CategoryBox.Button {

        public boolean selected = false;
        public boolean enabled = true;

        public SelectCategoriesButton(
               final Panel panel,
               final ShopTab category,
               final Consumer<ShopTab> onClick
        ) {
            super(panel, category, onClick);
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if(!enabled) return;
            super.draw(graphics, theme, x, y, w, h);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if(isMouseOver) ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_4_INT);
            else if(selected) ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_3_INT);
            else super.drawBackground(graphics, theme, x, y, w, h);
        }
    }
}
