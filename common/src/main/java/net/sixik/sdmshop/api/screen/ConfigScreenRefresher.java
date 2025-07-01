package net.sixik.sdmshop.api.screen;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import net.minecraft.client.Minecraft;

@Deprecated
public interface ConfigScreenRefresher {

    void refreshAndSafe(ConfigGroup group);

    static void refreshIfOpened(ConfigGroup group) {
        if (Minecraft.getInstance().screen instanceof ScreenWrapper w && w instanceof ConfigScreenRefresher refreshSupport)
            refreshSupport.refreshAndSafe(group);
    }
}
