package net.sixik.sdmshop.shop;

import com.google.common.collect.Lists;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.shop.ShopChangeListener;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmshop.utils.ListHelper;
import net.sixik.sdmshop.utils.RemoveResult;
import net.sixik.sdmshop.utils.ShopDebugUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BaseShop implements DataSerializerCompoundTag, ConfigSupport {

    public static int SPLITTER_SIZE = 20;

    public static final String ENTRIES_KEY = "shop_entries";
    public static final String TABS_KEY = "shop_tabs";
    public static final String PARAMS_KEY = "shop_params";

    protected final ResourceLocation registryId;
    protected final UUID uuid;
    protected List<ShopEntry> shopEntries = new ObjectArrayList<>();
    protected List<ShopTab> shopTabs = new ObjectArrayList<>();

    protected final ShopParams shopParams = new ShopParams();

    protected List<ShopChangeListener> changeListeners = new ObjectArrayList<>();

    public BaseShop(ResourceLocation registryId, UUID uuid) {
        this.registryId = registryId;
        this.uuid = uuid;
    }

    public BaseShop addListener(ShopChangeListener changeListener) {
        changeListeners.add(changeListener);
        return this;
    }

    public boolean removeListener(ShopChangeListener changeListener) {
        return changeListeners.remove(changeListener);
    }

    public BaseShop onChange() {
        changeListeners.forEach(ShopChangeListener::onShopChange);
        return this;
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
        getShopEntries().clear();
        getShopTabs().clear();

        if(tag.contains(ENTRIES_KEY)) {
            ListTag entriesList = (ListTag) tag.get(ENTRIES_KEY);

            for (Tag tag1 : entriesList) {
                try {
                    ShopEntry entry = new ShopEntry(this);
                    entry.deserialize((CompoundTag) tag1);
                    getShopEntries().add(entry);
                } catch (Exception e) {
                    SDMEconomy.printStackTrace("Error when read ShopEntry", e);
                }
            }
        }

        if(tag.contains(TABS_KEY)) {
            ListTag entriesList = (ListTag) tag.get(TABS_KEY);

            for (Tag tag1 : entriesList) {
                try {
                    ShopTab entry = new ShopTab(this);
                    entry.deserialize((CompoundTag) tag1);
                    getShopTabs().add(entry);
                } catch (Exception e) {
                    SDMEconomy.printStackTrace("Error when read ShopTab", e);
                }
            }
        }

        shopParams.deserialize(tag.getCompound(PARAMS_KEY));
    }

    public ResourceLocation getRegistryId() {
        return registryId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<ShopEntry> getShopEntries() {
        return shopEntries;
    }

    public List<ShopTab> getShopTabs() {
        return shopTabs;
    }

    public Optional<ShopTab> findTabByUUID(UUID uuid) {
        return findTab(uuid);
    }

    public ShopParams getShopParams() {
        return shopParams;
    }

    public Optional<ShopTab> findTabByEntry(ShopEntry entry) {
        if(entry == null || entry.ownerTab == null) return Optional.empty();
        return findTab(entry.ownerTab);
    }

    protected Optional<ShopTab> findTab(UUID uuid) {
        return getShopTabs().stream()
                .filter(s -> s.uuid.equals(uuid))
                .findFirst();
    }

    public Optional<ShopEntry> findShopEntryByUUID(UUID uuid) {
        return getShopEntries().stream()
                .filter(s -> Objects.equals(s.uuid, uuid))
                .findFirst();
    }

    public List<ShopEntry> findShopEntriesByTab(ShopTab tab) {
        if(tab == null || tab.uuid == null) return List.of();

        return findShopEntriesByTab(tab.uuid);
    }

    public List<ShopEntry> findShopEntriesByTab(UUID uuid) {
        return getShopEntries().stream()
                .filter(s -> s.ownerTab.equals(uuid))
                .toList();
    }

    public boolean addShopTab(ShopTab shopTab) {
        getShopTabs().add(shopTab);
        return true;
    }

    public RemoveResult removeShopTab(UUID uuid) {
        List<Integer> removedIndices = new ArrayList<>();
        boolean removed = false;

        for (int i = 0; i < getShopTabs().size(); i++) {
            ShopTab tab = getShopTabs().get(i);
            if (tab.uuid.equals(uuid)) {
                getShopTabs().remove(i);
                removed = true;
                break;
            }
        }

        return new RemoveResult(removed, removedIndices);
    }

    public RemoveResult removeShopTab(ShopTab shopTab) {
        List<Integer> removedIndices = new ArrayList<>();
        boolean removed = false;

        for (int i = 0; i < getShopTabs().size(); i++) {
            ShopTab tab = getShopTabs().get(i);
            if (tab == shopTab || tab.uuid.equals(shopTab.uuid)) {
                getShopTabs().remove(i);
                removed = true;
                break;
            }
        }

        return new RemoveResult(removed, removedIndices);
    }

    public boolean addShopEntry(ShopEntry shopEntry) {
        getShopEntries().add(shopEntry);
        return true;
    }

    public RemoveResult removeShopEntry(UUID uuid) {
        List<Integer> removedIndices = new ArrayList<>();
        boolean removed = false;

        for (int i = 0; i < getShopEntries().size(); i++) {
            ShopEntry entry = getShopEntries().get(i);
            if (entry.uuid.equals(uuid)) {
                getShopEntries().remove(i);
                removed = true;
                break;
            }
        }

        return new RemoveResult(removed, removedIndices);
    }

    public RemoveResult removeShopEntry(ShopEntry shopEntry) {
        List<Integer> removedIndices = new ArrayList<>();
        boolean removed = false;

        for (int i = 0; i < getShopEntries().size(); i++) {
            ShopEntry entry = getShopEntries().get(i);
            if (entry == shopEntry || entry.uuid.equals(shopEntry.uuid)) {
                getShopEntries().remove(i);
                removed = true;
                break;
            }
        }

        return new RemoveResult(removed, removedIndices);
    }

    public RemoveResult removeShopEntry(Predicate<ShopEntry> entryPredicate, Consumer<ShopEntry> onFind) {
        List<Integer> removedIndices = new ArrayList<>();
        boolean removed = false;

        for (int i = 0; i < getShopEntries().size(); i++) {
            ShopEntry entry = getShopEntries().get(i);
            if (entryPredicate.test(entry)) {
                onFind.accept(entry);
                getShopEntries().remove(i);
                removed = true;
                break;
            }
        }

        return new RemoveResult(removed, removedIndices);
    }

    public RemoveResult removeShopEntry(Predicate<ShopEntry> entryPredicate) {
        List<Integer> removedIndices = new ArrayList<>();
        boolean removed = false;

        for (int i = 0; i < getShopEntries().size(); i++) {
            ShopEntry entry = getShopEntries().get(i);
            if (entryPredicate.test(entry)) {
                getShopEntries().remove(i);
                removed = true;
                break;
            }
        }

        return new RemoveResult(removed, removedIndices);
    }

    public boolean swapEntries(UUID entryFrom, UUID entryTo, MoveType type) {
        int i1 = -1;
        int i2 = -1;

        for (int i = 0; i < shopEntries.size(); i++) {
            if (i1 != -1 && i2 != -1) break;

            UUID id = shopEntries.get(i).uuid;
            if (Objects.equals(id, entryFrom)) i1 = i;
            else if (Objects.equals(id, entryTo)) i2 = i;
        }

        if (i1 != -1 && i2 != -1) {

            switch (type) {
                case Swap -> ListHelper.swap(shopEntries, i1, i2);
                case Insert -> ListHelper.insert(shopEntries, i1, i2);
            }

            return true;
        }

        return false;
    }

    public boolean swapTabs(UUID tabFrom, UUID tabTo, MoveType type) {
        int i1 = -1;
        int i2 = -1;

        for (int i = 0; i < shopTabs.size(); i++) {
            if (i1 != -1 && i2 != -1) break;

            UUID id = shopTabs.get(i).uuid;
            if (Objects.equals(id, tabFrom)) i1 = i;
            else if (Objects.equals(id, tabTo)) i2 = i;
        }

        if (i1 != -1 && i2 != -1) {

            switch (type) {
                case Swap -> ListHelper.swap(shopTabs, i1, i2);
                case Insert -> ListHelper.insert(shopTabs, i1, i2);
            }

            return true;
        }

        return false;
    }

    public boolean moveEntry(UUID entryId, MoveType direction) {
        int index = -1;
        for (int i = 0; i < shopEntries.size(); i++) {
            if (Objects.equals(shopEntries.get(i).uuid, entryId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            ShopDebugUtils.error("Entry with UUID {} not found!", entryId);
            return false;
        }

        int newIndex = switch (direction) {
            case Swap, Insert -> 0;
            case Up -> index - 1;
            case Down -> index + 1;
        };

        if (newIndex < 0 || newIndex >= shopEntries.size()) {
            ShopDebugUtils.log("Cannot move entry {} {}: already at {}!",
                    entryId, direction, newIndex < 0 ? "start" : "end");
            return false;
        }

        Collections.swap(shopEntries, index, newIndex);
        ShopDebugUtils.log("Moved entry {} {}: {} -> {}",
                entryId, direction, index, newIndex);
        return true;
    }

    public boolean moveTab(UUID tabId, MoveType direction) {
        int index = -1;
        for (int i = 0; i < shopTabs.size(); i++) {
            if (Objects.equals(shopTabs.get(i).uuid, tabId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            ShopDebugUtils.error("Tab with UUID {} not found!", tabId);
            return false;
        }

        int newIndex = switch (direction) {
            case Swap, Insert -> -1;
            case Up -> index - 1;
            case Down -> index + 1;
        };

        if (newIndex < 0 || newIndex >= shopTabs.size()) {
            ShopDebugUtils.log("Cannot move tab {} {}: already at {}!",
                    tabId, direction, newIndex < 0 ? "start" : "end");
            return false;
        }

        Collections.swap(shopTabs, index, newIndex);
        ShopDebugUtils.log("Moved tab {} {}: {} -> {}",
                tabId, direction, index, newIndex);
        return true;
    }

    public List<CompoundTag> splitToNetworkPackages() {
        List<CompoundTag> resultList = new ArrayList<>();

        List<List<ShopEntry>> entriesSplit = Lists.partition(shopEntries, SPLITTER_SIZE);

        for (List<ShopEntry> entryList : entriesSplit) {
            CompoundTag nbt = new CompoundTag();
            ListTag data = new ListTag();

            for (ShopEntry shopEntry : entryList) {
                data.add(shopEntry.serialize());
            }
            nbt.put(ENTRIES_KEY, data);
            resultList.add(nbt);
        }

        List<List<ShopTab>> tabsSplit = Lists.partition(shopTabs, SPLITTER_SIZE);

        for (List<ShopTab> tabs : tabsSplit) {
            CompoundTag nbt = new CompoundTag();
            ListTag data = new ListTag();

            for (ShopTab shopEntry : tabs) {
                data.add(shopEntry.serialize());
            }
            nbt.put(TABS_KEY, data);
            resultList.add(nbt);
        }

        return resultList;
    }

    public void deserializeSplitedData(CompoundTag nbt) {
        boolean isTab = nbt.contains(TABS_KEY);
        ListTag data = (ListTag) (isTab ? nbt.get(TABS_KEY) : nbt.get(ENTRIES_KEY));

        for (Tag datum : data) {
            CompoundTag compoundTag = (CompoundTag) datum;

            if(isTab) {
                ShopTab shopTab = new ShopTab(this);
                shopTab.deserialize(compoundTag);
                getShopTabs().add(shopTab);
            } else {
                ShopEntry shopEntry = new ShopEntry(this);
                shopEntry.deserialize(compoundTag);
                getShopEntries().add(shopEntry);
            }
        }
    }


    public void clearData() {
        getShopEntries().clear();
        getShopTabs().clear();

        onChange();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        shopParams.getConfig(group);
    }
}
