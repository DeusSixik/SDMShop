package net.sixik.sdmshoprework.common.theme;

import dev.ftb.mods.ftblibrary.config.NameMap;

public enum ShopStyle {
    LEGACY,
    MODERN;

    public static final NameMap<ShopStyle> NAME_MAP = NameMap.of(MODERN, values()).baseNameKey("sdmshop.style").create();

}
