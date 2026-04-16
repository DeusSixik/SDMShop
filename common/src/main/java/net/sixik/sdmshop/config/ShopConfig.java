package net.sixik.sdmshop.config;

import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.snbt.config.*;
import net.sixik.sdmshop.SDMShopPaths;
public class ShopConfig {

    public enum UIStyle {
        Modern,
        BlockyModern
    }

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
    public static final EnumValue<UIStyle> GUI_STYLE;

    static {
        CONFIG = SNBTConfig.create("sdmshop-common");

        var group = CONFIG.addGroup("server");
        DISABLE_KEYBIND = group.addBoolean("disable_key_bind", false)
                .comment("Determines if the client is allowed to request opening a shop (e.g., via keybindings). If enabled, shops can only be opened by the server using the /sdmshop open_shop <player> <shopId> command. Useful for custom shop implementations.");
        SEND_NOTIFY = group.addBoolean("send_notify", true)
                .comment("Whether to display a notification in chat about purchasing an item.");
        DEFAULT_SHOP_ID = group.addString("default_shop_id", "default")
                .comment("The store ID that you specify when creating via /sdmshop create_shop <id>. After specifying the ID, clicking on the store button or the button in the menu will open the current store.");
        SHOW_ADMIN_MESSAGES = group.addBoolean("show_admin_messages", true)
                .comment("Debugging messages when editing, purchasing, etc. It is recommended to enable them when editing.");

        group = CONFIG.addGroup("client");
        GUI_STYLE = group.addEnum("ui_style", NameMap.of(UIStyle.BlockyModern, UIStyle.values()).create());

        group = group.addGroup("caching");
        USE_CACHED_SHOP_DATA = group.addBoolean("use_cached_shop_data", true).comment("Allows you to reduce the load on the network by caching store data on the player's client. In this case, the player will be able to copy your store data without any obstacles.");
    }
}
