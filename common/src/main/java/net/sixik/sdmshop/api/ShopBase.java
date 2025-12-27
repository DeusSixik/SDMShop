package net.sixik.sdmshop.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopParams;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.HashUtils;
import net.sixik.sdmshop.utils.ListHelper;
import net.sixik.sdmshop.utils.RemoveResult;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface ShopBase {

    String NULL_HASH = "null_hash";           // Default Hash
    String ENTRIES_KEY = "shop_entries";      // Shop Entries
    String TABS_KEY = "shop_tabs";            // Shop Tabs
    String PARAMS_KEY = "shop_params";        // Shop Params

    ResourceLocation getRegistryId();

    UUID getId();

    default String getIdString() {
        return getId().toString();
    }

    ShopParams getParams();

    boolean isDirty();

    void setDirty(final boolean value);

    List<ShopChangeListener> getShopChangeListeners();

    /**
     * He says that there have been changes and subscribers need to be notified. It will work immediately without any checks
     */
    default void onChangeForce() {
        onChangeMethod();
    }

    /**
     * Checks if there are changes in the store and calls the method for subscribers
     */
    default void onChange() {
        if (!isDirty()) return;
        onChangeMethod();
    }

    default void onChangeMethod() {
        final List<ShopChangeListener> listeners = getShopChangeListeners();

        /*
            In order not to create an Iterator, we use 'for' with index
         */
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this);
        }
        ShopEvents.SHOP_CHANGE_EVENT.invoker().handle(this);
        onChangeEvent();

        setDirty(false);
    }

    default void clearData() {
        getEntries().clear();
        getTabs().clear();
        onChangeForce();
    }

    /**
     * Perhaps something needs to be done after all the listeners have done their business.
     */
    default void onChangeEvent() {
    }

    /**
     * The cached NBT in order not to read it again if the data is not updated
     */
    @Nullable Tag getCachedNbt();

    /**
     * The cached NBT in order not to read it again if the data is not updated
     */
    void setCachedNbt(final Tag nbt);

    /**
     * Version for data validation
     */
    String getVersion();

    /**
     * Version for data validation
     */
    void setVersion(final String version);

    /**
     * Calculates the Hash for the data so that its validity can be verified
     */
    default String calculateVersion() {
        if (!isDirty()) {
            final Tag nbt = getCachedNbt();
            if (nbt != null) {
                if (getVersion().isEmpty() && nbt instanceof CompoundTag compoundTag) {
                    return HashUtils.calculateHash(compoundTag);
                }
                return NULL_HASH;
            }
        }

        final DataResult<Tag> result = codecNetwork().encodeStart(NbtOps.INSTANCE, this);
        final Tag nbt = result.getOrThrow(false, SDMShop.LOGGER::error);
        if (!(nbt instanceof CompoundTag compoundTag)) return NULL_HASH;
        return HashUtils.calculateHash(compoundTag);
    }

    /**
     * Returns the list of all products in the store
     */
    List<ShopEntry> getEntries();

    /**
     * Returns the list of all the store's tabs
     */
    List<ShopTab> getTabs();

    /**
     * Saving/loading data
     */
    <T extends ShopBase> Codec<T> codec();

    /**
     * Send/Read data from network
     */
    default <T extends ShopBase> Codec<T> codecNetwork() {
        return codec();
    }

    @Nullable
    default ShopTab getTab(final UUID tabId) {
        if (tabId == null) return null;

        final List<ShopTab> list = getTabs();
        for (int i = 0; i < list.size(); i++) {
            final ShopTab tab = list.get(i);
            if (tabId.equals(tab.getId())) return tab;
        }
        return null;
    }

    default Optional<ShopTab> getTabOptional(final UUID tabId) {
        return Optional.ofNullable(getTab(tabId));
    }

    @Nullable
    default ShopTab getTab(final ShopEntry entry) {
        return getTab(entry.getTab());
    }

    default Optional<ShopTab> getTabOptional(final ShopEntry entry) {
        return getTabOptional(entry.getTab());
    }

    @Nullable
    default ShopEntry getEntry(final UUID entryId) {
        if (entryId == null) return null;

        final List<ShopEntry> list = getEntries();
        for (int i = 0; i < list.size(); i++) {
            final ShopEntry entry = list.get(i);
            if (entryId.equals(entry.getId())) return entry;
        }
        return null;
    }

    default Optional<ShopEntry> getEntryOptional(final UUID entryId) {
        return Optional.ofNullable(getEntry(entryId));
    }

    default List<ShopEntry> getEntriesByTab(final ShopTab tab) {
        return getEntriesByTab(tab.getId());
    }

    default List<ShopEntry> getEntriesByTab(final UUID tabId) {
        final ObjectArrayList<ShopEntry> entriesList = new ObjectArrayList<>();
        final List<ShopEntry> list = getEntries();

        for (int i = 0; i < list.size(); i++) {
            final ShopEntry entry = list.get(i);
            if (java.util.Objects.equals(entry.getTab(), tabId)) {
                entriesList.add(entry);
            }
        }
        return entriesList;
    }

    List<EntryAddListener> getEntryAddListeners();

    List<EntryRemoveListener> getEntryRemoveListeners();

    List<EntryChangeListener> getEntryChangeListeners();

    default boolean addEntry(final ShopEntry entry) {
        return addEntry(null, entry);
    }

    default boolean addEntry(@Nullable final ShopTab tab, final ShopEntry entry) {
        if (entry == null) return false;

        final ShopTab resolvedTab = (tab != null)
                ? tab
                : (entry.getTab() != null ? getTab(entry.getTab()) : null);

        if (resolvedTab == null) return false;

        if (getEntry(entry.getId()) != null) return false;

        getEntries().add(entry);
        onEntryAdd(entry, resolvedTab);
        setDirty(true);
        return true;
    }

    default RemoveResult removeEntry(final ShopEntry entryBase) {
        return removeEntry(entryBase.getId());
    }

    default RemoveResult removeEntry(final UUID entryBase) {
        if (entryBase == null) return RemoveResult.FAIL;

        final int idx = indexOfEntry(entryBase);
        if (idx < 0) return RemoveResult.FAIL;

        final ShopEntry removed = getEntries().remove(idx);

        final ShopTab tab = getTab(removed.getTab());
        if (tab == null) throw new NullPointerException("Tab is null!");

        onEntryRemove(removed, tab);
        setDirty(true);
        return new RemoveResult(true);
    }

    default RemoveResult removeEntry(final java.util.function.Predicate<ShopEntry> entryPredicate,
                                     final java.util.function.Consumer<ShopEntry> onFind) {
        if (entryPredicate == null) return RemoveResult.FAIL;
        if (onFind == null) return removeEntry(entryPredicate);

        final java.util.List<Integer> removedIndices = new java.util.ArrayList<>();
        final java.util.List<ShopEntry> list = getEntries();

        for (int i = 0; i < list.size(); i++) {
            final ShopEntry entry = list.get(i);
            if (entryPredicate.test(entry)) {
                onFind.accept(entry);

                final ShopEntry removed = list.remove(i);
                removedIndices.add(i);

                final ShopTab tab = getTab(removed.getTab());
                if (tab == null) throw new NullPointerException("Tab is null!");
                onEntryRemove(removed, tab);

                setDirty(true);
                return new RemoveResult(true, removedIndices);
            }
        }

        return new RemoveResult(false, removedIndices);
    }

    default RemoveResult removeEntry(final java.util.function.Predicate<ShopEntry> entryPredicate) {
        if (entryPredicate == null) return RemoveResult.FAIL;

        final java.util.List<Integer> removedIndices = new java.util.ArrayList<>();
        final java.util.List<ShopEntry> list = getEntries();

        for (int i = 0; i < list.size(); i++) {
            final ShopEntry entry = list.get(i);
            if (entryPredicate.test(entry)) {
                final ShopEntry removed = list.remove(i);
                removedIndices.add(i);

                final ShopTab tab = getTab(removed.getTab());
                if (tab == null) throw new NullPointerException("Tab is null!");
                onEntryRemove(removed, tab);

                setDirty(true);
                return new RemoveResult(true, removedIndices);
            }
        }

        return new RemoveResult(false, removedIndices);
    }

    default void entryChange(final UUID entryId, final java.util.function.Consumer<ShopEntry> consumer) {
        if (entryId == null || consumer == null) return;

        final ShopEntry entry = getEntry(entryId);
        if (entry == null) return;

        consumer.accept(entry);

        final ShopTab tab = getTab(entry.getTab());
        onEntryChange(entry, tab);
        setDirty(true);
    }

    default int indexOfEntry(final UUID entryId) {
        if (entryId == null) return -1;

        final List<ShopEntry> list = getEntries();
        for (int i = 0; i < list.size(); i++) {
            final ShopEntry entry = list.get(i);
            if (entryId.equals(entry.getId())) return i;
        }
        return -1;
    }

    default void onEntryAdd(final ShopEntry entry, final ShopTab tab) {
        final List<EntryAddListener> listeners = getEntryAddListeners();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this, entry, tab);
        }
        ShopEvents.ENTRY_ADD_EVENT.invoker().handle(this, entry, tab);
    }

    default void onEntryRemove(final ShopEntry entry, final ShopTab tab) {
        final List<EntryRemoveListener> listeners = getEntryRemoveListeners();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this, entry, tab);
        }
        ShopEvents.ENTRY_REMOVE_EVENT.invoker().handle(this, entry, tab);
    }

    default void onEntryChange(final ShopEntry entry, final ShopTab tab) {
        final List<EntryChangeListener> listeners = getEntryChangeListeners();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this, entry, tab);
        }
        ShopEvents.ENTRY_CHANGE_EVENT.invoker().handle(this, entry, tab);
    }

    List<TabAddListener> getTabAddListeners();

    List<TabRemoveListener> getTabRemoveListeners();

    List<TabChangeListener> getTabChangeListeners();

    default boolean addTab(final ShopTab tab) {
        if (tab == null) return false;

        final int idx = indexOfTab(tab.getId());
        if (idx >= 0) getTabs().set(idx, tab);
        else getTabs().add(tab);

        onTabAdd(tab);
        setDirty(true);
        return true;
    }

    default RemoveResult removeTab(final ShopTab tab) {
        return removeTab(tab.getId());
    }

    default RemoveResult removeTab(final UUID tab) {
        if (tab == null) return RemoveResult.FAIL;

        final int idx = indexOfTab(tab);
        if (idx < 0) return RemoveResult.FAIL;

        final ShopTab removed = getTabs().remove(idx);
        onTabRemove(removed);
        setDirty(true);
        return RemoveResult.SUCCESS;
    }

    default int indexOfTab(final UUID tabId) {
        if (tabId == null) return -1;

        final List<ShopTab> list = getTabs();
        for (int i = 0; i < list.size(); i++) {
            final ShopTab tab = list.get(i);
            if (tabId.equals(tab.getId())) return i;
        }
        return -1;
    }

    default void changeTab(final UUID tabId, final java.util.function.Consumer<ShopTab> consumer) {
        if (tabId == null || consumer == null) return;

        final ShopTab tab = getTab(tabId);
        if (tab == null) return;

        consumer.accept(tab);
        onTabChange(tab);
        setDirty(true);
    }

    default void onTabAdd(final ShopTab tab) {
        final var list = getTabAddListeners();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).handle(this, tab);
        }
        ShopEvents.TAB_ADD_EVENT.invoker().handle(this, tab);
    }

    default void onTabRemove(final ShopTab tab) {
        final var list = getTabRemoveListeners();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).handle(this, tab);
        }
        ShopEvents.TAB_REMOVE_EVENT.invoker().handle(this, tab);
    }

    default void onTabChange(final ShopTab tab) {
        final var list = getTabChangeListeners();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).handle(this, tab);
        }
        ShopEvents.TAB_CHANGE_EVENT.invoker().handle(this, tab);
    }

    default boolean swapEntries(final UUID entryFrom, final UUID entryTo, final MoveType type) {
        if (entryFrom == null || entryTo == null || type == null) return false;

        final List<ShopEntry> entries = getEntries();

        int i1 = -1;
        int i2 = -1;

        for (int i = 0; i < entries.size(); i++) {
            if (i1 != -1 && i2 != -1) break;

            final ShopEntry e = entries.get(i);
            if (e == null) continue;

            final UUID id = e.getId();
            if (java.util.Objects.equals(id, entryFrom)) i1 = i;
            else if (java.util.Objects.equals(id, entryTo)) i2 = i;
        }

        if (i1 == -1 || i2 == -1) return false;

        switch (type) {
            case Swap -> ListHelper.swap(entries, i1, i2);
            case Insert -> ListHelper.insert(entries, i1, i2);
            default -> {
                return false;
            }
        }

        final ShopEntry e1 = entries.get(i2);
        final ShopEntry e2 = entries.get(i1);

        if (e1 != null) onEntryChange(e1, getTab(e1.getTab()));
        if (e2 != null) onEntryChange(e2, getTab(e2.getTab()));

        setDirty(true);
        return true;
    }

    default boolean swapTabs(final UUID tabFrom, final UUID tabTo, final MoveType type) {
        if (tabFrom == null || tabTo == null || type == null) return false;

        final List<ShopTab> tabs = getTabs();

        int i1 = -1;
        int i2 = -1;

        for (int i = 0; i < tabs.size(); i++) {
            if (i1 != -1 && i2 != -1) break;

            final ShopTab t = tabs.get(i);
            if (t == null) continue;

            final UUID id = t.getId();
            if (java.util.Objects.equals(id, tabFrom)) i1 = i;
            else if (java.util.Objects.equals(id, tabTo)) i2 = i;
        }

        if (i1 == -1 || i2 == -1) return false;

        switch (type) {
            case Swap -> ListHelper.swap(tabs, i1, i2);
            case Insert -> ListHelper.insert(tabs, i1, i2);
            default -> {
                return false;
            }
        }

        final ShopTab t1 = tabs.get(i2);
        final ShopTab t2 = tabs.get(i1);

        if (t1 != null) onTabChange(t1);
        if (t2 != null) onTabChange(t2);

        setDirty(true);
        return true;
    }

    default boolean moveEntry(final UUID entryId, final MoveType direction) {
        if (entryId == null || direction == null) return false;

        final List<ShopEntry> entries = getEntries();

        int index = -1;
        for (int i = 0; i < entries.size(); i++) {
            final ShopEntry e = entries.get(i);
            if (e != null && java.util.Objects.equals(e.getId(), entryId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            ShopDebugUtils.error("Entry with UUID {} not found!", entryId);
            return false;
        }

        if (direction == MoveType.Swap || direction == MoveType.Insert) {
            ShopDebugUtils.log("Cannot move entry {} with {}: use swapEntries(...) instead!", entryId, direction);
            return false;
        }

        final int newIndex = switch (direction) {
            case Up -> index - 1;
            case Down -> index + 1;
            default -> index;
        };

        if (newIndex < 0 || newIndex >= entries.size()) {
            ShopDebugUtils.log("Cannot move entry {} {}: already at {}!",
                    entryId, direction, newIndex < 0 ? "start" : "end");
            return false;
        }

        java.util.Collections.swap(entries, index, newIndex);

        final ShopEntry moved = entries.get(newIndex);
        if (moved != null) onEntryChange(moved, getTab(moved.getTab()));

        setDirty(true);

        ShopDebugUtils.log("Moved entry {} {}: {} -> {}",
                entryId, direction, index, newIndex);
        return true;
    }

    default boolean moveTab(final UUID tabId, final MoveType direction) {
        if (tabId == null || direction == null) return false;

        final List<ShopTab> tabs = getTabs();

        int index = -1;
        for (int i = 0; i < tabs.size(); i++) {
            final ShopTab t = tabs.get(i);
            if (t != null && java.util.Objects.equals(t.getId(), tabId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            ShopDebugUtils.error("Tab with UUID {} not found!", tabId);
            return false;
        }

        if (direction == MoveType.Swap || direction == MoveType.Insert) {
            ShopDebugUtils.log("Cannot move tab {} with {}: use swapTabs(...) instead!", tabId, direction);
            return false;
        }

        final int newIndex = switch (direction) {
            case Up -> index - 1;
            case Down -> index + 1;
            default -> index;
        };

        if (newIndex < 0 || newIndex >= tabs.size()) {
            ShopDebugUtils.log("Cannot move tab {} {}: already at {}!",
                    tabId, direction, newIndex < 0 ? "start" : "end");
            return false;
        }

        java.util.Collections.swap(tabs, index, newIndex);

        final ShopTab moved = tabs.get(newIndex);
        if (moved != null) onTabChange(moved);

        setDirty(true);

        ShopDebugUtils.log("Moved tab {} {}: {} -> {}",
                tabId, direction, index, newIndex);
        return true;
    }

    CompoundTag serializeOrCache();

    static boolean isVersionNull(final String version) {
        return Objects.equals(version, NULL_HASH);
    }

    @FunctionalInterface
    interface EntryBuyListener {

        void handle(final ShopBase base, final ShopEntry entry, final ShopTab tab, final ServerPlayer player, int count);
    }

    @FunctionalInterface
    interface EntrySellListener {

        void handle(final ShopBase base, final ShopEntry entry, final ShopTab tab, final ServerPlayer player, int count);
    }

    @FunctionalInterface
    interface ShopChangeListener {

        void handle(ShopBase base);
    }

    @FunctionalInterface
    interface EntryAddListener {

        void handle(final ShopBase shop, final ShopEntry entry, final ShopTab tab);
    }

    @FunctionalInterface
    interface EntryRemoveListener {

        void handle(final ShopBase shop, final ShopEntry entry, final ShopTab tab);
    }

    @FunctionalInterface
    interface EntryChangeListener {

        void handle(final ShopBase shop, final ShopEntry entry, final ShopTab tab);
    }

    @FunctionalInterface
    interface TabAddListener {

        void handle(final ShopBase shop, final ShopTab tab);
    }

    @FunctionalInterface
    interface TabRemoveListener {

        void handle(final ShopBase shop, final ShopTab tab);
    }

    @FunctionalInterface
    interface TabChangeListener {

        void handle(final ShopBase shop, final ShopTab tab);
    }
}
