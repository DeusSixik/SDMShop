package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.List;
import java.util.function.Consumer;

public class MainShopToolPanel extends Panel {


    public int borderOffest = 2;
    public int spacing = 2;

    public MainShopToolPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        if(ShopUtils.isEditModeClient()) {
            add(new ToolButton(this, Icons.ADD, () -> {
                ShopCreatorComponentModalPanel.openCentered(getGui());
            }, (tooltipList -> {
                tooltipList.add(Component.literal("Create"));
            })));
        }

        add(new ToolButton(this, Icons.MONEY, () -> {

        }, tooltipList -> {
            tooltipList.add(Component.literal("Money"));
        }));

        add(new ToolButton(this, Icons.STAR, () -> {

        }, tooltipList -> {
            tooltipList.add(Component.literal("Favorite"));
        }));
    }

    @Override
    public void alignWidgets() {
        final int ITEM_W = width / 4;
        final int ITEM_H = ITEM_W;

        for (Widget widget : widgets) {
            widget.setSize(ITEM_W, ITEM_H);
        }

        final int zoneW = this.width - borderOffest * 2;
        if (zoneW <= 0) return;

        final int startX = borderOffest;
        final int startY = borderOffest;

        final var list = widgets;
        if (list.isEmpty()) return;

        int y = startY;

        final List<Widget> row = new ObjectArrayList<>(16);

        for (int i = 0; i < list.size();) {
            row.clear();

            int rowW = 0;
            int rowH = 0;

            while (i < list.size()) {
                final var w = list.get(i++);
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

            final int offsetX = Math.max(0, (zoneW - rowW) / 2);
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

    public static class ToolButton extends SimpleTextButton {

        protected final Runnable onClick;
        protected final Consumer<TooltipList> onTooltip;

        public ToolButton(Panel panel, Component component, Runnable onClick, Consumer<TooltipList> onTooltip) {
            this(panel, component, Icon.empty(), onClick, onTooltip);
        }

        public ToolButton(Panel panel, Icon icon, Runnable onClick, Consumer<TooltipList> onTooltip) {
            this(panel, Component.empty(), icon, onClick, onTooltip);
        }

        public ToolButton(Panel panel, Component component, Icon icon, Runnable onClick, Consumer<TooltipList> onTooltip) {
            super(panel, component, icon);
            this.onTooltip = onTooltip;
            this.onClick = onClick;
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            if(onTooltip == null) return;
            onTooltip.accept(list);
        }

        @Override
        public void onClicked(MouseButton button) {
            if(button.isLeft()) {
                onClick.run();
            }
        }

        @Override
        public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            drawBackground(graphics, theme, x, y, w, h);

            final int s = (h >= 16) ? 16 : 8;
            final int off = (h - s) / 2;

            FormattedText title = getTitle();

            int sw = (title == null) ? 0 : theme.getStringWidth(title);
            final boolean hasTitle = sw > 0;
            final boolean hasIcon = hasIcon();

            if (hasIcon && !hasTitle) {
                final int ix = x + (w - s) / 2;
                final int iy = y + (h - s) / 2;
                drawIcon(graphics, theme, ix, iy, s, s);
                return;
            }

            final int textY = y + (h - theme.getFontHeight() + 1) / 2;
            final int iconBlockW = hasIcon ? (off + s) : 0;
            final int mw = w - iconBlockW - 6;

            if (hasTitle && sw > mw) {
                title = theme.trimStringToWidth(title, mw);
                sw = mw;
            }

            int textX;

            final boolean centerTitle = (!hasIcon) || renderTitleInCenter();

            if (centerTitle) {
                if (hasIcon) {
                    textX = x + iconBlockW + (mw - sw + 6) / 2;
                } else {
                    textX = x + (w - sw) / 2;
                }
            } else {
                textX = x + 4 + iconBlockW;
            }

            if (hasIcon) {
                drawIcon(graphics, theme, x + off, y + off, s, s);
            }

            if (hasTitle) {
                theme.drawString(graphics, title, textX, textY,
                        theme.getContentColor(getWidgetType()), Theme.SHADOW);
            }
        }
    }
}
