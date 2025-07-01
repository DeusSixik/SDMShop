package net.sixik.sdmshop.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public class ShopInputHelper {

    public static final int INSERT_KEY = GLFW.GLFW_KEY_Z;
    public static final int SWAP_KEY = GLFW.GLFW_KEY_X;

    public static boolean isControl() {
        return Screen.hasControlDown();
    }

    public static boolean isShift() {
        return Screen.hasShiftDown();
    }

    public static boolean isMoveInsert() {
        return isControl() && isKeyDown(INSERT_KEY);
    }

    public static boolean isMoveSwap() {
        return isControl() && isKeyDown(SWAP_KEY);
    }


    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }
}
