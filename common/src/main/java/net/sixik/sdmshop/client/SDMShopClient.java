package net.sixik.sdmshop.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftblibrary.ui.CustomClickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.cache.ShopClientCache;
import net.sixik.sdmshop.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.network.async.AsyncBridge;
import net.sixik.sdmshop.network.async.AsyncClientTasks;
import net.sixik.sdmshop.network.async.BlobTransfer;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopParams;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.*;

public class SDMShopClient {

    public static @Nullable BaseShop CurrentShop;
    public static ShopLimiter shopLimiter = new ShopLimiter();

    public static Map<Class<? extends AbstractEntryType>, List<AbstractEntryTypeFilter<? extends AbstractEntryType>>> shopFilters;

    public static final Color4I someColor = Color4I.rgb(214,154,255);

    public static ClientShopData userData;

    public static final ResourceLocation OPEN_GUI = new ResourceLocation(SDMShop.MODID, "open_gui");
    public static final String SHOP_CATEGORY = "key.category.sdmshopr";
    public static final String KEY_NAME = "key.sdmshop.shopr";

    public static KeyMapping KEY_SHOP = new KeyMapping(KEY_NAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, SHOP_CATEGORY);

    public static void init(Runnable onClient) {
        AsyncBridge.initClient();
        BlobTransfer.initClient();

        AsyncClientTasks.init();
        ClientLifecycleEvent.CLIENT_SETUP.register(SDMShopClient::onClientSetup);
        ClientTickEvent.CLIENT_PRE.register(SDMShopClient::keyInput);
        CustomClickEvent.EVENT.register(SDMShopClient::customClick);
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(SDMShopClient::onClientPlayerConnect);

        if(!ShopConfig.DISABLE_KEYBIND.get()) {
            KeyMappingRegistry.register(KEY_SHOP);
        }

        onClient.run();
    }

    public static void onClientPlayerConnect(Player player) {
        ShopClientCache.loadCache();
    }

    public static EventResult customClick(CustomClickEvent event) {
        if (event.id().equals(OPEN_GUI) && !ShopConfig.DISABLE_KEYBIND.get()) {
            openGui(SDMShopConstants.DEFAULT_SHOP);
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }
    public static void keyInput(Minecraft mc) {
        if (KEY_SHOP.consumeClick()) {
            if(!ShopConfig.DISABLE_KEYBIND.get())
                openGui(ShopConfig.DEFAULT_SHOP_ID.get());
        }
    }

    public static void openGui(String shopId) {
        AsyncClientTasks.openShop(SDMShopServer.parseLocation(shopId));
    }

    public static void openGui() {
        switch (ShopConfig.GUI_STYLE.get()) {
            case Modern -> new ModernShopScreen().openGui();
            case BlockyModern -> new MainShopScreen().openGui();
        }
    }


//    public static void openGui(ShopStyle shopStyle, boolean isOpenCommand) {
//        new SendGetMoneyC2S().sendToServer();
//
//        switch (shopStyle) {
//            case LEGACY -> {
//                new LegacyShopScreen(isOpenCommand).openGui();
//            }
//            case MODERN -> {
//                new ModernShopScreen(isOpenCommand).openGui();
//            }
//        }
//    }

    public static void onClientSetup(Minecraft minecraft)
    {
        userData = new ClientShopData(SDMShopPaths.getFileClient());
        SNBTCompoundTag d1 = SNBT.read(SDMShopPaths.getFileClient());
        if(d1 != null) {
            userData.deserialize(d1);
        }
    }


    public static class ClientShopData implements DataSerializerCompoundTag, ConfigSupport {
        public Path path;
        protected List<String> favoriteCreator = new ArrayList<>();
        protected List<UUID> favoriteEntries = new ArrayList<>();
        protected List<UUID> lastOpenedTabs = new ArrayList<>();
        protected Map<UUID, ShopParams> paramsMap = new HashMap<>();

        public ClientShopData(Path path){
            this.path = path;
        }

        public boolean showEntryWitchCantBuy(UUID uuid) {
            return paramsMap.getOrDefault(uuid, new ShopParams()).showEntryWitchCantBuy();
        }

        @Override
        public CompoundTag serialize() {
            CompoundTag nbt = new CompoundTag();
            ShopNBTUtils.putList(nbt, "favoriteCreator", favoriteCreator, StringTag::valueOf);
            ShopNBTUtils.putList(nbt, "favoriteEntries", favoriteEntries, NbtUtils::createUUID);
            ShopNBTUtils.putList(nbt, "lastOpenedTabs", lastOpenedTabs, NbtUtils::createUUID);

            ListTag listTag = new ListTag();
            for (Map.Entry<UUID, ShopParams> entry : paramsMap.entrySet()) {
                CompoundTag d1 = new CompoundTag();
                d1.putUUID("shopId", entry.getKey());
                d1.put("data", entry.getValue().serialize());
                listTag.add(d1);
            }
            nbt.put("shop_client_data", listTag);

            return nbt;
        }

        @Override
        public void deserialize(CompoundTag nbt) {
            favoriteCreator = ShopNBTUtils.getList(nbt, "favoriteCreator", Tag::getAsString);
            favoriteEntries = ShopNBTUtils.getList(nbt, "favoriteEntries", NbtUtils::loadUUID);
            lastOpenedTabs = ShopNBTUtils.getList(nbt, "lastOpenedTabs", NbtUtils::loadUUID);

            if(nbt.contains("shop_client_data")) {
                paramsMap.clear();
                ListTag listTag = (ListTag) nbt.get("shop_client_data");
                for (Tag tag : listTag) {
                    CompoundTag d1 = (CompoundTag) tag;
                    UUID shopId = d1.getUUID("shopId");

                    ShopParams params = new ShopParams(d1.getCompound("data"));
                    paramsMap.put(shopId, params);
                }
            }
        }

        public void save() {
            SNBT.write(SDMShopPaths.getFileClient(), serialize());
        }

        public List<String> getCreator() {
            return favoriteCreator;
        }

        public List<UUID> getEntries() {
            return favoriteEntries;
        }

        @Override
        public void getConfig(ConfigGroup group) {
            if(CurrentShop == null) return;
            ShopParams value = paramsMap.computeIfAbsent(CurrentShop.getId(), s -> new ShopParams());
            value.getClientConfig(group);
        }
    }
}
