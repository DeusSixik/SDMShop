package net.sixik.sdmshoprework.common.data;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdmshoprework.api.INBTSerializable;

import java.nio.file.Path;
import java.util.*;

public class LimiterData implements INBTSerializable<CompoundTag> {

    public static LimiterData SERVER;
    public static LimiterData CLIENT = new LimiterData();

    public static UUID defaul_ = UUID.fromString("57874c37-f8fe-4090-927e-be008faa95ed");

    public Map<UUID, Map<UUID, Integer>> PLAYER_TAB_DATA = new HashMap<>();
    public Map<UUID, Integer> TAB_DATA = new HashMap<>();

    public Map<UUID, Map<UUID, Integer>> PLAYER_ENTRY_DATA = new HashMap<>();
    public Map<UUID, Integer> ENTRY_DATA = new HashMap<>();


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        List<UUID> u = new ArrayList<>(PLAYER_ENTRY_DATA.keySet());
        u.addAll(ENTRY_DATA.keySet());

        ListTag tagPlayerData = new ListTag();
        for (UUID uuid : u) {
            CompoundTag d1 = serializeClient(uuid);
            d1.putUUID("playerID", uuid);
            tagPlayerData.add(d1);
        }
        nbt.put("players", tagPlayerData);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag tagPlayersData = (ListTag) nbt.get("players");
        PLAYER_TAB_DATA.clear();
        PLAYER_ENTRY_DATA.clear();
        for (Tag tagPlayersDatum : tagPlayersData) {
            CompoundTag d1 = (CompoundTag) tagPlayersDatum;
            UUID playerID = d1.getUUID("playerID");

            ListTag f1 = new ListTag();

            if(d1.contains("player_entry"))
                f1 = (ListTag) d1.get("player_entry");
            else if(d1.contains("player"))
                f1 = (ListTag) d1.get("player");

            Map<UUID, Integer> j1 = new HashMap<>();
            for (Tag tag : f1) {
                CompoundTag tag1 = (CompoundTag) tag;
                j1.put(tag1.getUUID("id"), tag1.getInt("count"));
            }
            PLAYER_ENTRY_DATA.put(playerID, j1);


            if(d1.contains("player_tab")) {
                f1 = (ListTag) d1.get("player_tab");
            } else f1 = new ListTag();
            j1 = new HashMap<>();
            for (Tag tag : f1) {
                CompoundTag tag1 = (CompoundTag) tag;
                j1.put(tag1.getUUID("id"), tag1.getInt("count"));
            }
            PLAYER_TAB_DATA.put(playerID, j1);
        }
    }

    public CompoundTag serializeClient(UUID uuid) {
        CompoundTag f1 = new CompoundTag();
        ListTag d1 = new ListTag();
        for (Map.Entry<UUID, Integer> h : PLAYER_ENTRY_DATA.getOrDefault(uuid, new HashMap<>()).entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("id", h.getKey());
            nbt.putInt("count", h.getValue());
            d1.add(nbt);
        }
        for (Map.Entry<UUID, Integer> h : PLAYER_ENTRY_DATA.getOrDefault(defaul_, new HashMap<>()).entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("id", h.getKey());
            nbt.putInt("count", h.getValue());
            d1.add(nbt);
        }

        f1.put("player_entry", d1);

        ListTag d2 = new ListTag();
        for (Map.Entry<UUID, Integer> h : PLAYER_TAB_DATA.getOrDefault(uuid, new HashMap<>()).entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("id", h.getKey());
            nbt.putInt("count", h.getValue());
            d2.add(nbt);
        }
        for (Map.Entry<UUID, Integer> h : PLAYER_TAB_DATA.getOrDefault(defaul_, new HashMap<>()).entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("id", h.getKey());
            nbt.putInt("count", h.getValue());
            d2.add(nbt);
        }
        f1.put("player_tab", d2);

        return f1;
    }

    public void deserializeClient(CompoundTag nbt) {
        ENTRY_DATA.clear();

        ListTag tag = new ListTag();

        if(nbt.contains("player_entry"))
            tag = (ListTag) nbt.get("player_entry");
        else if(nbt.contains("player"))
            tag = (ListTag) nbt.get("player");

        for (Tag tag1 : tag) {
            CompoundTag d1 = (CompoundTag) tag1;
            ENTRY_DATA.put(d1.getUUID("id"), d1.getInt("count"));
        }

        TAB_DATA.clear();
        if(nbt.contains("player_tab"))
            tag = (ListTag) nbt.get("player_tab");
        else tag = new ListTag();

        for (Tag tag1 : tag) {
            CompoundTag d1 = (CompoundTag) tag1;
            TAB_DATA.put(d1.getUUID("id"), d1.getInt("count"));
        }
    }

    public void save(MinecraftServer server) {

       Path path = server.getWorldPath(LevelResource.ROOT).resolve("shop_limit_data.snbt");
        SNBT.write(path, serializeNBT());
    }

    public void load(MinecraftServer server) {

       Path path = server.getWorldPath(LevelResource.ROOT).resolve("shop_limit_data.snbt");
       var data = SNBT.read(path);
       if (data != null) deserializeNBT(data);
    }

}
