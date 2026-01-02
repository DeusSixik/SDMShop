package net.sixik.sdmshop.server;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.old_api.DataSaver;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.ShopSerializerUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SDMShopServer implements DataSaver {

    public static final Path SHOP_FOLDER_DATA = Path.of("shops");
    private static final String LIMITER_FILE_NAME = "limiter.data";

    private static SDMShopServer _instance;

    protected final Map<UUID, BaseShop> shopsByUUID = new Object2ObjectOpenHashMap<>();
    protected final Map<ResourceLocation, BaseShop> shopsByRes = new Object2ObjectOpenHashMap<>();

    protected MinecraftServer server;
    protected ShopLimiter shopLimiter;
    protected @Nullable BaseShop defaultShop;

    public static SDMShopServer Instance() {
        return _instance;
    }

    public static Optional<SDMShopServer> InstanceOptional() {
        return Optional.ofNullable(_instance);
    }

    public SDMShopServer(MinecraftServer server) {
        this.server = server;
        this.shopLimiter = new ShopLimiter();
        _instance = this;
        load(server);
    }

    public boolean exists(String id) {
        return shopsByRes.containsKey(parseLocation(id));
    }

    public Optional<BaseShop> getShop(ResourceLocation shopId) {
        return Optional.ofNullable(shopsByRes.get(shopId));
    }

    public Optional<BaseShop> getShop(UUID uuid) {
        return Optional.ofNullable(shopsByUUID.get(uuid));
    }

    public BaseShop createShop(String id) {
        return createShop(new ResourceLocation(SDMShop.MODID, id));
    }

    public BaseShop createShop(ResourceLocation shopId) {
        if (shopsByRes.containsKey(shopId)) {
            return shopsByRes.get(shopId);
        }

        BaseShop shop = new BaseShop(shopId, UUID.randomUUID());
        registerInternal(shop);

        saveShopToFile(shop);

        return shop;
    }

    public boolean removeShop(String shopId) {
        return removeShop(parseLocation(shopId));
    }

    public boolean removeShop(ResourceLocation shopId) {
        BaseShop shop = shopsByRes.get(shopId);
        if (shop == null) return false;

        shopsByUUID.remove(shop.getId());
        shopsByRes.remove(shop.getRegistryId());

        deleteShopFile(shop);
        return true;
    }

    public Optional<BaseShop> getFirstShop() {
        Iterator<BaseShop> it = shopsByUUID.values().iterator();
        return it.hasNext() ? Optional.of(it.next()) : Optional.empty();
    }

    public List<String> getAllShopIDs() {
        return shopsByRes.keySet().stream().map(ResourceLocation::toString).toList();
    }

    public List<UUID> getAllShopUUIDs() {
        return new ArrayList<>(shopsByUUID.keySet());
    }

    public ShopLimiter getShopLimiter() {
        return shopLimiter;
    }

    private void registerInternal(BaseShop shop) {
        shopsByUUID.put(shop.getId(), shop);
        shopsByRes.put(shop.getRegistryId(), shop);
    }

    public static ResourceLocation parseLocation(String id) {
        ResourceLocation res = ResourceLocation.tryParse(id);
        if (res == null) return new ResourceLocation(SDMShop.MODID, id);
        return "minecraft".equals(res.getNamespace()) ? new ResourceLocation(SDMShop.MODID, res.getPath()) : res;
    }

    private void createDefault() {
        defaultShop = createShop(SDMShopConstants.DEFAULT_SHOP);
        if (Platform.isDevelopmentEnvironment()) {
            UUID ownerTab = UUID.randomUUID();
            defaultShop.addTab(new ShopTab(defaultShop, ownerTab));
            for (int i = 0; i < 100; i++) {
                defaultShop.addEntry(new ShopEntry(defaultShop, ownerTab));
            }
            saveShopToFile(defaultShop);
        }
    }

    @Override
    public void save(MinecraftServer server) {
        saveAllShops();
        saveLimiter(server);
    }

    @Override
    public void load(MinecraftServer server) {
        loadAllShops();
        loadLimiter(server);
    }

    public void saveAllShops() {
        Path root = getShopsDir();
        ensureDir(root);
        for (BaseShop shop : shopsByUUID.values()) {
            saveShopToFile(shop, root);
        }
    }

    public void saveShop(MinecraftServer server, UUID shopId) {
        BaseShop shop = shopsByUUID.get(shopId);
        if (shop != null) saveShopToFile(shop);
    }

    public void saveShopToFile(BaseShop shop) {
        saveShopToFile(shop, getShopsDir());
    }

    public void saveShopToFile(BaseShop shop, Path dir) {
        try {
            File file = new File(dir.toFile(), shop.getId().toString() + ".data");
            NbtIo.write(shop.serialize(), file);
        } catch (IOException e) {
            SDMShop.LOGGER.error("Error writing shop " + shop.getId(), e);
        }
    }

    public void loadAllShops() {
        shopsByUUID.clear();
        shopsByRes.clear();

        Path dir = getShopsDir();
        if (!dir.toFile().exists()) {
            createDefault();
            return;
        }

        File[] files = dir.toFile().listFiles((d, name) -> name.endsWith(".data"));
        if (files == null) return;

        for (File file : files) {
            try {
                CompoundTag nbt = NbtIo.read(file);
                if (nbt != null) {
                    ResourceLocation registryId = new ResourceLocation(nbt.getString("id"));
                    UUID uuid = nbt.getUUID("uuid");
                    BaseShop shop = new BaseShop(registryId, uuid);
                    shop.deserialize(nbt);
                    ShopSerializerUtils.deleteEntriesWithNonexistentTabs(shop);
                    registerInternal(shop);
                }
            } catch (Exception e) {
                SDMShop.LOGGER.error("Error reading shop file " + file.getName(), e);
            }
        }

        if (defaultShop == null && !shopsByUUID.isEmpty()) {
            ResourceLocation defId = new ResourceLocation(SDMShop.MODID, SDMShopConstants.DEFAULT_SHOP);
            defaultShop = shopsByRes.getOrDefault(defId, getFirstShop().orElse(null));
        }
    }

    public void deleteShopFile(BaseShop shop) {
        Path path = getShopsDir().resolve(shop.getId().toString() + ".data");
        File file = path.toFile();
        if (file.exists() && !file.delete()) {
            SDMShop.LOGGER.error("Failed to delete shop file: {}", path);
        }
    }

    public Path getShopsDir() {
        Path p = SDMShopPaths.getModFolder().resolve(SHOP_FOLDER_DATA);
        ensureDir(p);
        return p;
    }

    public void saveLimiter(MinecraftServer server) {
        Path dir = getLimiterDir(server);
        try {
            File file = new File(dir.toFile(), LIMITER_FILE_NAME);
            NbtIo.write(shopLimiter.serialize(), file);
        } catch (Exception e) {
            SDMShop.LOGGER.error("Error writing limiter file", e);
        }
    }

    public void loadLimiter(MinecraftServer server) {
        Path dir = getLimiterDir(server);
        File file = new File(dir.toFile(), LIMITER_FILE_NAME);

        if (file.exists()) {
            try {
                CompoundTag nbt = NbtIo.read(file);
                if (nbt != null) shopLimiter.deserialize(nbt);
            } catch (Exception e) {
                SDMShop.LOGGER.error("Error reading limiter file", e);
            }
        }
    }

    public Path getLimiterDir(MinecraftServer server) {
        Path p = server.getWorldPath(LevelResource.ROOT).resolve("SDMShop").resolve(ShopLimiter.LIMITER_FOLDER);
        ensureDir(p);
        return p;
    }

    public void ensureDir(Path path) {
        File f = path.toFile();
        if (!f.exists() && !f.mkdirs()) {
            SDMShop.LOGGER.error("Error creating directory [{}]", path);
        }
    }
}