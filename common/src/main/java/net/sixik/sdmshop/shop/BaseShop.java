package net.sixik.sdmshop.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmshop.utils.HashUtils;
import net.sixik.sdmshop.utils.ShopSerializerUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BaseShop implements DataSerializerCompoundTag, ConfigSupport, ShopBase {

    public static final Codec<BaseShop> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(CompoundTag.CODEC.fieldOf("shop").forGetter(BaseShop::serialize))
        .apply(instance, BaseShop::new));

    public static final String ENTRIES_KEY = "shop_entries";
    public static final String TABS_KEY = "shop_tabs";
    public static final String PARAMS_KEY = "shop_params";

    protected final ResourceLocation registryId;
    protected final UUID uuid;
    protected final List<ShopEntry> shopEntries = new ObjectArrayList<>();
    protected final List<ShopTab> shopTabs = new ObjectArrayList<>();

    protected final ShopParams shopParams = new ShopParams();

    private String version = ShopBase.NULL_HASH;
    private boolean dirty = false;
    private @Nullable Tag cachedNBT = null;
    private final List<ShopBase.ShopChangeListener> shopChangeListeners = new ObjectArrayList<>();
    private final List<ShopBase.EntryAddListener> entryAddListeners = new ObjectArrayList<>();
    private final List<ShopBase.EntryRemoveListener> entryRemoveListeners = new ObjectArrayList<>();
    private final List<ShopBase.EntryChangeListener> entryChangeListeners = new ObjectArrayList<>();
    private final List<ShopBase.TabAddListener> tabAddListeners = new ObjectArrayList<>();
    private final List<ShopBase.TabRemoveListener> tabRemoveListeners = new ObjectArrayList<>();
    private final List<ShopBase.TabChangeListener> tabChangeListeners = new ObjectArrayList<>();

    public BaseShop(CompoundTag data) {
        this.registryId = ResourceLocation.tryParse(data.getString("id"));
        this.uuid = data.getUUID("uuid");
        deserialize(data);
    }

    public BaseShop(ResourceLocation registryId, UUID uuid) {
        this.registryId = registryId;
        this.uuid = uuid;
    }

    @Override
    public void onChangeMethod() {
        cachedNBT = serialize();
        version = calculateVersion();
    }

    @Override
    public ResourceLocation getRegistryId() {
        return registryId;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public List<ShopEntry> getEntries() {
        return shopEntries;
    }

    @Override
    public List<ShopTab> getTabs() {
        return shopTabs;
    }

    @Override
    public <T extends ShopBase> Codec<T> codec() {
        return (Codec<T>) CODEC;
    }

    @Override
    public ShopParams getParams() {
        return shopParams;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean value) {
        this.dirty = value;
    }

    @Override
    public @Nullable Tag getCachedNbt() {
        return cachedNBT;
    }

    @Override
    public void setCachedNbt(Tag nbt) {
        this.cachedNBT = nbt;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public List<ShopChangeListener> getShopChangeListeners() {
        return shopChangeListeners;
    }

    @Override
    public List<EntryAddListener> getEntryAddListeners() {
        return entryAddListeners;
    }

    @Override
    public List<EntryRemoveListener> getEntryRemoveListeners() {
        return entryRemoveListeners;
    }

    @Override
    public List<EntryChangeListener> getEntryChangeListeners() {
        return entryChangeListeners;
    }

    @Override
    public List<TabAddListener> getTabAddListeners() {
        return tabAddListeners;
    }

    @Override
    public List<TabRemoveListener> getTabRemoveListeners() {
        return tabRemoveListeners;
    }

    @Override
    public List<TabChangeListener> getTabChangeListeners() {
        return tabChangeListeners;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        shopParams.getConfig(group);
    }

    @Override
    public CompoundTag serializeOrCache() {
        if(cachedNBT == null)
            cachedNBT = serialize();

        return (CompoundTag) cachedNBT;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("id", registryId.toString());
        nbt.putUUID("uuid", uuid);

        ListTag tags = new ListTag();
        for (var entry : shopEntries) {
            tags.add(entry.serialize());
        }
        nbt.put(ENTRIES_KEY, tags);

        tags = new ListTag();
        for (var entry : shopTabs) {
            tags.add(entry.serialize());
        }
        nbt.put(TABS_KEY, tags);

        nbt.put(PARAMS_KEY, shopParams.serialize());

        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        getEntries().clear();
        getTabs().clear();

        if (tag.contains(ENTRIES_KEY)) {
            ListTag entriesList = (ListTag) tag.get(ENTRIES_KEY);

            for (Tag tag1 : entriesList) {
                try {
                    ShopEntry entry = new ShopEntry(this);
                    entry.deserialize((CompoundTag) tag1);
                    getEntries().add(entry);
                } catch (Exception e) {
                    SDMEconomy.printStackTrace("Error when read ShopEntry", e);
                }
            }
        }

        if (tag.contains(TABS_KEY)) {
            ListTag entriesList = (ListTag) tag.get(TABS_KEY);

            for (Tag tag1 : entriesList) {
                try {
                    ShopTab entry = new ShopTab(this);
                    entry.deserialize((CompoundTag) tag1);
                    getTabs().add(entry);
                } catch (Exception e) {
                    SDMEconomy.printStackTrace("Error when read ShopTab", e);
                }
            }
        }

        shopParams.deserialize(tag.getCompound(PARAMS_KEY));

        this.version = calculateVersion();
    }
}
