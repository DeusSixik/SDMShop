package net.sixik.sdmshop.old_api.screen;

import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import net.minecraft.client.Minecraft;

import java.util.Objects;

public interface RefreshSupport {

    void onRefresh();

    static void refreshIfOpened() {
        if (Minecraft.getInstance().screen instanceof ScreenWrapper w && w instanceof RefreshSupport refreshSupport)
            refreshSupport.onRefresh();
    }

    static void refreshIfOpened(Class<? extends RefreshSupport> ref) {
        if (Minecraft.getInstance().screen instanceof ScreenWrapper w && w instanceof RefreshSupport refreshSupport) {
            if(Objects.equals(w.getGui().getClass(), ref))
                refreshSupport.onRefresh();
        }
    }
}
