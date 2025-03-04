package net.sixik.sdmshoprework;


import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftblibrary.ui.CustomClickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.client.screen.legacy.LegacyShopScreen;
import net.sixik.sdmshoprework.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.common.config.ConfigFile;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.theme.SDMThemes;
import net.sixik.sdmshoprework.common.theme.ShopStyle;
import net.sixik.sdmshoprework.common.theme.ShopTheme;
import net.sixik.sdmshoprework.network.server.SendGetMoneyC2S;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    public static KeyMapping KEY_SHOP = new KeyMapping(KEY_NAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, SDMSHOP_CATEGORY);

    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(SDMShopClient::onClientSetup);
        ClientTickEvent.CLIENT_PRE.register(SDMShopClient::keyInput);
        CustomClickEvent.EVENT.register(SDMShopClient::customClick);
        if(!ConfigFile.CLIENT.disableKeyBind) {
            KeyMappingRegistry.register(KEY_SHOP);
        }

    }

    public static void openGui(boolean isOpenCommand) {
        openGui(Config.STYLE.get(), isOpenCommand);
    }

    public static void openGui(ShopStyle shopStyle, boolean isOpenCommand) {
        new SendGetMoneyC2S().sendToServer();

        switch (shopStyle) {
            case LEGACY -> {
                new LegacyShopScreen(isOpenCommand).openGui();
            }
            case MODERN -> {
                new ModernShopScreen(isOpenCommand).openGui();
            }
        }
    }

    public static EventResult customClick(CustomClickEvent event) {
        if (ShopBase.CLIENT != null && event.id().equals(OPEN_GUI) && !ConfigFile.CLIENT.disableKeyBind) {
            openGui(Config.STYLE.get(), false);
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }
    public static void keyInput(Minecraft mc) {
        if (KEY_SHOP.consumeClick() && ShopBase.CLIENT != null) {
            if(!ConfigFile.CLIENT.disableKeyBind) {
                SDMShopClient.openGui(Config.STYLE.get(), false);
            }
        }
    }


    public static void onClientSetup(Minecraft minecraft)
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
