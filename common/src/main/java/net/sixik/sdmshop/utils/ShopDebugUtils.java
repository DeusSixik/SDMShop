package net.sixik.sdmshop.utils;

import dev.architectury.platform.Platform;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;

public class ShopDebugUtils {

    public static boolean isDebug() {
        return SDMShopConstants.SHOP_DEBUG_MODE && Platform.isDevelopmentEnvironment();
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
