package net.sixik.sdmshop.utils.rendering;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

class ShopRenderingImpl {

    public static void beginBatch(
            final float width,
            final float height,
            final float cornerSize,
            final float borderWidth
    ) {
        final ShaderInstance shader = ShopRenderingComponents.BLOCKY_BORDER_BATCHED_SHADER;
        if (shader == null) return;


        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(() -> shader);

        safeSetUniform(shader, "u_Size", width, height);
        safeSetUniform(shader, "u_CornerSize", cornerSize);
        safeSetUniform(shader, "u_BorderWidth", borderWidth);

        Tesselator.getInstance().getBuilder().begin(VertexFormat.Mode.QUADS, ShopRenderingComponents.BATCH_FORMAT);
    }

    public static void addBatchRect(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float width,
            final float height,
            final int fillColor,
            final int borderColor
    ) {
        final Matrix4f matrix = poseStack.last().pose();
        final BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        int fA = (fillColor >> 24) & 0xFF;
        if (fA == 0) fA = 255;
        final int fR = (fillColor >> 16) & 0xFF;
        final int fG = (fillColor >> 8) & 0xFF;
        final int fB = (fillColor & 0xFF);

        int bA = (borderColor >> 24) & 0xFF;
        if (bA == 0) bA = 255;
        final int bR = (borderColor >> 16) & 0xFF;
        final int bG = (borderColor >> 8) & 0xFF;
        final int bB = (borderColor & 0xFF);

        writeVertex(buffer, matrix, x, y + height, fR, fG, fB, fA, 0, 1, bR, bG, bB, bA);
        writeVertex(buffer, matrix, x + width, y + height, fR, fG, fB, fA, 1, 1, bR, bG, bB, bA);
        writeVertex(buffer, matrix, x + width, y, fR, fG, fB, fA, 1, 0, bR, bG, bB, bA);
        writeVertex(buffer, matrix, x, y, fR, fG, fB, fA, 0, 0, bR, bG, bB, bA);
    }

    private static void writeVertex(
            final BufferBuilder buf,
            final Matrix4f mat,
            final float x,
            final float y,
            final int r,
            final int g,
            final int b,
            final int a,
            final float u,
            final float v,
            final int bR,
            final int bG,
            final int bB,
            final int bA
    ) {
        buf.vertex(mat, x, y, 0);
        buf.color(r, g, b, a);
        buf.uv(u, v);

        /*
            Overlay (UV1) stores the Red and Green strokes
         */
        buf.overlayCoords(bR, bG);

        /*
            Lightmap (UV2) stores the Blue and Alpha strokes
         */
        buf.uv2(bB, bA);
        buf.endVertex();
    }

    public static void endBatch() {
        Tesselator.getInstance().end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawBlockyRect(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float width,
            final float height,
            final float cornerSize,
            final float borderWidth,
            int fillColor,
            int borderColor
    ) {
        final ShaderInstance shader = ShopRenderingComponents.BLOCKY_BORDER_SHADER;
        if (shader == null) return;

        final Matrix4f matrix = poseStack.last().pose();

        // GL State setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(() -> shader);

        // Fix Alphas
        if ((fillColor & 0xFF000000) == 0 && fillColor != 0) fillColor |= 0xFF000000;
        if ((borderColor & 0xFF000000) == 0 && borderColor != 0) borderColor |= 0xFF000000;

        // Setup Uniforms
        safeSetUniform(shader, "u_Size", width, height);
        safeSetUniform(shader, "u_CornerSize", cornerSize);
        safeSetUniform(shader, "u_BorderWidth", borderWidth);
        safeSetColorUniform(shader, "u_FillColor", fillColor);
        safeSetColorUniform(shader, "u_BorderColor", borderColor);

        // Draw Quad
        final Tesselator tesselator = Tesselator.getInstance();
        final BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, 0).uv(0, 1).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0).uv(1, 1).endVertex();
        buffer.vertex(matrix, x + width, y, 0).uv(1, 0).endVertex();
        buffer.vertex(matrix, x, y, 0).uv(0, 0).endVertex();
        tesselator.end();

