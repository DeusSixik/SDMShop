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


    static {
        CONFIG = SNBTConfig.create("sdmshop-common");

        DISABLE_KEYBIND = CONFIG.addBoolean("disable_key_bind", false);
        SEND_NOTIFY = CONFIG.addBoolean("send_notify", true);
        DEFAULT_SHOP_ID = CONFIG.addString("default_shop_id", "default");
    }
}
