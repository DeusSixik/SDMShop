package net.sixik.sdmshop.api;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

public interface CustomIcon {

    @Nullable Icon getCustomIcon(ShopEntry entry, int tick);
}
