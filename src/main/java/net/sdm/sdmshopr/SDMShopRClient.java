package net.sdm.sdmshopr;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.CustomClickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.themes.SDMThemes;
import net.sdm.sdmshopr.themes.ShopTheme;
import org.lwjgl.glfw.GLFW;


public class SDMShopRClient extends SDMShopRCommon {

    public static ShopTheme shopTheme = getTheme();

    public static ShopTheme getTheme(){
        ShopTheme tm = Config.THEMES.get().getTheme();
        if(Config.THEMES.get() == SDMThemes.CUSTOM){
            tm = new ShopTheme(
                    Color4I.fromString(Config.BACKGROUND.get()),
                    Color4I.fromString(Config.SHADOW.get()),
                    Color4I.fromString(Config.REACT.get()),
                    Color4I.fromString(Config.STOKE.get()),
                    Color4I.fromString(Config.TEXTCOLOR.get()),
                    Color4I.fromString(Config.SELCETTABCOLOR.get())
            );
        }
        return tm;
    }

    public static final ResourceLocation OPEN_GUI = new ResourceLocation(SDMShopR.MODID, "open_gui");


    public static final String SDMSHOP_CATEGORY = "key.category.sdmshopr";
    public static final String KEY_NAME = "key.sdmshop.shopr";

    public static KeyMapping KEY_SHOP = new KeyMapping(KEY_NAME, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, SDMSHOP_CATEGORY);



    @Override
    public void preInit() {
        CustomClickEvent.EVENT.register(this::customClick);
        MinecraftForge.EVENT_BUS.addListener(this::keyInput);
    }

    public EventResult customClick(CustomClickEvent event) {
        if (Shop.CLIENT != null && event.id().equals(OPEN_GUI)) {
            new MainShopScreen().openGui();
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }
    public void keyInput(InputEvent.Key event) {
        if (KEY_SHOP.consumeClick() && Shop.CLIENT != null) {
            new MainShopScreen().openGui();
        }
    }

    @Mod.EventBusSubscriber(modid = SDMShopR.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModButton{
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(KEY_SHOP);
        }

        @SubscribeEvent
        public static void clientSetup(final FMLClientSetupEvent event) {

        }
    }
}
