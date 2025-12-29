package net.sixik.sdmshop.config;

import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringValue;
import net.sixik.sdmshop.SDMShopPaths;
public class ShopConfig {


    public static void reload(){
        CONFIG.load(SDMShopPaths.getModConfig());
    }

    public static void loadConfig() {
        ConfigUtil.loadDefaulted(CONFIG, SDMShopPaths.getModFolder(), "sdmshop");
    }


    public static final SNBTConfig CONFIG;
    public static final BooleanValue DISABLE_KEYBIND;
    public static final BooleanValue SEND_NOTIFY;
    public static final StringValue DEFAULT_SHOP_ID;
    public static final BooleanValue SHOW_ADMIN_MESSAGES;
    public static final BooleanValue USE_CACHED_SHOP_DATA;


    static {
        CONFIG = SNBTConfig.create("sdmshop-common");

        DISABLE_KEYBIND = CONFIG.addBoolean("disable_key_bind", false);
        SEND_NOTIFY = CONFIG.addBoolean("send_notify", true);
        DEFAULT_SHOP_ID = CONFIG.addString("default_shop_id", "default").comment("The store ID that you specify when creating via /sdmshop create_shop <id>. After specifying the ID, clicking on the store button or the button in the menu will open the current store.");
        SHOW_ADMIN_MESSAGES = CONFIG.addBoolean("show_admin_messages", true);

        var group = CONFIG.addGroup("caching");
        USE_CACHED_SHOP_DATA = group.addBoolean("use_cached_shop_data", true).comment("Allows you to reduce the load on the network by caching store data on the player's client. In this case, the player will be able to copy your store data without any obstacles.");
    }
}
