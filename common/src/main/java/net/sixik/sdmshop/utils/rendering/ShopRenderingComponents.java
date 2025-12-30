package net.sixik.sdmshop.utils.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import org.jetbrains.annotations.Nullable;

public class ShopRenderingComponents {

    public static final ResourceLocation BLOCKY_BORDER_SHADER_ID = new ResourceLocation(SDMShop.MODID, "blocky_border");
    public static final ResourceLocation BLOCKY_BORDER_BATCHED_SHADER_ID = new ResourceLocation(SDMShop.MODID, "blocky_border_batched");
    public static final ResourceLocation ROUNDED_BORDER_SHADER_ID = new ResourceLocation(SDMShop.MODID, "rounded_border");

    public static @Nullable ShaderInstance BLOCKY_BORDER_SHADER;
    public static @Nullable ShaderInstance BLOCKY_BORDER_BATCHED_SHADER;
    public static @Nullable ShaderInstance ROUNDED_BORDER_SHADER;

    public static final VertexFormat BATCH_FORMAT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
            .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
            .put("Color", DefaultVertexFormat.ELEMENT_COLOR)      // Fill Color
            .put("UV0", DefaultVertexFormat.ELEMENT_UV0)          // Shape UV (0..1)
            .put("UV1", DefaultVertexFormat.ELEMENT_UV1)          // Border Color (Red, Green) - use as 2 short
            .put("UV2", DefaultVertexFormat.ELEMENT_UV2)          // Border Color (Blue, Alpha) - use as 2 short
            .put("Padding", DefaultVertexFormat.ELEMENT_PADDING)  // Byte alignment
            .build());

    public static boolean isShaderLoaded() {
        return BLOCKY_BORDER_SHADER != null
                && BLOCKY_BORDER_BATCHED_SHADER != null
                && ROUNDED_BORDER_SHADER != null;
    }
}
