package net.sdm.sdmshopr.themes;

import dev.ftb.mods.ftblibrary.icon.Color4I;

public class ShopTheme {

    private Color4I background;
    private Color4I shadow;
    private Color4I react;
    private Color4I stoke;
    private Color4I moneyTextColor;

    public ShopTheme(Color4I background, Color4I shadow, Color4I react, Color4I stoke){
        this.background = background;
        this.shadow = shadow;
        this.react = react;
        this.stoke = stoke;
        this.moneyTextColor = Color4I.WHITE;
    }
    public ShopTheme(Color4I background, Color4I shadow, Color4I react, Color4I stoke, Color4I moneyTextColor){
        this.background = background;
        this.shadow = shadow;
        this.react = react;
        this.stoke = stoke;
        this.moneyTextColor = moneyTextColor;
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
}
