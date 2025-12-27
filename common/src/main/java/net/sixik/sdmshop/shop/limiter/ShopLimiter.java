package net.sixik.sdmshop.shop.limiter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.utils.ErrorCodes;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

import java.nio.file.Path;
import java.util.*;

public class ShopLimiter implements DataSerializerCompoundTag {

    private static final Map<UUID, Integer> NULL_MAP = new HashMap<>(0);

    public static final Path LIMITER_FOLDER = Path.of("limiter");
    public static final String FILE_NAME = "limiter.data";

    private static final UUID DEFAULT_UUID = UUID.fromString("57874c37-f8fe-4090-927e-be008faa95ed");
    private static final String PLAYERS_TAG = "players";
    private static final String PLAYER_ID_TAG = "playerID";
    private static final String PLAYER_ENTRY_TAG = "player_entry";
    private static final String PLAYER_TAG = "player";
    private static final String PLAYER_TAB_TAG = "player_tab";
    private static final String ID_TAG = "id";
    private static final String COUNT_TAG = "count";

    public ShopLimiter() {}

    private final Map<UUID, Map<UUID, Integer>> playerTabData = new HashMap<>();
    private final Map<UUID, Integer> tabData = new HashMap<>();
    private final Map<UUID, Map<UUID, Integer>> playerEntryData = new HashMap<>();
    private final Map<UUID, Integer> entryData = new HashMap<>();

