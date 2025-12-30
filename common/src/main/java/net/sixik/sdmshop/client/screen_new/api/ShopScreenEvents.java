package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.ui.Widget;
import org.jetbrains.annotations.Nullable;

public interface ShopScreenEvents {

    @FunctionalInterface
    interface OnClickElement {

        void handle(final @Nullable Widget widget);
    }
}
