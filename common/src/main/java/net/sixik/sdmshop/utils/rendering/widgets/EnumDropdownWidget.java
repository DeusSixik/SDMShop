package net.sixik.sdmshop.utils.rendering.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.lwjgl.glfw.GLFW;

public class EnumDropdownWidget<E extends Enum<E>> extends Widget {

    private final Class<E> enumClass;
    private final E[] values;

    private E value;

    private boolean open;
    private int itemHeight = 12;
    private int maxVisibleItems = 8;

    private java.util.function.Function<E, Component> label = e -> Component.literal(e.name());
    private java.util.function.Consumer<E> onChange = e -> {
    };

    public EnumDropdownWidget(Panel p, Class<E> enumClass, E initial) {
        super(p);
        this.enumClass = enumClass;
        this.values = enumClass.getEnumConstants();
        this.value = initial;
    }

    public EnumDropdownWidget<E> setLabel(java.util.function.Function<E, Component> label) {
        this.label = label != null ? label : (e -> Component.literal(e.name()));
        return this;
    }

    public EnumDropdownWidget<E> onChange(java.util.function.Consumer<E> cb) {
        this.onChange = cb != null ? cb : (e -> {
        });
        return this;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E v) {
        if (v == null || v == value) return;
        value = v;
        onChange.accept(v);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean v) {
        open = v;
    }

    @Override
    public Component getTitle() {
        return label.apply(value);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawButton(graphics, x, y, w, h, getWidgetType());
        Component t = label.apply(value);
        theme.drawString(graphics, t, x + 4, y + (h - theme.getFontHeight() + 1) / 2,
                theme.getContentColor(getWidgetType()), Theme.SHADOW);

        theme.drawString(graphics, Component.literal(open ? "▲" : "▼"),
                x + w - 10, y + (h - theme.getFontHeight() + 1) / 2,
                theme.getContentColor(getWidgetType()), 0);

        if (!open) return;

        final var pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 200);

        final int listX = x;
        final int listY = y + h + 2;
        final int visible = Math.min(values.length, maxVisibleItems);
        final int listH = visible * itemHeight;

        theme.drawPanelBackground(graphics, listX, listY, w, listH);

        int mouseX = getMouseX();
        int mouseY = getMouseY();

        for (int i = 0; i < visible; i++) {
            int iy = listY + i * itemHeight;
            boolean hover = mouseX >= listX && mouseX < listX + w && mouseY >= iy && mouseY < iy + itemHeight;

            if (hover) {
                ShopRenderingWrapper.drawRoundedRect(
                        graphics.pose(), listX, iy, w, itemHeight, 2, 1, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_3_INT
                );
            }

            Component it = label.apply(values[i]);
            theme.drawString(graphics, it, listX + 4, iy + (itemHeight - theme.getFontHeight() + 1) / 2,
                    theme.getContentColor(getWidgetType()), 0);
        }
        pose.popPose();
    }

    @Override
    public boolean mousePressed(MouseButton button) {
        if (!isEnabled()) return false;

        int x = getX();
        int y = getY();

        if (isMouseOver()) {
            open = !open;
            playClickSound();
            return true;
        }

        if (open) {
            int listX = x;
            int listY = y + height + 2;
            int visible = Math.min(values.length, maxVisibleItems);
            int listH = visible * itemHeight;

            int mx = getMouseX();
            int my = getMouseY();

            boolean insideList = mx >= listX && mx < listX + width && my >= listY && my < listY + listH;
            if (insideList) {
                int idx = (my - listY) / itemHeight;
                if (idx >= 0 && idx < visible) {
                    setValue(values[idx]);
                    open = false;
                    playClickSound();
                    return true;
                }
            } else {
                open = false;
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(Key key) {
        if (!open) return false;

        if (key.keyCode == GLFW.GLFW_KEY_ESCAPE) {
            open = false;
            return true;
        }
        return false;
    }
}
