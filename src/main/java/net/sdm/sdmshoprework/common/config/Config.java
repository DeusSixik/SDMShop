package net.sdm.sdmshoprework.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.sdm.sdmshoprework.common.theme.SDMThemes;
import net.sdm.sdmshoprework.common.theme.ShopStyle;

import java.nio.file.Path;

public class Config {

    public static void init(Path file)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(file)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        SPEC.setConfig(configData);
    }
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<ShopStyle> STYLE;
    public static final ForgeConfigSpec.EnumValue<SDMThemes> THEMES;
    public static SDMThemes defaultTheme = SDMThemes.SHADOW;

    public static final ForgeConfigSpec.ConfigValue<String> BACKGROUND;
    public static final ForgeConfigSpec.ConfigValue<String> SHADOW;
    public static final ForgeConfigSpec.ConfigValue<String> REACT;
    public static final ForgeConfigSpec.ConfigValue<String> STOKE;
    public static final ForgeConfigSpec.ConfigValue<String> TEXTCOLOR;
    public static final ForgeConfigSpec.ConfigValue<String> SELCETTABCOLOR;
    public static String defaultBackground = "#5555FF";
    public static String defaultShadow = "#5555FF";
    public static String defaultReact = "#5555FF";
    public static String defaultStoke = "#5555FF";
    public static String defaultTextColor = "#5555FF";
    public static String colorSelectTab = "#5555FF";

    public static final String THEMES_NAME = "Shop Theme";
    public static final String STYLE_NAME = "LEGACY";

    static {
        BUILDER.push("THEMES");


        STYLE = BUILDER.defineEnum(STYLE_NAME, ShopStyle.LEGACY);
        THEMES = BUILDER.defineEnum(THEMES_NAME, defaultTheme);

        BUILDER.push("THEMES_CUSTOM");
        BACKGROUND = BUILDER.define("background", defaultBackground);
        SHADOW = BUILDER.define("shadow", defaultShadow);
        REACT = BUILDER.define("react", defaultReact);
        STOKE = BUILDER.define("stoke", defaultStoke);
        SELCETTABCOLOR = BUILDER.define("select_tab_color", colorSelectTab);
        TEXTCOLOR = BUILDER.define("moneyTextColor", defaultTextColor);


        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
