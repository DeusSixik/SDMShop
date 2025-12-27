package net.sixik.sdmshop.cache;

import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.BaseShop;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ShopClientCache {

    public static final String CACHE_FOLDER = "cache";
    public static final String EXTENSION = ".cache";

    private static final Map<UUID, ShopBase> SHOP_CACHE = new HashMap<>();

    private static String serverIp = "";

    @Nullable
    public static ShopBase getCache(final UUID shopId) {
        return SHOP_CACHE.get(shopId);
    }

    @Nullable
    public static ShopBase getCache(final ResourceLocation shopId) {
        for (ShopBase value : SHOP_CACHE.values()) {
            if(Objects.equals(value.getRegistryId(), shopId)) return value;
        }
        return null;
    }

    public static String getCacheVersion(final UUID shopId) {
        final ShopBase shop = SHOP_CACHE.get(shopId);
        if(shop == null) return ShopBase.NULL_HASH;
        return shop.getVersion();
    }

    public static String getCacheVersion(final ResourceLocation shopId) {
        for (ShopBase value : SHOP_CACHE.values()) {
            if(Objects.equals(value.getRegistryId(), shopId)) return value.getVersion();
        }
        return ShopBase.NULL_HASH;
    }

    public static boolean loadCache() {
        final ServerData serverData = Minecraft.getInstance().getCurrentServer();
        if(serverData == null) return false;

        if(Objects.equals(serverData.ip, serverIp)) return false;
        return loadCacheInternal(serverData);
    }

    protected static boolean loadCacheInternal(ServerData serverData) {
        SHOP_CACHE.clear();
        if (serverData == null) return false;

        final Path shopFolder = SDMShopPaths.getModFolder();
        final Path cacheFolder = shopFolder.resolve(CACHE_FOLDER);
        final Path serverCacheFolder = getCacheDirForCurrentServer(cacheFolder, serverData);

        try {
            Files.createDirectories(serverCacheFolder);

            final File[] files = serverCacheFolder.toFile().listFiles();
            if (files == null || files.length == 0) return true;

            int loaded = 0;

            for (File file : files) {
                if (!file.isFile()) continue;

                final String name = file.getName();
                if (!name.endsWith(EXTENSION)) continue;

                final String uuidStr = name.substring(0, name.length() - EXTENSION.length());

                final UUID shopId;
                try {
                    shopId = UUID.fromString(uuidStr);
                } catch (IllegalArgumentException badName) {
                    SDMShop.LOGGER.warn("Skip cache file with invalid UUID name: {}", name);
                    continue;
                }

                try {
                    final CompoundTag tag = readNbtFile(file.toPath());
                    if (tag == null) continue;

                    final ShopBase shop = decodeShop(tag);
                    if (shop == null) {
                        SDMShop.LOGGER.warn("Failed to decode shop cache for {} (file {})", shopId, name);
                        continue;
                    }

                    SHOP_CACHE.put(shopId, shop);

                    loaded++;
                } catch (Exception e) {
                    SDMShop.LOGGER.error("Failed to load cache file {}", file.getAbsolutePath(), e);
                }
            }

            SDMShop.LOGGER.info("Loaded {} shop caches from {}", loaded, serverCacheFolder);
            return true;

        } catch (Exception e) {
            SDMShop.LOGGER.error("Failed to load cache folder {}", serverCacheFolder, e);
            return false;
        }
    }

    public static void saveCache(final BaseShop shop) {
        if (shop == null) return;

        SHOP_CACHE.put(shop.getId(), shop);

        final ServerData serverData = Minecraft.getInstance().getCurrentServer();
        if (serverData == null) return;

        final Path shopFolder = SDMShopPaths.getModFolder();
        final Path cacheFolder = shopFolder.resolve(CACHE_FOLDER);
        final Path serverCacheFolder = getCacheDirForCurrentServer(cacheFolder, serverData);

        try {
            Files.createDirectories(serverCacheFolder);

            final UUID shopId = shop.getId();
            final Path file = serverCacheFolder.resolve(shopId.toString() + EXTENSION);

            final CompoundTag tag = shop.serialize();

            final Path tmp = serverCacheFolder.resolve(shopId.toString() + EXTENSION + ".tmp");

            try (var out = Files.newOutputStream(tmp,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                net.minecraft.nbt.NbtIo.writeCompressed(tag, out);
            }

            try {
                Files.move(tmp, file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                /*
                    Some FS do not support atomic move.
                 */
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            SDMShop.LOGGER.error("Failed to save shop cache (shop={})", shop.getId(), e);
        }
    }

    private static String normalizeServerAddress(String raw) {
        if (raw == null || raw.isBlank()) return "unknown";

        final String s = raw.trim().toLowerCase(java.util.Locale.ROOT);

        /*
            If the address is already in the form “host:port”
         */
        final int colon = s.lastIndexOf(':');
        if (colon > 0 && colon < s.length() - 1) {
            final String host = s.substring(0, colon);
            final String portStr = s.substring(colon + 1);
            try {
                final int port = Integer.parseInt(portStr);
                return host + ":" + port;
            } catch (NumberFormatException ignored) {
                /*
                    it could be IPv6 without brackets or garbage — we'll fall back to the default below
                 */
            }
        }

        /*
            If the port is not specified
         */
        return s + ":25565";
    }

    private static String sha256Hex(String input) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            final StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            /*
                fallback (very rarely)
             */
            return Integer.toHexString(input.hashCode());
        }
    }

    private static Path getCacheDirForCurrentServer(final Path baseCacheDir, final ServerData serverData) {
        final String key = (serverData == null) ? "singleplayer" : normalizeServerAddress(serverData.ip);
        final String hash = sha256Hex("sdmshop|" + key);
        final String folderName = hash.substring(0, 24);
        return baseCacheDir.resolve(folderName);
    }

    private static CompoundTag readNbtFile(Path path) throws Exception {
        return net.minecraft.nbt.NbtIo.read(path.toFile());
    }

    private static ShopBase decodeShop(CompoundTag tag) {
        return BaseShop.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(SDMShop.LOGGER::error)
                .orElse(null);
    }
}
