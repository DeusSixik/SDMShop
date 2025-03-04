package net.sixik.sdmshoprework.common.theme;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import net.minecraft.client.gui.GuiGraphics;

public class ShopTheme {

    private Color4I background;
    private Color4I shadow;
    private Color4I react;
    private Color4I stoke;
    private Color4I moneyTextColor;
    private Color4I colorSelectTab;

    public ShopTheme(Color4I background, Color4I shadow, Color4I react, Color4I stoke){
        this.background = background;
        this.shadow = shadow;
        this.react = react;
        this.stoke = stoke;
        this.moneyTextColor = Color4I.WHITE;
        this.colorSelectTab = Color4I.WHITE;
    }
    public ShopTheme(Color4I background, Color4I shadow, Color4I react, Color4I stoke, Color4I moneyTextColor){
        this.background = background;
        this.shadow = shadow;
        this.react = react;
        this.stoke = stoke;
        this.moneyTextColor = moneyTextColor;
        this.colorSelectTab = Color4I.WHITE;
    }
    public ShopTheme(Color4I background, Color4I shadow, Color4I react, Color4I stoke, Color4I moneyTextColor, Color4I colorSelectTab){
        this.background = background;
        this.shadow = shadow;
        this.react = react;
        this.stoke = stoke;
        this.moneyTextColor = moneyTextColor;
        this.colorSelectTab = colorSelectTab;
    }


    public Color4I getMoneyTextColor() {
        return moneyTextColor;
    }

    public Color4I getBackground() {
        return background;
    }

    public Color4I getReact() {
        return react;
    }

    public Color4I getShadow() {
        return shadow;
    }

    public Color4I getStoke() {
        return stoke;
    }

    public Color4I getColorSelectTab() {
        return colorSelectTab;
    }

    public void draw(GuiGraphics graphics, int x, int y, int w, int h) {
        this.getShadow().draw(graphics, x, y, w, h + 4);
        this.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, this.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, this.getStoke(), false);
    }

    public void draw(GuiGraphics graphics, int x, int y, int w, int h, int trans) {
        this.getShadow().withAlpha(trans).draw(graphics, x, y, w, h + 4);
        this.getBackground().withAlpha(trans).draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, this.getReact().withAlpha(trans), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, this.getStoke().withAlpha(trans), false);
    }

    public void drawHollow(GuiGraphics graphics, int x, int y, int w, int h) {
        this.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, this.getReact(), false);
    }
}
