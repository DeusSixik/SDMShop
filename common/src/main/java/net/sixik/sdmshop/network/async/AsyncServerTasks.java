package net.sixik.sdmshop.network.async;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;

import java.util.Optional;

public class AsyncServerTasks {

    public static final String SYNC_SHOP_NEW = "sync_shop_new";
    public static final String SYNC_SHOP_CACHE_GET = "sync_shop_cache_get";
    public static final String SYNC_SHOP_CACHE_GET_OPEN = "sync_shop_cache_get_open";
    public static final String SYNC_SHOP_CACHE_SET = "sync_shop_cache_set";
    public static final String SYNC_SHOP_CACHE_SET_OPEN = "sync_shop_cache_set_open";

    public static final String OPEN_SHOP_NEW = "open_shop_new";

    public static final String GET_OPEN_SHOP = "get_open_shop";

    public static void init() {
        AsyncBridge.registerHandler(GET_OPEN_SHOP, buf -> {
            final ResourceLocation shopId = buf.readResourceLocation();
            final Optional<BaseShop> optionalShop = SDMShopServer.Instance().getShop(shopId);
            if (optionalShop.isEmpty()) {
                SDMShop.LOGGER.error("Can't find shop with id [{}]", shopId);
                return null;
            }
            final BaseShop shop = optionalShop.get();
            final CompoundTag fullShopData = shop.serialize();

            final FriendlyByteBuf hugeData = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
            hugeData.writeBoolean(true);
            hugeData.writeUUID(shop.getId());
            hugeData.writeNbt(fullShopData);
            return hugeData;
        });
    }

    public static void openShopOrCache(
            final ServerPlayer player,
            final ResourceLocation shopId
    ) {
        final Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
        if (shopOpt.isEmpty()) return;
        openShopOrCache(player, shopOpt.get());
    }

    public static void openShopOrCache(
            final ServerPlayer player,
            final BaseShop shop
    ) {
        if(ShopConfig.USE_CACHED_SHOP_DATA.get())
            openShopCacheNew(player, shop);
        else openShopNew(player, shop);
    }

    public static void openShopNew(
            final ServerPlayer player,
            final ResourceLocation shopId
    ) {
        final Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
        if (shopOpt.isEmpty()) return;
        openShopNew(player, shopOpt.get());
    }

    public static void openShopNew(
            final ServerPlayer player,
            final BaseShop shop
    ) {
        AsyncBridge.askPlayer(player, OPEN_SHOP_NEW, buf -> {
            buf.writeUUID(shop.getId());
            buf.writeResourceLocation(shop.getRegistryId());
            buf.writeNbt(shop.serializeOrCache());
            return buf;
        });
    }

    public static void openShopCacheNew(
            final ServerPlayer player,
            final ResourceLocation shopId
    ) {
        final Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
        if (shopOpt.isEmpty()) return;
        openShopCacheNew(player, shopOpt.get());
    }

    public static void openShopCacheNew(
            final ServerPlayer player,
            final BaseShop shop
    ) {
        AsyncBridge.askPlayer(player, SYNC_SHOP_CACHE_GET_OPEN, buf -> {
            buf.writeUUID(shop.getId());
            buf.writeResourceLocation(shop.getRegistryId());
            buf.writeUtf(shop.getVersion());
            return buf;
        }).thenAcceptAsync(response -> {
            final boolean haveShop = response.readBoolean();
            if(haveShop) return;

            AsyncBridge.askPlayer(player, SYNC_SHOP_CACHE_SET_OPEN, buf -> {
                buf.writeUUID(shop.getId());
                buf.writeResourceLocation(shop.getRegistryId());
                buf.writeNbt(shop.serializeOrCache());
                return buf;
            });
        }, player.getServer());
    }

    public static void syncShop(
            final ServerPlayer player,
            final ResourceLocation shopId
    ) {
        final Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
        if (shopOpt.isEmpty()) return;
        syncShop(player, shopOpt.get());
    }

    public static void syncShop(
            final ServerPlayer player,
            final BaseShop shop
    ) {
        AsyncBridge.askPlayer(player, SYNC_SHOP_NEW, buf -> {
            buf.writeUUID(shop.getId());
            buf.writeResourceLocation(shop.getRegistryId());
            buf.writeNbt(shop.serializeOrCache());
            return buf;
        });
    }

    public static void syncShopCache(
            final ServerPlayer player,
            final ResourceLocation shopId
    ) {
        final Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
        if (shopOpt.isEmpty()) return;
        syncShopCache(player, shopOpt.get());
    }

    public static void syncShopCache(
            final ServerPlayer player,
            final BaseShop shop
    ) {
        AsyncBridge.askPlayer(player, SYNC_SHOP_CACHE_GET, buf -> {
            buf.writeUUID(shop.getId());
            buf.writeResourceLocation(shop.getRegistryId());
            buf.writeUtf(shop.getVersion());
            return buf;
        }).thenAcceptAsync(response -> {
            final boolean haveShop = response.readBoolean();
            if(haveShop) return;

            AsyncBridge.askPlayer(player, SYNC_SHOP_CACHE_SET, buf -> {
                buf.writeUUID(shop.getId());
                buf.writeResourceLocation(shop.getRegistryId());
                buf.writeNbt(shop.serializeOrCache());
                return buf;
            });
        }, player.getServer());
    }
}
