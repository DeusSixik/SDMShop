package net.sdm.sdmshoprework.common.utils;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.misc.RGB;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import org.joml.Matrix4f;

public class RenderTest {

    public static void drawTest(GuiGraphics graphics, int cX, int cY, int radius, RGB rgb) {
        int r = rgb.r;
        int g = rgb.g;
        int b = rgb.b;
        int a = 255;
        if (rgb instanceof RGBA rgba) {
            a = rgba.a;
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        Matrix4f m = graphics.pose().last().pose();

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(m, cX, cY, 0).color(r, g, b, a).endVertex(); // Центр дуги

        for (int i = 270; i >= 180; i -= 5) {
            double angle = Math.toRadians(i);
            float x = (float) (Math.cos(angle) * radius) + cX;
            float y = (float) (Math.sin(angle) * radius) + cY;
            bufferBuilder.vertex(m, x, y, 0).color(r, g, b, a).endVertex();
        }

        tesselator.end();

    }


}