    public ErrorCodes deleteTabData(UUID tabId) {
        Map<UUID, Integer> data = playerTabData.getOrDefault(tabId, null);
        if(data == null) {
            Integer data2 = tabData.getOrDefault(tabId, -1);
            if(data2 == -1) return ErrorCodes.NOT_FOUND;

            tabData.remove(tabId);
        } else {
            playerTabData.remove(tabId);
        }

        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes deleteEntryData(UUID tabId) {
        Map<UUID, Integer> data = playerEntryData.getOrDefault(tabId, null);
        if(data == null) {
            Integer data2 = entryData.getOrDefault(tabId, -1);
            if(data2 == -1) return ErrorCodes.NOT_FOUND;

            entryData.remove(tabId);
        } else {
            playerEntryData.remove(tabId);
        }

        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetAllDataGlobal() {
        tabData.clear();
        entryData.clear();
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetAllData(final UUID id, ShopObjectTypes types) {
        return switch (types) {
            case SHOP_ENTRY -> resetEntryDataAll(id);
            case SHOP_TAB -> resetTabDataAll(id);
            default -> throw new IllegalStateException("Unexpected value: " + types);
        };
    }

    public ErrorCodes resetAllData(Player player) {
        return resetAllData(player.getGameProfile().getId());
    }

    public ErrorCodes resetAllData(UUID playerId) {
        if(!playerEntryData.containsKey(playerId) || playerTabData.containsKey(playerId))
            return ErrorCodes.NOT_FOUND;

        playerEntryData.getOrDefault(playerId, NULL_MAP).clear();
        playerTabData.getOrDefault(playerId, NULL_MAP).clear();

        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetTabData(UUID tabId, Player player) {
        return resetTabData(tabId, player.getGameProfile().getId());
    }

    public ErrorCodes resetTabData(UUID tabId, UUID playerId) {
        Map<UUID, Integer> value = playerTabData.getOrDefault(tabId, new HashMap<>());
        if(value.isEmpty()) return ErrorCodes.NOT_FOUND;
        value.put(playerId, 0);
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetTabData(UUID tabId) {
        Integer id = tabData.getOrDefault(tabId, -1);
        if(id == -1) return ErrorCodes.NOT_FOUND;
        tabData.put(tabId, 0);
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetTabDataAll(UUID tabId) {
        ErrorCodes code = resetTabData(tabId);
        if(code.isSuccess()) return code;

        Map<UUID, Integer> tabData = playerTabData.getOrDefault(tabId, new HashMap<>());
        if(tabData.isEmpty()) return ErrorCodes.NOT_FOUND;
        tabData.clear();
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetEntryData(UUID entryId, Player player) {
        return resetEntryData(entryId, player.getGameProfile().getId());
    }

    public ErrorCodes resetEntryData(UUID entryId, UUID playerId) {
        Map<UUID, Integer> value = playerEntryData.getOrDefault(entryId, new HashMap<>());
        if(value.isEmpty()) return ErrorCodes.NOT_FOUND;
        value.put(playerId, 0);
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetEntryData(UUID entryId) {
        Integer id = entryData.getOrDefault(entryId, -1);
        if(id == -1) return ErrorCodes.NOT_FOUND;
        entryData.put(entryId, 0);
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes resetEntryDataAll(UUID entryId) {
        ErrorCodes code = resetEntryData(entryId);
        if(code.isSuccess()) return code;

        Map<UUID, Integer> tabData = playerEntryData.getOrDefault(entryId, new HashMap<>());
        if(tabData.isEmpty()) return ErrorCodes.NOT_FOUND;
        tabData.clear();
        return ErrorCodes.SUCCESS;
    }

    public Optional<Integer> getTabData(UUID tabId, Player player) {
        return getTabData(tabId, player.getGameProfile().getId());
    }

    public Optional<Integer> getTabData(UUID tabId, UUID playerId) {
        Map<UUID, Integer> value = playerTabData.getOrDefault(tabId, new HashMap<>());
        if(value.isEmpty()) return Optional.empty();

        Integer result = value.getOrDefault(playerId, null);
        if(result == null) return Optional.empty();
        return Optional.of(result);
    }

    public boolean containsTabData(UUID uuid) {
        return tabData.containsKey(uuid) || playerTabData.containsKey(uuid);
    }

    public boolean containsEntryData(UUID uuid) {
        return entryData.containsKey(uuid) || playerEntryData.containsKey(uuid);
    }

    public Optional<Integer> getTabData(UUID tabId) {
        Integer result = tabData.getOrDefault(tabId, null);
        if(result == null) return Optional.empty();
        return Optional.of(result);
    }

    public Optional<Integer> getEntryData(UUID entryId, Player player) {
        return getEntryData(entryId, player.getGameProfile().getId());
    }

    public Optional<Integer> getEntryData(UUID entryId, UUID playerId) {
        Map<UUID, Integer> value = playerEntryData.getOrDefault(entryId, new HashMap<>());
        if(value.isEmpty()) return Optional.empty();

        Integer result = value.getOrDefault(playerId, null);
        if(result == null) return Optional.empty();
        return Optional.of(result);
    }

    public Optional<Integer> getEntryData(UUID entryId) {
        Integer result = entryData.getOrDefault(entryId, null);
        if(result == null) return Optional.empty();
        return Optional.of(result);
    }

    public void addEntryData(UUID uuid, int count) {
        entryData.merge(uuid, count, Integer::sum);
    }

    public void addOrSetEntryData(UUID uuid, int count) {
        if(entryData.containsKey(uuid))
            addEntryData(uuid, count);
        else entryData.put(uuid, count);
    }

    public void addEntryData(UUID uuid, Player player, int count) {
        addEntryData(uuid, player.getGameProfile().getId(), count);
    }

    public void addEntryData(UUID uuid, UUID playerId, int count) {
        playerEntryData
                .computeIfAbsent(uuid, k -> new HashMap<>())
                .merge(playerId, count, Integer::sum);
    }

    public void addOrSetEntryData(UUID uuid, UUID playerId, int count) {
        final Map<UUID, Integer> map = playerEntryData.computeIfAbsent(uuid, k -> new HashMap<>());
        if(map.containsKey(playerId))
            map.merge(playerId, count, Integer::sum);
        else map.put(playerId, count);

        playerEntryData.put(uuid, map);
    }

    public void setEntryData(UUID uuid, int count) {
        entryData.put(uuid, count);
    }

    public void setEntryData(UUID uuid, Player player, int count) {
        setEntryData(uuid, player.getGameProfile().getId(), count);
    }

    public void setEntryData(UUID uuid, UUID playerId, int count) {
        playerEntryData.computeIfAbsent(uuid, k -> new HashMap<>()).put(playerId, count);
    }

    public void addTabData(UUID uuid, int count) {
        tabData.merge(uuid, count, Integer::sum);
    }

    public void addTabData(UUID uuid, Player player, int count) {
        addTabData(uuid, player.getGameProfile().getId(), count);
    }

    public void addTabData(UUID uuid, UUID playerId, int count) {
        playerTabData
                .computeIfAbsent(uuid, k -> new HashMap<>())
                .merge(playerId, count, Integer::sum);
    }

    public void setTabData(UUID uuid, int count) {
        tabData.put(uuid, count);
    }


    public void setTabData(UUID uuid, Player player, int count) {
        setTabData(uuid, player.getGameProfile().getId(), count);
    }

    public void setTabData(UUID uuid, UUID playerId, int count) {
        playerTabData
                .computeIfAbsent(uuid, k -> new HashMap<>())
                .put(playerId, count);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        ListTag playerData = new ListTag();

        Set<UUID> playerIds = new HashSet<>(playerEntryData.keySet());
        playerIds.addAll(entryData.keySet());

        if(!playerIds.isEmpty()) {
            for (UUID playerId : playerIds) {
                CompoundTag playerTag = serializeClient(playerId);
                playerTag.putUUID(PLAYER_ID_TAG, playerId);
                playerData.add(playerTag);
            }

            nbt.put(PLAYERS_TAG, playerData);
        }
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        playerTabData.clear();
        playerEntryData.clear();

        if(nbt.contains(PLAYERS_TAG)) {
            ListTag playerData = nbt.getList(PLAYERS_TAG, Tag.TAG_COMPOUND);
            for (Tag tag : playerData) {
                CompoundTag playerTag = (CompoundTag) tag;
                UUID playerId = playerTag.getUUID(PLAYER_ID_TAG);

                Map<UUID, Integer> entryMap = deserializeMap(playerTag, PLAYER_ENTRY_TAG, PLAYER_TAG);
                playerEntryData.put(playerId, entryMap);

                Map<UUID, Integer> tabMap = deserializeMap(playerTag, PLAYER_TAB_TAG);
                playerTabData.put(playerId, tabMap);
            }
        }
    }

    public CompoundTag serializeClient(Player player) {
        return serializeClient(player.getGameProfile().getId());
    }

    public CompoundTag serializeClient(UUID uuid) {
        CompoundTag result = new CompoundTag();

        ListTag entryList = serializeMap(playerEntryData.getOrDefault(uuid, new HashMap<>()));
        entryList.addAll(serializeMap(playerEntryData.getOrDefault(DEFAULT_UUID, new HashMap<>())));
        result.put(PLAYER_ENTRY_TAG, entryList);

        ListTag tabList = serializeMap(playerTabData.getOrDefault(uuid, new HashMap<>()));
        tabList.addAll(serializeMap(playerTabData.getOrDefault(DEFAULT_UUID, new HashMap<>())));
        result.put(PLAYER_TAB_TAG, tabList);

        return result;
    }

    public void deserializeClient(CompoundTag nbt) {
        entryData.clear();
        tabData.clear();

        ListTag entryList = getListTag(nbt, PLAYER_ENTRY_TAG, PLAYER_TAG);
        entryData.putAll(deserializeMap(entryList));

        ListTag tabList = getListTag(nbt, PLAYER_TAB_TAG);
        tabData.putAll(deserializeMap(tabList));
    }

    private ListTag serializeMap(Map<UUID, Integer> map) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Integer> entry : map.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID(ID_TAG, entry.getKey());
            tag.putInt(COUNT_TAG, entry.getValue());
            list.add(tag);
        }
        return list;
    }

    private Map<UUID, Integer> deserializeMap(ListTag list) {
        Map<UUID, Integer> result = new HashMap<>();
        for (Tag tag : list) {
            CompoundTag compound = (CompoundTag) tag;
            result.put(compound.getUUID(ID_TAG), compound.getInt(COUNT_TAG));
        }
        return result;
    }

    private Map<UUID, Integer> deserializeMap(CompoundTag tag, String... keys) {
        ListTag list = getListTag(tag, keys);
        return deserializeMap(list);
    }

    private ListTag getListTag(CompoundTag tag, String... keys) {
        for (String key : keys) {
            if (tag.contains(key)) {
                return tag.getList(key, Tag.TAG_COMPOUND);
            }
        }
        return new ListTag();
    }
}
