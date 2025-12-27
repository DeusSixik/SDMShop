package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IconRenderSupport {

    @Environment(EnvType.CLIENT)
    Icon getIcon();
}
