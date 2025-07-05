package net.sixk.sdmshop.data.config;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;

import java.nio.file.Path;

public class Config {

    public static void init()
    {
        loadConfig(true);
    }

    public static void reload(){
        CONFIG.load(getClientConfig());
    }

    public static void loadConfig(boolean client) {
        ConfigUtil.loadDefaulted(CONFIG,getModFolder(), "sdmshop");
        if(client) {
            ConfigFile.CLIENT.disableKeyBind = DISABLE_KEYBIND.get();
            ConfigFile.CLIENT.style = STYLES.get();
        } else {
            ConfigFile.SERVER.disableKeyBind = DISABLE_KEYBIND.get();
            ConfigFile.SERVER.style = STYLES.get();
        }
    }

    public static Path getModFolder(){
        return Platform.getConfigFolder().resolve("SDMShop");
    }

    public static Path getClientConfig(){
        return getModFolder().resolve("sdmshop-common.snbt");
    }

    public static final SNBTConfig CONFIG;

    //    public static final BooleanValue DISABLE_BUTTON;
    public static final BooleanValue DISABLE_KEYBIND;
    //public static final BooleanValue SEND_NOTIFY;
    public static final BooleanValue STYLES;



    static {
        CONFIG = SNBTConfig.create("sdmshop-common");

        DISABLE_KEYBIND = CONFIG.addBoolean("disable_key_bind", false);
        //SEND_NOTIFY = CONFIG.addBoolean("send_notify", true);
        STYLES = CONFIG.addBoolean("isModern", false);

        SNBTConfig CUSTOM = CONFIG.addGroup("CUSTOM");

    }
}
