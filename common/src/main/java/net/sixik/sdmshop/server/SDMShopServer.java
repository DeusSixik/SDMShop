package net.sixik.sdmshop.server;

import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.old_api.DataSaver;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SDMShopServer implements DataSaver {

    public static final Path SHOP_FOLDER_DATA = Path.of("shops");

    private static SDMShopServer _instance;

    public static SDMShopServer Instance() {
        return _instance;
    }

    public static Optional<SDMShopServer> InstanceOptional() {
        return Optional.ofNullable(_instance);
    }

    protected @Nullable BaseShop defaultShop;
    protected Map<UUID, BaseShop> ShopMap = new HashMap<>();
    protected MinecraftServer server;

    protected ShopLimiter shopLimiter;

    public SDMShopServer(MinecraftServer server) {
        this.server = server;
        this.shopLimiter = new ShopLimiter();
        _instance = this;
        load(server);
    }

    public boolean removeShop(String shopId) {
        ResourceLocation res = ResourceLocation.tryParse(shopId);
        return removeShop(res == null ? new ResourceLocation(SDMShop.MODID, shopId) : res);
    }

    public boolean removeShop(ResourceLocation shopId) {
        Optional<BaseShop> opt = Optional.empty();
        for (Map.Entry<UUID, BaseShop> entry : ShopMap.entrySet()) {
            if(Objects.equals(entry.getValue().getRegistryId().toString(), shopId.toString())) {
                opt = Optional.of(entry.getValue());
                break;
            }
        }

        if(opt.isEmpty()) return false;
        BaseShop shop = opt.get();
        ShopMap.remove(shop.getUuid());
        removeFile(shop);
        save(server);
        return true;
    }

    public static ResourceLocation fromString(String id) {
        ResourceLocation res = ResourceLocation.tryParse(id);

        if(res != null && Objects.equals(res.getNamespace(), "minecraft"))
            res = new ResourceLocation(SDMShop.MODID, res.getPath());

        return res == null ? new ResourceLocation(SDMShop.MODID, id) : res;
    }

    public boolean exists(String id) {
        ResourceLocation res = ResourceLocation.tryParse(id);
        ResourceLocation res1 = res == null ? new ResourceLocation(SDMShop.MODID, id) : res;
        return ShopMap.values().stream().anyMatch(s -> Objects.equals(s.getRegistryId().toString(), res1.toString()));
    }

    public BaseShop createShop(String id) {
        return createShop(new ResourceLocation(SDMShop.MODID, id));
    }

    public BaseShop createShop(ResourceLocation shopId) {
        BaseShop shop = new BaseShop(shopId, UUID.randomUUID());
        ShopMap.put(shop.getUuid(), shop);

        save(server);
        return shop;
    }

    public Optional<BaseShop> getShop(ResourceLocation shopId) {
       return ShopMap.values().stream().filter(s -> Objects.equals(s.getRegistryId(), shopId)).findFirst();
    }

    public Optional<BaseShop> getShop(UUID uuid) {
        return Optional.ofNullable(ShopMap.get(uuid));
    }

    public Optional<BaseShop> getFirstShop() {
        return Optional.ofNullable(ShopMap.values().stream().toList().get(0));
    }

    public List<String> getAllShopIDs() {
        return ShopMap.values().stream().map(BaseShop::getRegistryId).map(ResourceLocation::toString).toList();
    }

    public List<UUID> getAllShopUUIDs() {
        return ShopMap.values().stream().map(BaseShop::getUuid).toList();
    }

    protected void createDefault() {
        defaultShop = createShop(SDMShopConstants.DEFAULT_SHOP);
        if(Platform.isDevelopmentEnvironment()) {
            UUID ownerTab = UUID.randomUUID();

            defaultShop.addShopTab(new ShopTab(defaultShop, ownerTab));

            for (int i = 0; i < 100; i++) {
                defaultShop.addShopEntry(new ShopEntry(defaultShop, ownerTab));
            }
        }
    }

    public ShopLimiter getShopLimiter() {
        return shopLimiter;
    }

    @Override
    public void save(MinecraftServer server) {
        Path data = SDMShopPaths.getModFolder().resolve(SHOP_FOLDER_DATA);
        if(!data.toFile().exists())
            if(!data.toFile().mkdirs()) {
                SDMShop.LOGGER.error("Error when create shop folder [{}]", data);
                return;
            }

        for (Map.Entry<UUID, BaseShop> uuidBaseShopEntry : ShopMap.entrySet()) {
            try {
                File file = new File(data.toFile(), uuidBaseShopEntry.getKey().toString() + ".data");
                NbtIo.write(uuidBaseShopEntry.getValue().serialize(), file);
            } catch (Exception e) {
                SDMEconomy.printStackTrace("Error writing file " + uuidBaseShopEntry.getKey() + ".data", e);
            }
        }

        data = server.getWorldPath(LevelResource.ROOT).resolve("SDMShop").resolve(ShopLimiter.LIMITER_FOLDER);

        if(!data.toFile().exists())
            if(!data.toFile().mkdirs()) {
                SDMShop.LOGGER.error("Error when create limiter folder [{}]", data);
                return;
            }

        try {
            File file = new File(data.toFile(), ShopLimiter.FILE_NAME);
            NbtIo.write(shopLimiter.serialize(), file);
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error writing file " + ShopLimiter.FILE_NAME, e);
        }
    }

    public void saveShop(MinecraftServer server, UUID shopId) {
        Path data = SDMShopPaths.getModFolder().resolve(SHOP_FOLDER_DATA);

        getShop(shopId).ifPresent(shop -> {
            try {
                File file = new File(data.toFile(), shop.getUuid().toString() + ".data");
                NbtIo.write(shop.serialize(), file);
            } catch (IOException e) {
                SDMEconomy.printStackTrace("Error writing file " + ShopLimiter.FILE_NAME, e);
            }
        });
    }

    @Override
    public void load(MinecraftServer server) {
        Path data = SDMShopPaths.getModFolder().resolve(SHOP_FOLDER_DATA);
        if(!data.toFile().exists()) {
            createDefault();
        } else {
            for (File file : data.toFile().listFiles()) {
                try {
                    CompoundTag nbt = NbtIo.read(file);
                    if (nbt != null) {
                        ResourceLocation registryId = new ResourceLocation(nbt.getString("id"));
                        UUID uuid = nbt.getUUID("uuid");
                        BaseShop shopBase = new BaseShop(registryId, uuid);
                        shopBase.deserialize(nbt);
                        ShopMap.put(shopBase.getUuid(), shopBase);
                    }
                } catch (Exception e) {
                    SDMEconomy.printStackTrace("Error reading file " + String.valueOf(file), e);
                }
            }

            if (ShopMap.size() == 1) {
                defaultShop = ShopMap.values().stream().toList().get(0);
            }
        }

        data = server.getWorldPath(LevelResource.ROOT).resolve("SDMShop").resolve(ShopLimiter.LIMITER_FOLDER);

        if(data.toFile().exists()) {

            try {
                File file = new File(data.toFile(), ShopLimiter.FILE_NAME);
                CompoundTag nbt = NbtIo.read(file);
                if (nbt != null) {
                    shopLimiter.deserialize(nbt);
                }
            } catch (Exception e) {
                SDMEconomy.printStackTrace("Error writing file " + ShopLimiter.FILE_NAME, e);
            }
        }
    }

    protected void removeFile(BaseShop base) {
        Path data = SDMShopPaths.getModFolder().resolve(SHOP_FOLDER_DATA).resolve(base.getUuid().toString() + ".data");
        if(!data.toFile().exists())
            return;

        data.toFile().delete();

    }
}
