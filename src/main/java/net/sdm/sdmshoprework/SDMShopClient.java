package net.sdm.sdmshoprework;


import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftblibrary.ui.CustomClickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sdm.sdmshoprework.client.screen.legacy.LegacyShopScreen;
import net.sdm.sdmshoprework.common.config.Config;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.common.theme.SDMThemes;
import net.sdm.sdmshoprework.common.theme.ShopTheme;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SDMShopRework.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SDMShopClient {

    public static ClientShopData creator;

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

    public static final ResourceLocation OPEN_GUI = new ResourceLocation(SDMShopRework.MODID, "open_gui");
    public static final String SDMSHOP_CATEGORY = "key.category.sdmshopr";
    public static final String KEY_NAME = "key.sdmshop.shopr";

    public static KeyMapping KEY_SHOP = new KeyMapping(KEY_NAME, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, SDMSHOP_CATEGORY);

    public void init() {
        CustomClickEvent.EVENT.register(this::customClick);
        MinecraftForge.EVENT_BUS.addListener(this::keyInput);
    }

    public EventResult customClick(CustomClickEvent event) {
        if (ShopBase.CLIENT != null && event.id().equals(OPEN_GUI)) {
            new LegacyShopScreen().openGui();
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }
    public void keyInput(InputEvent.Key event) {
        if (KEY_SHOP.consumeClick() && ShopBase.CLIENT != null) {
            new LegacyShopScreen().openGui();
        }
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event){
        event.register(KEY_SHOP);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        creator = new ClientShopData(SDMShopPaths.getFileClient());
        SNBTCompoundTag d1 = SNBT.read(SDMShopPaths.getFileClient());
        if(d1 != null) {
            creator.deserializeNBT(d1);
        }
    }

    public static class ClientShopData implements INBTSerializable<CompoundTag> {
        public Path path;
        public List<String> favoriteCreator = new ArrayList<>();


        public ClientShopData(Path path){
            this.path = path;
        }


        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            ListTag tags = new ListTag();
            for (String s : favoriteCreator) {
                tags.add(StringTag.valueOf(s));
            }
            nbt.put("favoriteCreator", tags);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            favoriteCreator.clear();
            ListTag tags = (ListTag) nbt.get("favoriteCreator");
            for (Tag tag : tags) {
                favoriteCreator.add(((StringTag)tag).getAsString());
            }
        }
    }

}
