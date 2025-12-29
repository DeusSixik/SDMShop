package net.sixik.sdmshop.network.async;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.cache.ShopClientCache;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.Objects;
import java.util.UUID;

public class AsyncClientTasks {

    public static void init() {
        AsyncBridge.registerHandler(AsyncServerTasks.SYNC_SHOP_NEW, buf -> {
            final UUID shopUId = buf.readUUID();
            final ResourceLocation shopId = buf.readResourceLocation();
            final CompoundTag shopData = buf.readAnySizeNbt();

            boolean success = false;
            if (shopData != null) {
                SDMShopClient.CurrentShop = new BaseShop(shopData);
                success = true;
            }
            else
                SDMShop.LOGGER.error("[Requests {}] Can't sync shop because 'shopData' is null", AsyncServerTasks.SYNC_SHOP_NEW);


            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(success);
            return response;
        });

        AsyncBridge.registerHandler(AsyncServerTasks.SYNC_SHOP_CACHE_GET, buf -> {
            final UUID shopUId = buf.readUUID();
            final ResourceLocation shopID = buf.readResourceLocation();
            final String shopVersion = buf.readUtf();

            ShopClientCache.loadCache();

            boolean haveShop = false;
            final ShopBase shopCache = ShopClientCache.getCache(shopID);
            if (shopCache != null) {
                final String cachedVersion = shopCache.getVersion();
                if (!cachedVersion.equals(BaseShop.NULL_HASH) && cachedVersion.equals(shopVersion)) {
                    haveShop = true;
                }
            }

            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(haveShop);
            return response;
        });

        AsyncBridge.registerHandler(AsyncServerTasks.SYNC_SHOP_CACHE_GET_OPEN, buf -> {
            final UUID shopUId = buf.readUUID();
            final ResourceLocation shopID = buf.readResourceLocation();
            final String shopVersion = buf.readUtf();

            boolean haveShop = false;

            ShopClientCache.loadCache();

            final ShopBase shopCache = ShopClientCache.getCache(shopID);
            if (shopCache != null) {
                final String cachedVersion = shopCache.getVersion();
                if (!cachedVersion.equals(BaseShop.NULL_HASH) && cachedVersion.equals(shopVersion)) {
                    SDMShopClient.CurrentShop = (BaseShop) shopCache;
                    new ModernShopScreen().openGui();
                    haveShop = true;
                }
            }

            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(haveShop);
            return response;
        });

        AsyncBridge.registerHandler(AsyncServerTasks.SYNC_SHOP_CACHE_SET, buf -> {
            final UUID shopUId = buf.readUUID();
            final ResourceLocation shopID = buf.readResourceLocation();
            final CompoundTag shopData = buf.readAnySizeNbt();

            boolean success = false;
            if (shopData != null) {
                ShopClientCache.saveCache(new BaseShop(shopData));
                success = true;
            }
            else
                SDMShop.LOGGER.error("[Requests {}] Can't sync shop because 'shopData' is null", AsyncServerTasks.SYNC_SHOP_CACHE_SET);

            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(success);
            return response;
        });

        AsyncBridge.registerHandler(AsyncServerTasks.SYNC_SHOP_CACHE_SET_OPEN, buf -> {
            final UUID shopUId = buf.readUUID();
            final ResourceLocation shopID = buf.readResourceLocation();
            final CompoundTag shopData = buf.readAnySizeNbt();

            boolean success = false;
            if (shopData != null) {
                final BaseShop shop = new BaseShop(shopData);
                SDMShopClient.CurrentShop = shop;
                ShopClientCache.saveCache(shop);
                new ModernShopScreen().openGui();
                success = true;
            }
            else
                SDMShop.LOGGER.error("[Requests {}] Can't sync and open shop because 'shopData' is null", AsyncServerTasks.SYNC_SHOP_CACHE_SET);

            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(success);
            return response;
        });

        AsyncBridge.registerHandler(AsyncServerTasks.OPEN_SHOP_NEW, buf -> {
            final UUID shopUId = buf.readUUID();
            final ResourceLocation shopID = buf.readResourceLocation();
            final CompoundTag shopData = buf.readAnySizeNbt();

            boolean success = false;

            if (shopData != null) {
                if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUId)) {
                    SDMShopClient.CurrentShop = new BaseShop(shopID, shopUId);
                }

                SDMShopClient.CurrentShop.deserialize(shopData);

                new ModernShopScreen().openGui();
                success = true;
            } else
                SDMShop.LOGGER.error("[Requests {}] Can't open shop because 'shopData' is null", AsyncServerTasks.OPEN_SHOP_NEW);

            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(success);
            return response;
        });
    }



    public static void openShop(ResourceLocation shopId) {
        AsyncBridge.askServer(AsyncServerTasks.GET_OPEN_SHOP, buf -> {
            buf.writeResourceLocation(shopId);
            return buf;
        }).thenAcceptAsync(response -> {
            final boolean found = response.readBoolean();
            if (!found) {
                Minecraft.getInstance().player.displayClientMessage(Component.literal("Shop not found!"), false);
                return;
            }
            final UUID shopUID = response.readUUID();
            final CompoundTag shopData = response.readAnySizeNbt();

            if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUID)) {
                SDMShopClient.CurrentShop = new BaseShop(shopId, shopUID);
            }

            SDMShopClient.CurrentShop.deserialize(shopData);

            new ModernShopScreen().openGui();
        }, Minecraft.getInstance()).exceptionally(ex -> {
            SDMShop.LOGGER.error("Failed to load shop", ex);
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Error loading shop!"), false);
            return null;
        });
    }
}
