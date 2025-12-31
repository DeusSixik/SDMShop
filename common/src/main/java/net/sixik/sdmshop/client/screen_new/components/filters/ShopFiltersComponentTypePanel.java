package net.sixik.sdmshop.client.screen_new.components.filters;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.List;
import java.util.function.Function;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopFiltersComponentTypePanel extends Panel {

    private static final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> NULL = List.of();

    protected final ShopFiltersComponentModalPanel modalPanel;
    public Button selectedButton;

    public ShopFiltersComponentTypePanel(ShopFiltersComponentModalPanel panel) {
        super(panel);
        this.modalPanel = panel;
    }

    @Override
    public void addWidgets() {
        ShopEntry entry = new ShopEntry(SDMShopClient.CurrentShop);

        for (Function<ShopEntry, AbstractEntryType> value : ShopContentRegister.getEntryTypes().values()) {
            final AbstractEntryType type = value.apply(entry);
            if(type == null) continue;
            final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filter =
                    SDMShopClient.shopFilters.getOrDefault(type.getClass(), NULL);

            if(!filter.isEmpty()) {
                final Button button = new Button(this, type, filter);
                add(button);
                onSelected(button);
            }
        }
    }

    @Override
    public void alignWidgets() {
        final int wH = this.width - 4;

        final List<Widget> list = getWidgets();

        final int offsetY = 4;

        for (int i = 0; i < list.size(); i++) {
            final Widget w = list.get(i);
            w.setHeight(12);
            w.setPos(2,  offsetY + i * (w.getHeight() + 2));
            w.setWidth(wH);
        }
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }

    public void onSelected(final Button button) {
        final List<Widget> list = getWidgets();
        for (int i = 0; i < list.size(); i++) {
            final Widget widget = list.get(i);
            if(widget instanceof Button b && widget == button) {
                if(!b.selected) {
                    b.selected = true;
                    selectedButton = b;
                    modalPanel.onSelected(selectedButton);
                }
                continue;
            }

            if(widget instanceof Button b) {
                b.selected = false;
            }
        }
    }

    public Button getSelectedButton() {
        return selectedButton;
    }

    public static class Button extends SimpleTextButton {

        protected final ShopFiltersComponentTypePanel typePanel;
        protected final AbstractEntryType type;
        protected final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filter;
        protected boolean selected = false;


        public Button(
                ShopFiltersComponentTypePanel panel,
                AbstractEntryType type,
                List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filter
        ) {
            super(panel, type.getTranslatableForCreativeMenu(), type.getCreativeIcon());
            this.typePanel = panel;
            this.type = type;
            this.filter = filter;
        }

        public Button setSelected(boolean value) {
            this.selected = value;
            return this;
        }

        public List<AbstractEntryTypeFilter<? extends AbstractEntryType>> getFilter() {
            return filter;
        }

        public ShopFiltersComponentTypePanel getTypePanel() {
            return typePanel;
        }

        public AbstractEntryType getType() {
            return type;
        }

        @Override
        public void onClicked(MouseButton button) {
            typePanel.onSelected(this);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if(isMouseOver || selected) {
                ShopRenderingWrapper.drawRoundedRect(
                        graphics.pose(), x, y, w, h, 4, 1, BACKGROUND_INT, BORDER_3_INT
                );
            } else {
                ShopRenderingWrapper.drawRoundedRectNoBorder(
                        graphics.pose(), x, y, w, h, 4, BACKGROUND_INT
                );
            }
        }
    }
}
