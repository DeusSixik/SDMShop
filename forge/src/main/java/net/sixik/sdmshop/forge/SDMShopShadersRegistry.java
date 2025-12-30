package net.sixik.sdmshop.forge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.utils.rendering.ShopRenderingComponents;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = SDMShop.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SDMShopShadersRegistry {

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ShopRenderingComponents.ROUNDED_BORDER_SHADER_ID, DefaultVertexFormat.POSITION_TEX), (shaderInstance -> {
            ShopRenderingComponents.ROUNDED_BORDER_SHADER = shaderInstance;
            SDMShop.LOGGER.info("Registered Rounded Border Shader | {}", shaderInstance.getName());
        }));
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ShopRenderingComponents.BLOCKY_BORDER_SHADER_ID, DefaultVertexFormat.POSITION_TEX), (shaderInstance -> {
            ShopRenderingComponents.BLOCKY_BORDER_SHADER = shaderInstance;
            SDMShop.LOGGER.info("Registered Blocky Border Shader | {}", shaderInstance.getName());
        }));
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ShopRenderingComponents.BLOCKY_BORDER_BATCHED_SHADER_ID, ShopRenderingComponents.BATCH_FORMAT), (shaderInstance -> {
            ShopRenderingComponents.BLOCKY_BORDER_BATCHED_SHADER = shaderInstance;
            SDMShop.LOGGER.info("Registered Blocky Batched Border Shader | {}", shaderInstance.getName());
        }));
    }
}
