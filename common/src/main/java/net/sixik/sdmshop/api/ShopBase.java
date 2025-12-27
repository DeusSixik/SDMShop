package net.sixik.sdmshop.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.HashUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface ShopBase {

    String NULL_HASH = "null_hash"; // Default Hash
    String ENTRIES_KEY = "SE";      // Shop Entries
    String TABS_KEY = "ST";         // Shop Tabs

    ResourceLocation getId();

    boolean isDirty();

    void setDirty(final boolean value);

    List<ShopChangeListener> getListeners();

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
        if(!isDirty()) return;
        onChangeMethod();
    }

    default void onChangeMethod() {
        final List<ShopChangeListener> listeners = getListeners();

        /*
            In order not to create an Iterator, we use 'for' with index
         */
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this);
        }
        onChangeEvent();

        setDirty(false);
    }

    /**
     * Perhaps something needs to be done after all the listeners have done their business.
     */
    default void onChangeEvent() { }

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
     * Calculates the Hash for the data so that its validity can be verified
     */
    default String calculateVersion() {
        if(!isDirty()) {
            final Tag nbt = getCachedNbt();
            if(nbt != null) {
                if(getVersion().isEmpty() && nbt instanceof CompoundTag compoundTag) {
                    return HashUtils.calculateHash(compoundTag);
                }
                return NULL_HASH;
            }
        }

        final DataResult<Tag> result = codecNetwork().encodeStart(NbtOps.INSTANCE, this);
        final Tag nbt = result.getOrThrow(false, SDMShop.LOGGER::error);
        if(!(nbt instanceof CompoundTag compoundTag)) return NULL_HASH;
        return HashUtils.calculateHash(compoundTag);
    }

    /**
     * Returns the map of all products in the store
     */
    Map<UUID, ShopEntry> getEntries();

    /**
     * Returns the map of all the store's tabs
     */
    Map<UUID, ShopTab> getTabs();

    /**
     * Saving/loading data
     */
    Codec<ShopBase> codec();

    /**
     * Send/Read data from network
     */
    default Codec<ShopBase> codecNetwork() {
        return codec();
    }

    @Nullable
    default ShopTab getTab(final UUID tabId) {
        return getTabs().getOrDefault(tabId, null);
    }

    default Optional<ShopTab> getTabOptional(final UUID tabId) {
        return Optional.ofNullable(getTab(tabId));
    }

    @Nullable
    default ShopEntry getEntry(final UUID entryId) {
        return getEntries().getOrDefault(entryId, null);
    }

    default Optional<ShopEntry> getEntryOptional(final UUID entryId) {
        return Optional.ofNullable(getEntry(entryId));
    }

    default List<ShopEntry> getEntriesByTab(final ShopTab tab) {
        return getEntriesByTab(tab.getId());
    }

    default List<ShopEntry> getEntriesByTab(final UUID tabId) {
        final ObjectArrayList<ShopEntry> entiesList = new ObjectArrayList<>();
        for (ShopEntry entry : getEntries().values()) {
            if(entry.getTab().equals(tabId)) entiesList.add(entry);
        }

        return entiesList;
    }

    List<EntryAddListener> getEntryAddListeners();

    List<EntryRemoveListener> getEntryRemoveListeners();

    List<EntryChangeListener> getEntryChangeListeners();

    default boolean addEntry(@Nullable final ShopTab tab, final ShopEntry entry) {
        final ShopTab _tab = tab == null ? (entry.getTab() != null ? getTabs().get(entry.getTab()) : null) : tab;
        if(_tab == null) return false;
        getEntries().put(entry.getId(), entry);
        onEntryAdd(entry, tab);
        setDirty(true);
        return true;
    }

    default boolean removeEntry(final ShopEntry entryBase) {
        if(!getEntries().containsKey(entryBase.getId())) return false;
        final ShopTab tab = getTab(entryBase.getTab());
        if(tab == null) throw new NullPointerException("Tab is null!");
        final boolean value = getEntries().remove(entryBase.getId(), entryBase);
        onEntryRemove(entryBase, tab);
        setDirty(true);
        return value;
    }

    default void entryChange(final UUID entryId, Consumer<ShopEntry> consumer) {
        final var entry = getEntry(entryId);
        if(entry == null) return;
        consumer.accept(entry);
        onEntryChange(entry, getTab(entry.getTab()));
        setDirty(true);
    }

    default void onEntryAdd(final ShopEntry entry, final ShopTab tab) {
        final List<EntryAddListener> listeners = getEntryAddListeners();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this, entry, tab);
        }
    }

    default void onEntryRemove(final ShopEntry entry, final ShopTab tab) {
        final List<EntryRemoveListener> listeners = getEntryRemoveListeners();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this, entry, tab);
        }
    }

    default void onEntryChange(final ShopEntry entry, final ShopTab tab) {
        final List<EntryChangeListener> listeners = getEntryChangeListeners();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handle(this, entry, tab);
        }
    }

    List<TabAddListener> getTabAddListeners();

    List<TabRemoveListener> getTabRemoveListeners();

    List<TabChangeListener> getTabChangeListeners();

    default void addTab(final ShopTab tab) {
        getTabs().put(tab.getId(), tab);
        onTabAdd(tab);
        setDirty(true);
    }

    default void removeTab(final ShopTab tab) {
        if(getTabs().remove(tab.getId(), tab)) {
            onTabRemove(tab);
            setDirty(true);
        }
    }

    default void changeTab(final UUID tabId, final Consumer<ShopTab> consumer) {
        final ShopTab tab = getTab(tabId);
        if(tab == null) return;
        consumer.accept(tab);
        onTabChange(tab);
        setDirty(true);
    }

    default void onTabAdd(final ShopTab tab) {
        final var list = getTabAddListeners();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).handle(this, tab);
        }
    }

    default void onTabRemove(final ShopTab tab) {
        final var list = getTabRemoveListeners();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).handle(this, tab);
        }
    }

    default void onTabChange(final ShopTab tab) {
        final var list = getTabChangeListeners();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).handle(this, tab);
        }
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
