package net.sixk.sdmshop.utils;

import dev.architectury.platform.Platform;
import net.sixk.sdmshop.SDMShop;

public class ShopDebugUtils {

    private static final boolean debug = true;

    public static boolean isDebug() {
        return debug && Platform.isDevelopmentEnvironment();
    }

    public static void log(String string) {
        if(!isDebug()) return;
        SDMShop.LOGGER.info(string);
    }

    public static void log(String string, Object... objects) {
        if(!isDebug()) return;
        SDMShop.LOGGER.info(string, objects);
    }

    public static void error(String string) {
        if(!isDebug()) return;
        SDMShop.LOGGER.error(string);
    }

    public static void error(String string, Object... objects) {
        if(!isDebug()) return;
        SDMShop.LOGGER.error(string, objects);
    }
}
