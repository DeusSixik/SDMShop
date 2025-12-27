package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.old_api.screen.ShopUIRenderComponent;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ShopRenderUtils {

    private static final Font FONT = Minecraft.getInstance().font;

    public static int centerTextX(Widget widget, String text, int x) {
        return centerTextX(widget, FONT.width(text), x);
    }

    public static int centerTextX(Widget widget, int textW, int x) {
        return centerTextX(widget.width, textW, x);
    }

    public static int centerTextX(int panelW, int textW, int x) {
        return x + (panelW - textW) / 2;
    }

    public static int centerTextX(int panelW, String text, int x) {
        return centerTextX(panelW, FONT.width(text), x);
    }

    public static int centerTextX(int panelW, String text, int x, float scale) {
        return centerTextX(panelW, FONT.width(text), x, scale);
    }

    public static int centerTextX(int panelW, int textW, int x, float scale) {
        int scaledTextW = (int) (textW * scale);
        return x + (panelW - scaledTextW) / 2;
    }

    public static int centerTextXFromMiddle(int panelW, String text, int x) {
        return centerTextXFromMiddle(panelW, Theme.DEFAULT.getStringWidth(text), x);
    }


    public static int centerTextXFromMiddle(int panelW, int textW, int x) {
        int panelCenterX = x + panelW / 2;
        int textCenterX = textW / 2;
        return panelCenterX - textCenterX;
    }

    public static void drawLabel(GuiGraphics graphics, Theme theme, Vector2 pos, Vector2 size, String left, String right) {
        int lineHeight = Minecraft.getInstance().font.lineHeight;

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1, 4);

        GLHelper.pushScissor(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1);
        theme.drawString(graphics, left, pos.x + 2, pos.y + 1, Color4I.WHITE, 2);
        GLHelper.popScissor(graphics);

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x + size.x / 2,pos.y, size.x / 2, lineHeight + 1, 4);

        GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, lineHeight + 1);
        theme.drawString(graphics, right, pos.x + size.x / 2 + 2, pos.y + 1, Color4I.WHITE, 2);
        GLHelper.popScissor(graphics);
    }

    public static void drawLabel(GuiGraphics graphics, Theme theme, Vector2 pos, Vector2 size, ShopUIRenderComponent left, ShopUIRenderComponent right) {
        int lineHeight = Minecraft.getInstance().font.lineHeight;

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1, 4);

        GLHelper.pushScissor(graphics, pos.x,pos.y + 1, size.x / 2 - 2, lineHeight + 1);
        left.draw(graphics, theme, pos.x, pos.y, size.x, size.y);
        GLHelper.popScissor(graphics);

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x + size.x / 2,pos.y, size.x / 2, lineHeight + 1, 4);

        GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, lineHeight + 1);
        right.draw(graphics, theme, pos.x + size.x / 2, pos.y + 1, size.x, size.y);
        GLHelper.popScissor(graphics);
    }

//    public void drawNewLabelR(GuiGraphics graphics, Theme theme, Vector2 pos, Vector2 size, String left, String right) {
//        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1, 4);
//
//        GLHelper.pushScissor(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1);
//        theme.drawString(graphics, left, pos.x + 2, pos.y + 1, Color4I.WHITE, 2);
//        GLHelper.popScissor(graphics);
//
//        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x + size.x / 2,pos.y, size.x / 2, lineHeight + 1, 4);
//
//        GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, lineHeight + 1);
//
//        shopEntry.getEntrySellerType().draw(graphics, theme, pos.x + size.x / 2 + 2, pos.y + 1, 0, 16, shopEntry.getPrice() * count, this, - 2);
//
////        shopEntry.shopSellerType.draw(graphics, theme, pos.x + size.x / 2 + 2, pos.y + 1, 0, 16, shopEntry.entryPrice * count, this, - 2);
////        theme.drawString(graphics, right, pos.x + size.x / 2 + 2, pos.y + 1, Color4I.WHITE, 2);
//        GLHelper.popScissor(graphics);
//    }
}
