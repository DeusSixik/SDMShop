package net.sixik.sdmshoprework.common.theme;

import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import org.jetbrains.annotations.Nullable;


public enum SDMThemes {

    SHADOW("shadow", new ShopTheme(Color4I.rgb(74,64,90),Color4I.rgb(53,46,64),Color4I.rgb(214,154,255), Color4I.rgb(32,28,39))),
    PINK("pink", new ShopTheme(Color4I.rgb(245,245,245),Color4I.rgb(231,170,175),Color4I.rgb(239,207,210), Color4I.rgb(148,	109,	112))),
    DESIDERIUM("desiderium", new ShopTheme(Color4I.rgb(62,19,23), Color4I.rgb(69,22,28), Color4I.rgb(148,118,87), Color4I.rgb(60,48,36))),
    VORTEX("vortex",
            new ShopTheme(Color4I.fromString("#362e61"), Color4I.fromString("#231d3a"), Color4I.fromString("#d9892a"), Color4I.fromString("#79362e"), Color4I.fromString("#5c6090"), Color4I.fromString("#5555FF"))
    ),
    CUSTOM("custom", new ShopTheme(Color4I.BLACK, Color4I.BLACK, Color4I.BLACK, Color4I.BLACK, Color4I.WHITE, Color4I.WHITE));

    private String id;
    private ShopTheme theme;
    SDMThemes(String id, ShopTheme theme){
        this.theme =theme;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ShopTheme getTheme() {
        return theme;
    }

    public static @Nullable ShopTheme getThemeFromId(String id){
        for (SDMThemes t :SDMThemes.values()) {
            if(t.id.equals(id)) return t.theme;
        }
        return null;
    }

    public static final NameMap<SDMThemes> NAME_MAP = NameMap.of(SHADOW, values()).baseNameKey("sdmshop.theme").create();

}
