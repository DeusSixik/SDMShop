package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.categories.ShopSelectCategoriesComponentModalPanel;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GUIShopWidgets {

    public static class SearchBox extends TextBox {

        protected final Consumer<String> onTyped;

        public SearchBox(Panel panel, Consumer<String> onTyped) {
            super(panel);
            this.onTyped = onTyped;
            this.ghostText = I18n.get("sdm.shop.gui.box.search.ghost_text");
        }

        @Override
        public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            ShopRenderingWrapper.drawRoundedRectNoBorder(
                    graphics.pose(), x, y, w, h, 6, GUIShopMenu.INPUT_BOX_INT
            );
        }

        @Override
        public void onTextChanged() {
            onTyped.accept(getText());
        }
    }

    public static class EditCategoryButton extends SimpleTextButton {

        private CategoryBox categoryBox;

        public EditCategoryButton(
                Panel panel,
                Component txt,
                CategoryBox categoryBox
        ) {
            super(panel, txt, Icons.SETTINGS);
            this.categoryBox = categoryBox;
        }

        @Override
        public void onClicked(MouseButton button) {
            ShopSelectCategoriesComponentModalPanel.openCentered(getGui(), categoryBox, this);
        }
    }

    public static class CategoryBox extends Panel {

        protected int borderOffest;
        protected int spacing;
        protected ObjectArrayList<ShopTab> selectedCategories = new ObjectArrayList<>();

        public CategoryBox(Panel panel) {
            this(panel, 2);
        }

        public CategoryBox(Panel panel, int spacing) {
            this(panel, 0, spacing);
        }

        public CategoryBox(Panel panel, int borderOffest, int spacing) {
            super(panel);
            this.borderOffest = borderOffest;
            this.spacing = spacing;
        }

        public void selectNewCategories(List<ShopTab> categories) {
            addWidgets();
            alignWidgets();
        }

        @Override
        public void addWidgets() {
            clearWidgets();

            final int size = (this.width - borderOffest * 2) / 2 - spacing;

            final ObjectArrayList<ShopTab> list = selectedCategories;

            if(!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    final Button button = new Button(
                            this,
                            list.get(i),
                            (s) -> {
                            }) {
                        @Override
                        public boolean checkMouseOver(int mouseX, int mouseY) {
                            return false;
                        }
                    };
                    add(button);
                    button.width = Math.min(button.width, size);
                }
            } else {
                final Button button = new Button(
                        this,
                        null,
                        (s) -> {
                        }) {
                    @Override
                    public boolean checkMouseOver(int mouseX, int mouseY) {
                        return false;
                    }
                };
                add(button);
                button.width = Math.min(button.width, size);
            }
        }

        @Override
        public void alignWidgets() {
            final int zoneW = this.width - borderOffest * 2;
            final int startX = borderOffest;
            final int startY = borderOffest;

            final var list = getWidgets();
            if (list.isEmpty() || zoneW <= 0) return;

            int x = startX;
            int y = startY;

            int rowH = 0;
            int line = 0;

            for (int i = 0; i < list.size(); i++) {
                final var w = list.get(i);

                final int wW = org.joml.Math.clamp(w.width, 1, this.width);
                final int wH = Math.max(1, w.height);

                if (x != startX && (x - startX + wW) > zoneW) {
                    x = startX;
                    y += rowH + spacing;
                    rowH = 0;
                    line++;
                }

                w.posX = x;
                w.posY = y;

                x += wW + spacing;
                rowH = Math.max(rowH, wH);
            }
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            ShopRenderingWrapper.beginBatch(w, h, 1, 1);

            ShopRenderingWrapper.addBatchRect(graphics, x - 1, y - 1, w + 2, h + 2, GUIShopMenu.EMPTY_INT, GUIShopMenu.BORDER_INT);

            ShopRenderingWrapper.endBatch();
        }

        public List<ShopTab> getSelectedCategories() {
            return selectedCategories;
        }

        public List<ShopTab> getCategories() {
           return SDMShopClient.CurrentShop.getTabs();
        }

        public static class Button extends SimpleTextButton {

            protected final Panel categoryBox;
            public final Consumer<ShopTab> onClick;
            public final ShopTab category;

            public Button(
                    Panel panel,
                    @Nullable ShopTab category,
                    Consumer<ShopTab> onClick
            ) {
                super(panel, category == null ? Component.translatable("sdm.shop.gui.box.categories.empty_element") : category.title, Icon.empty());
                this.categoryBox = panel;
                this.category = category;
                this.onClick = onClick;
            }

            @Override
            public void onClicked(MouseButton button) {
                onClick.accept(category);
            }

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                this.drawBackground(graphics, theme, x, y, w, h);

                int s = h >= 20 ? 16 : 8;
                int off = (h - s) / 2;

                FormattedText title = getTitle();

                int textX = x;
                int mw = w - (hasIcon() ? off + s : 0) - 6;

                // ---- measure ----
                int rawW = theme.getStringWidth(title);

                // ---- scale (max -30%) + trim if still too long ----
                float scale = 1.0f;
                if (rawW > mw && mw > 0) {
                    float required = (float) mw / (float) rawW; // 0..1

                    if (required >= 0.7f) {
                        // можно уместить одним только скейлом
                        scale = required;
                    } else {
                        // скейлим до минимума 0.7 и дальше режем текст
                        scale = 0.7f;
                        int maxRawWidth = (int) Math.floor(mw / scale);
                        title = theme.trimStringToWidth(title, Math.max(0, maxRawWidth));
                        rawW = theme.getStringWidth(title);
                    }
                }

                float scaledW = rawW * scale;

                // ---- X align (учитываем scaledW) ----
                if (renderTitleInCenter()) {
                    textX += (int) ((mw - scaledW + 6) / 2.0f);
                } else {
                    textX += 4;
                }

                // ---- icon ----
                if (hasIcon()) {
                    drawIcon(graphics, theme, x + off, y + off, s, s);
                    textX += off + s;
                }

                // ---- Y align (учитываем scale) ----
                int fontH = theme.getFontHeight();
                int scaledFontH = (int) Math.ceil(fontH * scale);
                int textY = y + (h - scaledFontH + 1) / 2;

                Color4I color = isMouseOver() ? NordColors.SNOW_STORM_3 : NordColors.SNOW_STORM_1;

                // ---- draw (with optional scaling) ----
                if (scale != 1.0f) {
                    graphics.pose().pushPose();
                    graphics.pose().translate(textX, textY, 0.0f);
                    graphics.pose().scale(scale, scale, 1.0f);
                    theme.drawString(graphics, title, 0, 0, color, 0);
                    graphics.pose().popPose();
                } else {
                    theme.drawString(graphics, title, textX, textY, color, 0);
                }
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.pose(), x, y, w, h, 5, GUIShopMenu.INPUT_BOX_INT);
            }
        }
    }
}