        // Restore State
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedRect(PoseStack poseStack, float x, float y, float width, float height,
                                       float radius, float borderWidth, int color, int borderColor) {
        drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, color, color, color, color, borderColor);
    }

    public static void drawRoundedGradientRect(PoseStack poseStack, float x, float y, float width, float height,
                                               float radius, float borderWidth, int colorTop, int colorBottom, int borderColor) {
        drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, colorTop, colorTop, colorBottom, colorBottom, borderColor);
    }

    public static void drawRoundedCornerRect(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float width,
            final float height,
            final float radius,
            final float borderWidth,
            int cTL,
            int cTR,
            int cBL,
            int cBR,
            int borderColor
    ) {
        final ShaderInstance shader = ShopRenderingComponents.ROUNDED_BORDER_SHADER;
        if (shader == null) return;

        final Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(() -> shader);

        // Fix Alphas
        if ((cTL & 0xFF000000) == 0) cTL |= 0xFF000000;
        if ((cTR & 0xFF000000) == 0) cTR |= 0xFF000000;
        if ((cBL & 0xFF000000) == 0) cBL |= 0xFF000000;
        if ((cBR & 0xFF000000) == 0) cBR |= 0xFF000000;
        if ((borderColor & 0xFF000000) == 0) borderColor |= 0xFF000000;

        // Setup Uniforms
        safeSetUniform(shader, "u_Size", width, height);
        safeSetUniform(shader, "u_Radius", radius);
        safeSetUniform(shader, "u_BorderWidth", borderWidth);
        safeSetColorUniform(shader, "u_BorderColor", borderColor);

        safeSetColorUniform(shader, "u_ColorTL", cTL);
        safeSetColorUniform(shader, "u_ColorTR", cTR);
        safeSetColorUniform(shader, "u_ColorBL", cBL);
        safeSetColorUniform(shader, "u_ColorBR", cBR);

        final Tesselator tesselator = Tesselator.getInstance();
        final BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(matrix, x, y + height, 0).uv(0, 1).endVertex(); // BL
        buffer.vertex(matrix, x + width, y + height, 0).uv(1, 1).endVertex(); // BR
        buffer.vertex(matrix, x + width, y, 0).uv(1, 0).endVertex(); // TR
        buffer.vertex(matrix, x, y, 0).uv(0, 0).endVertex(); // TL

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawDirectionalGradientRect(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float width,
            final float height,
            final float radius,
            final float borderWidth,
            int colorStart,
            int colorEnd,
            int borderColor,
            final float angleDeg
    ) {
        final ShaderInstance shader = ShopRenderingComponents.ROUNDED_BORDER_SHADER;
        if (shader == null) return;

        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(() -> shader);

        if ((colorStart & 0xFF000000) == 0) colorStart |= 0xFF000000;
        if ((colorEnd & 0xFF000000) == 0) colorEnd |= 0xFF000000;
        if ((borderColor & 0xFF000000) == 0) borderColor |= 0xFF000000;

        safeSetUniform(shader, "u_Size", width, height);
        safeSetUniform(shader, "u_Radius", radius);
        safeSetUniform(shader, "u_BorderWidth", borderWidth);
        safeSetColorUniform(shader, "u_BorderColor", borderColor);

        safeSetIntUniform(shader, "u_GradientType", 1);
        safeSetUniform(shader, "u_Angle", (float) Math.toRadians(angleDeg));

        safeSetColorUniform(shader, "u_ColorTL", colorStart);
        safeSetColorUniform(shader, "u_ColorBR", colorEnd);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(matrix, x, y + height, 0).uv(0, 1).endVertex(); // Низ-Лево
        buffer.vertex(matrix, x + width, y + height, 0).uv(1, 1).endVertex(); // Низ-Право
        buffer.vertex(matrix, x + width, y, 0).uv(1, 0).endVertex(); // Верх-Право
        buffer.vertex(matrix, x, y, 0).uv(0, 0).endVertex(); // Верх-Лево

        tesselator.end();

        safeSetIntUniform(shader, "u_GradientType", 0);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void safeSetUniform(
            final ShaderInstance shaderInstance,
            final String name,
            final float... values
    ) {
        final Uniform uniform = shaderInstance.getUniform(name);
        if (uniform != null) uniform.set(values);
    }

    private static void safeSetIntUniform(
            final ShaderInstance shaderInstance,
            final String name,
            final int value
    ) {
        final Uniform uniform = shaderInstance.getUniform(name);
        if (uniform != null) uniform.set(value);
    }

    private static void safeSetColorUniform(
            final ShaderInstance shaderInstance,
            final String name,
            final int color
    ) {
        final Uniform uniform = shaderInstance.getUniform(name);
        if (uniform != null) {
            float a = ((color >> 24) & 0xFF) / 255f;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            uniform.set(r, g, b, a);
        }
    }
}
