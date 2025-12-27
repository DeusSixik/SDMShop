package net.sixik.sdmshop.network.async;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.cache.ShopClientCache;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.Objects;
import java.util.UUID;

public class AsyncClientTasks {

    public static void init() {
        AsyncBridge.registerHandler(AsyncServerTasks.OPEN_SHOP, buf -> {
            final ResourceLocation shopID = buf.readResourceLocation();
            final UUID shopUId = buf.readUUID();
            final CompoundTag shopData = buf.readAnySizeNbt();

            if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUId)) {
                SDMShopClient.CurrentShop = new BaseShop(shopID, shopUId);
            }

            SDMShopClient.CurrentShop.deserialize(shopData);

            new ModernShopScreen().openGui();

            return null; // No response to the server is required.
        });

        AsyncBridge.registerHandler(AsyncServerTasks.GET_SHOP_CACHE, buf -> {
            final UUID shopId = buf.readUUID();
            final String version = buf.readUtf();

            ShopClientCache.loadCache();

            final String cacheVersion = ShopClientCache.getCacheVersion(shopId);
            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            response.writeBoolean(Objects.equals(cacheVersion, version));
            return response;
        });

        AsyncBridge.registerHandler(AsyncServerTasks.SYNC_SHOP, buf -> {
            final ResourceLocation shopId = buf.readResourceLocation();
            final UUID shopUId = buf.readUUID();
            final CompoundTag shopData = buf.readAnySizeNbt();

            final BaseShop baseShop = new BaseShop(shopData);
            ShopClientCache.saveCache(baseShop);
            return null;
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
