package net.sdm.sdmshopr.api;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sdm.sdmshopr.client.buyer.BuyerScreen;

public interface ICustomEntryType extends IEntryType{


    void addWidgets(BuyerScreen panel);
    void alignWidgets(BuyerScreen panel);
    default void drawBackground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h){}
    default void drawOffsetBackground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h){}
    default void drawForeground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h){}

    default void onConfirm() {}
    default void onCancel() {}
}
