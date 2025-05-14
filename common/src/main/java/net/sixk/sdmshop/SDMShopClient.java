package net.sixk.sdmshop;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.shop.ShopPage;
import net.sixk.sdmshop.shop.modern.ShopPageModern;
import org.lwjgl.glfw.GLFW;

public class SDMShopClient {

    public static final String SDMSHOP_CATEGORY = "key.category.sdmshopr";
    public static final String KEY_NAME = "key.sdmshop.shopr";
    public static KeyMapping KEY_SHOP = new KeyMapping(KEY_NAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, SDMSHOP_CATEGORY);

    public static void init(){
        KeyMappingRegistry.register(KEY_SHOP);
        ClientTickEvent.CLIENT_PRE.register((instance -> {
            if (KEY_SHOP.consumeClick()) {
                if(ConfigFile.CLIENT.disableKeyBind) return;
                if(ConfigFile.CLIENT.style)
                    new ShopPageModern().openGui();
                else
                    new ShopPage().openGui();
            }
        }));
    }

    public static Player getPlayer(){
        return Minecraft.getInstance().player;
    }
}
