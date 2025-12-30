package net.sixik.sdmshop.utils.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public class ShopRenderingWrapper {

    /**
     * Initializes the batch rendering pipeline for blocky-border elements.
     * <p>
     * This method performs the following setup:
     * <ol>
     * <li>Enables Blending and disables Depth Test (optimized for UI rendering).</li>
     * <li>Binds the custom shader and a required dummy texture.</li>
     * <li>Uploads geometric properties to the shader as <b>Uniforms</b>.</li>
     * <li>Starts the {@link com.mojang.blaze3d.vertex.BufferBuilder} with the custom {@link ShopRenderingComponents#BATCH_FORMAT}.</li>
     * </ol>
     * * <b>Important Constraint:</b> Since {@code width}, {@code height}, {@code cornerSize}, and {@code borderWidth}
     * are passed as global Uniforms, <u>every element drawn in this batch must share these exact dimensions</u>.
     * To draw elements of different sizes, you must call {@link #endBatch()} and start a new batch.
     *
     * @param width       The unified width for all elements in this batch (in pixels).
     * @param height      The unified height for all elements in this batch (in pixels).
     * @param cornerSize  The size of the corner "cut" (notch) in pixels.
     *                    Use {@code 0.0f} for sharp corners, {@code >0.0f} for the blocky cut effect.
     * @param borderWidth The thickness of the border line in pixels. Set to {@code 0.0f} to disable the border.
     */
    public static void beginBatch(
            final float width, final float height,
            final float cornerSize, final float borderWidth
    ) {
        ShopRenderingImpl.beginBatch(width, height, cornerSize, borderWidth);
    }

    /**
     * Convenience method to add a rectangle to the current batch using {@link GuiGraphics}.
     * <p>
     * This simply extracts the {@link PoseStack} and delegates to the main {@link #addBatchRect} method.
     *
     * @param graphics    The current GuiGraphics instance.
     * @param x           The X screen coordinate.
     * @param y           The Y screen coordinate.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param fillColor   The inner fill color (ARGB).
     * @param borderColor The border stroke color (ARGB).
     */
    public static void addBatchRect(
            final GuiGraphics graphics,
            final float x, final float y,
            final float width, final float height,
            final int fillColor, final int borderColor
    ) {
        addBatchRect(graphics.pose(), x, y, width, height, fillColor, borderColor);
    }

    /**
     * Adds a single rectangle geometry to the active batch buffer.
     * <p>
     * <b>Technical Detail:</b> Since standard Vertex Formats only support one color attribute,
     * this method uses a custom packing strategy:
     * <ul>
     * <li><b>Fill Color:</b> Passed via the standard {@code Color} attribute.</li>
     * <li><b>Border Color:</b> Split and packed into custom UV channels.
     * Red/Green components are mapped to {@code UV1} (Overlay),
     * and Blue/Alpha components are mapped to {@code UV2} (Lightmap).
     * </li>
     * </ul>
     *
     * @param poseStack   The matrix stack used for positioning (takes current transform into account).
     * @param x           The X screen coordinate.
     * @param y           The Y screen coordinate.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param fillColor   The inner fill color (ARGB). If Alpha is 0, it defaults to 255.
     * @param borderColor The border stroke color (ARGB). If Alpha is 0, it defaults to 255.
     * @throws IllegalStateException if {@link #beginBatch} was not called first.
     */
    public static void addBatchRect(
            final PoseStack poseStack,
            final float x, final float y,
            final float width, final float height,
            final int fillColor, final int borderColor
    ) {
        ShopRenderingImpl.addBatchRect(poseStack, x, y, width, height, fillColor, borderColor);
    }

    /**
     * Finishes the current batch rendering process.
     * <p>
     * This method uploads the accumulated geometry to the GPU, issues the Draw Call via {@link com.mojang.blaze3d.vertex.Tesselator},
     * and restores the {@link com.mojang.blaze3d.systems.RenderSystem} state (re-enables Depth Test and Depth Mask)
     * to prevent visual glitches in subsequent vanilla rendering.
     */
    public static void endBatch() {
        ShopRenderingImpl.endBatch();
    }

    /**
     * Renders a rounded rectangle filled with a linear gradient at a specific angle.
     *
     * @param poseStack   The matrix stack for positioning.
     * @param x           The X screen coordinate.
     * @param y           The Y screen coordinate.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param radius      The corner radius in pixels.
     * @param borderWidth The thickness of the border. Set to 0 to disable.
     * @param colorStart  The starting color of the gradient (ARGB).
     * @param colorEnd    The ending color of the gradient (ARGB).
     * @param borderColor The color of the border (ARGB).
     * @param angleDeg    The angle of the gradient in degrees (0 = Right, 90 = Down, 180 = Left, 270 = Up).
     */
    public static void drawDirectionalGradientRect(
            PoseStack poseStack,
            final float x, final float y,
            final float width, final float height,
            final float radius, final float borderWidth,
            int colorStart, int colorEnd, int borderColor,
            final float angleDeg) {
        ShopRenderingImpl.drawDirectionalGradientRect(poseStack, x, y, width, height, radius, borderWidth, colorStart, colorEnd, borderColor, angleDeg);
    }

    /**
     * Renders a rounded rectangle with a solid fill color and a border.
     * <p>
     * This is a convenience wrapper for {@link #drawRoundedCornerRect}.
     *
     * @param poseStack   The matrix stack for positioning.
     * @param x           The X screen coordinate.
     * @param y           The Y screen coordinate.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param radius      The corner radius in pixels.
     * @param borderWidth The thickness of the border.
     * @param color       The solid fill color (ARGB).
     * @param borderColor The color of the border (ARGB).
     */
    public static void drawRoundedRect(
            PoseStack poseStack,
            float x, float y,
            float width, float height,
            float radius, float borderWidth,
            int color, int borderColor) {
        ShopRenderingImpl.drawRoundedRect(poseStack, x, y, width, height, radius, borderWidth, color, borderColor);
    }

    /**
     * Renders a rounded rectangle with a vertical gradient (Top to Bottom).
     *
     * @param poseStack   The matrix stack for positioning.
     * @param x           The X screen coordinate.
     * @param y           The Y screen coordinate.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param radius      The corner radius in pixels.
     * @param borderWidth The thickness of the border.
     * @param colorTop    The color at the top of the rectangle (ARGB).
     * @param colorBottom The color at the bottom of the rectangle (ARGB).
     * @param borderColor The color of the border (ARGB).
     */
    public static void drawRoundedGradientRect(
            PoseStack poseStack,
            float x, float y,
            float width, float height,
            float radius, float borderWidth,
            int colorTop, int colorBottom, int borderColor) {
        ShopRenderingImpl.drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, colorTop, colorTop, colorBottom, colorBottom, borderColor);
    }

    /**
     * Renders a rounded rectangle with full control over each corner's color.
     * This allows for complex bilinear interpolation gradients.
     *
     * @param poseStack   The matrix stack for positioning.
     * @param x           The X screen coordinate.
     * @param y           The Y screen coordinate.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param radius      The corner radius in pixels.
     * @param borderWidth The thickness of the border.
     * @param cTL         Color for Top-Left corner (ARGB).
     * @param cTR         Color for Top-Right corner (ARGB).
     * @param cBL         Color for Bottom-Left corner (ARGB).
     * @param cBR         Color for Bottom-Right corner (ARGB).
     * @param borderColor The color of the border (ARGB).
     */
    public static void drawRoundedCornerRect(
            PoseStack poseStack,
            float x, float y,
            float width, float height,
            float radius, float borderWidth,
            int cTL, int cTR, int cBL, int cBR,
            int borderColor
    ) {
        ShopRenderingImpl.drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, cTL, cTR, cBL, cBR, borderColor);
    }

    public static void drawRoundedRectNoBorder(
            PoseStack poseStack, float x, float y, float width, float height,
            float radius, int color
    ) {
        drawRoundedCornerRectNoBorder(poseStack, x, y, width, height, radius, color, color, color, color);
    }

    public static void drawRoundedGradientRectNoBorder(
            PoseStack poseStack, float x, float y, float width, float height,
            float radius, int colorTop, int colorBottom
    ) {
        drawRoundedCornerRectNoBorder(poseStack, x, y, width, height, radius, colorTop, colorTop, colorBottom, colorBottom);
    }

    public static void drawRoundedCornerRectNoBorder(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float width,
            final float height,
            final float radius,
            int cTL, int cTR, int cBL, int cBR
    ) {
        ShopRenderingImpl.drawRoundedCornerRectNoBorder(poseStack, x, y, width, height, radius, cTL, cTR, cBL, cBR);
    }

    public static void drawDirectionalGradientRectNoBorder(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float width,
            final float height,
            final float radius,
            int colorStart,
            int colorEnd,
            final float angleDeg
    ) {
        ShopRenderingImpl.drawDirectionalGradientRectNoBorder(poseStack, x, y, width, height, radius, colorStart, colorEnd, angleDeg);
    }
}
