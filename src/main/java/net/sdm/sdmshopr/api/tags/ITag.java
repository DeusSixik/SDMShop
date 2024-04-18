package net.sdm.sdmshopr.api.tags;

import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.themes.SDMThemes;
import net.sdm.sdmshopr.themes.ShopTheme;
import org.jetbrains.annotations.Nullable;

public interface ITag {

    default String getModID(){
        return "minecraft";
    }

    ITag create();

    default String getParent(){
        return "";
    }

    boolean isGlobalTag();

    String getID();

    default boolean isOnlyClient(){
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void executeClient(GuiGraphics graphics, ShopTheme theme, int x, int y, int w, int h){

    }
    default void executeServer(ServerPlayer player){

    }

}
