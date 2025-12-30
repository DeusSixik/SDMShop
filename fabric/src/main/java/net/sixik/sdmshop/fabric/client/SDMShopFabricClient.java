package net.sixik.sdmshop.fabric.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.utils.rendering.ShopRenderingComponents;

public final class SDMShopFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {

            context.register(ShopRenderingComponents.ROUNDED_BORDER_SHADER_ID, DefaultVertexFormat.POSITION_TEX, (shaderInstance -> {
                ShopRenderingComponents.ROUNDED_BORDER_SHADER = shaderInstance;
                SDMShop.LOGGER.info("Registered Rounded Border Shader | {}", shaderInstance.getName());
            }));
            context.register(ShopRenderingComponents.BLOCKY_BORDER_SHADER_ID, DefaultVertexFormat.POSITION_TEX, (shaderInstance -> {
                ShopRenderingComponents.BLOCKY_BORDER_SHADER = shaderInstance;
                SDMShop.LOGGER.info("Registered Blocky Border Shader | {}", shaderInstance.getName());
            }));
            context.register(ShopRenderingComponents.BLOCKY_BORDER_BATCHED_SHADER_ID, ShopRenderingComponents.BATCH_FORMAT, (shaderInstance -> {
                ShopRenderingComponents.BLOCKY_BORDER_BATCHED_SHADER = shaderInstance;
                SDMShop.LOGGER.info("Registered Blocky Batched Border Shader | {}", shaderInstance.getName());
            }));
        });
    }
}
