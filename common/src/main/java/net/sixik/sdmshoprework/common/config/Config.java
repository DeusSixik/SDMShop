package net.sixik.sdmshoprework.common.config;

import dev.ftb.mods.ftblibrary.snbt.config.*;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.common.theme.SDMThemes;
import net.sixik.sdmshoprework.common.theme.ShopStyle;

import java.nio.file.Path;

public class Config {

    public static void init(Path file)
    {
//        ConfigUtil.loadDefaulted(CONFIG, file, "sdmshop");
        loadConfig(true);
    }

    public static void reload(){
        CONFIG.load(SDMShopPaths.getClientConfig());
    }

    public static void loadConfig(boolean client) {
        ConfigUtil.loadDefaulted(CONFIG, SDMShopPaths.getModFolder(), "sdmshop");
        if(client) {
            ConfigFile.CLIENT.disableKeyBind = DISABLE_KEYBIND.get();
        } else {
            ConfigFile.SERVER.disableKeyBind = DISABLE_KEYBIND.get();
        }
    }

    public static final EnumValue<ShopStyle> STYLE;
    public static final EnumValue<SDMThemes> THEMES;


    public static final SNBTConfig CONFIG;
    public static final StringValue BACKGROUND;
    public static final StringValue SHADOW;
    public static final StringValue REACT;
    public static final StringValue STOKE;
    public static final StringValue TEXTCOLOR;
    public static final StringValue SELCETTABCOLOR;
//    public static final BooleanValue DISABLE_BUTTON;
    public static final BooleanValue DISABLE_KEYBIND;


    public static String defaultBackground = "#5555FF";
    public static String defaultShadow = "#5555FF";
    public static String defaultReact = "#5555FF";
    public static String defaultStoke = "#5555FF";
    public static String defaultTextColor = "#5555FF";
    public static String colorSelectTab = "#5555FF";


    public static final String THEMES_NAME = "Shop Theme";
    public static final String STYLE_NAME = "Shop Style";

    static {
        CONFIG = SNBTConfig.create("sdmshop-common");

        DISABLE_KEYBIND = CONFIG.addBoolean("disable_key_bind", false);

        STYLE = CONFIG.addEnum(STYLE_NAME, ShopStyle.NAME_MAP);
        THEMES = CONFIG.addEnum(THEMES_NAME, SDMThemes.NAME_MAP);

        SNBTConfig CUSTOM = CONFIG.addGroup("CUSTOM");
        BACKGROUND = CUSTOM.addString("background", defaultBackground);
        SHADOW = CUSTOM.addString("shadow", defaultShadow);
        REACT = CUSTOM.addString("react", defaultReact);
        STOKE = CUSTOM.addString("stoke", defaultStoke);
        TEXTCOLOR = CUSTOM.addString("select_tab_color", defaultTextColor);
        SELCETTABCOLOR = CUSTOM.addString("moneyTextColor", colorSelectTab);
    }
}
