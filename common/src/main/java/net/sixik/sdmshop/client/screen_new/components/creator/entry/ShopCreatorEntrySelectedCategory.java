package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.v2.render.RenderHelper;

public class ShopCreatorEntrySelectedCategory extends Panel {

    protected final ShopCreatorEntryPanel entryPanel;
    protected CategoryButton categoryButton;
    protected TextField selectCategoryText;

    public ShopCreatorEntrySelectedCategory(ShopCreatorEntryPanel panel) {
        super(panel);
        this.entryPanel = panel;
        setHeight(22);
    }

    @Override
    public void addWidgets() {
        add(selectCategoryText = new TextField(this));
        selectCategoryText.setText(Component.translatable("sdm.shop.gui.creator.text.selected_category"));
        add(categoryButton = new CategoryButton(this));
    }

    @Override
    public void alignWidgets() {
        categoryButton.setTab(ShopCreatorComponentData.Data.Entry.selectedTab);

        selectCategoryText.posY = (this.height - Minecraft.getInstance().font.lineHeight) / 2;
        selectCategoryText.posX = 2;

        categoryButton.posX = this.width - categoryButton.width - 2;
        categoryButton.posY = 1;
    }

    @Override
    public boolean shouldDraw() {
        return ShopCreatorComponentModalPanel.Data.Entry.selectedType != null;
    }

    public static boolean isTabSelected() {
        return ShopCreatorComponentData.Data.Entry.selectedTab != null;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawHollowRect(graphics, x, y, w, h, GUIShopMenu.BORDER, false);
    }

    public static class CategoryButton extends SimpleTextButton {

        protected final  ShopCreatorEntrySelectedCategory entrySelectedCategory;

        public CategoryButton(ShopCreatorEntrySelectedCategory panel) {
            super(panel, Component.translatable("sdm.shop.gui.creator.text.select_category"), Icons.BOOK_RED);
            this.entrySelectedCategory = panel;
        }

        public CategoryButton setTab(ShopTab tab) {
            if(tab != null) {
                this.title = tab.title;
                this.icon = Icon.empty();
            }
            return this;
        }

        public CategoryButton(ShopCreatorEntrySelectedCategory panel, ShopTab tab) {
            this(panel);
            setTab(tab);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.translatable("sdm.shop.gui.creator.button.select_category.tooltip"));
        }

        @Override
        public void onClicked(MouseButton button) {
            new ShopCreatorSelectCategoryScreen(entrySelectedCategory.entryPanel::alignWidgetsWithoutEntryTypes).openGui();
        }
    }
}
