package net.sdm.sdmshopr;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.sun.jna.WString;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.sdm.sdmshopr.themes.SDMThemes;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = SDMShopR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
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

    public static final ForgeConfigSpec.EnumValue<SDMThemes> THEMES;
    public static SDMThemes defaultTheme = SDMThemes.SHADOW;

    public static final ForgeConfigSpec.ConfigValue<String> BACKGROUND;
    public static final ForgeConfigSpec.ConfigValue<String> SHADOW;
    public static final ForgeConfigSpec.ConfigValue<String> REACT;
    public static final ForgeConfigSpec.ConfigValue<String> STOKE;
    public static final ForgeConfigSpec.ConfigValue<String> TEXTCOLOR;
    public static String defaultBackground = "#5555FF";
    public static String defaultShadow = "#5555FF";
    public static String defaultReact = "#5555FF";
    public static String defaultStoke = "#5555FF";
    public static String defaultTextColor = "#5555FF";

    public static final String THEMES_NAME = "Shop Theme";

    static {
        BUILDER.push("THEMES");

        THEMES = BUILDER.defineEnum(THEMES_NAME, defaultTheme);

        BUILDER.push("THEMES_CUSTOM");
        BUILDER.comment("Colors Settings");
        BACKGROUND = BUILDER.define("background", defaultBackground);
        SHADOW = BUILDER.define("shadow", defaultShadow);
        REACT = BUILDER.define("react", defaultReact);
        STOKE = BUILDER.define("stoke", defaultStoke);
        TEXTCOLOR = BUILDER.define("moneyTextColor", defaultTextColor);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

//    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
//
//    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
//            .comment("Whether to log the dirt block on common setup")
//            .define("logDirtBlock", true);
//
//    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
//            .comment("A magic number")
//            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
//    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
//            .comment("What you want the introduction message to be for the magic number")
//            .define("magicNumberIntroduction", "The magic number is... ");
//
//    // a list of strings that are treated as resource locations for items
//    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);
//
//    static final ForgeConfigSpec SPEC = BUILDER.build();
//
//    public static boolean logDirtBlock;
//    public static int magicNumber;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;
//
//    private static boolean validateItemName(final Object obj)
//    {
//        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
//    }
//
//    @SubscribeEvent
//    static void onLoad(final ModConfigEvent event)
//    {
//        logDirtBlock = LOG_DIRT_BLOCK.get();
//        magicNumber = MAGIC_NUMBER.get();
//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream()
//                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
//                .collect(Collectors.toSet());
//    }
}
