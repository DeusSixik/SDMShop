package net.sixik.sdmshoprework.client.screen.modern.widget;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractMarketButton;
import net.sixik.v2.color.RGBA;

public class ModernMarketButton extends AbstractMarketButton {
    public ModernMarketButton(Panel panel) {
        super(panel);
        setSize(32,32);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.add(Component.translatable("sidebar_button_sdm.market"));
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0,255/2).drawRoundFill(graphics,x,y,w,h, 6);
    }
}
