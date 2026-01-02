package net.sixik.sdmshop.utils.rendering.widgets;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public class IconTooltipWidget extends Widget {

    protected final Icon icon;
    protected final Consumer<TooltipList> listConsumer;

    public IconTooltipWidget(Panel p, Icon icon, Consumer<TooltipList> listConsumer) {
        super(p);
        this.icon = icon;
        this.listConsumer = listConsumer;
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        listConsumer.accept(list);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if (icon.isEmpty()) return;
        icon.draw(graphics, x, y, w, h);
    }
}
