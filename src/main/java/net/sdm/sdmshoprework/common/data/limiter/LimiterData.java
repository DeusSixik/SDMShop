package net.sdm.sdmshoprework.common.data.limiter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LimiterData implements INBTSerializable<CompoundTag> {

    public static LimiterData SERVER;
    public static LimiterData CLIENT = new LimiterData();


    public Map<UUID, Map<UUID, Integer>> PLAYER_DATA = new HashMap<>();


    public Map<UUID, Integer> LIMITER_DATA = new HashMap<>();


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag tagPlayerData = new ListTag();
        for (UUID uuid : PLAYER_DATA.keySet()) {
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
        PLAYER_DATA.clear();
        for (Tag tagPlayersDatum : tagPlayersData) {
            CompoundTag d1 = (CompoundTag) tagPlayersDatum;
            UUID playerID = d1.getUUID("playerID");
            ListTag f1 = (ListTag) d1.get("player");
            Map<UUID, Integer> j1 = new HashMap<>();
            for (Tag tag : f1) {
                CompoundTag tag1 = (CompoundTag) tag;
                j1.put(tag1.getUUID("id"), tag1.getInt("count"));
            }
            PLAYER_DATA.put(playerID, j1);
        }
    }

    public CompoundTag serializeClient(UUID uuid) {
        CompoundTag f1 = new CompoundTag();
        ListTag d1 = new ListTag();
        for (Map.Entry<UUID, Integer> h : PLAYER_DATA.getOrDefault(uuid, new HashMap<>()).entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("id", h.getKey());
            nbt.putInt("count", h.getValue());
            d1.add(nbt);
        }

        f1.put("player", d1);
        return f1;
    }

    public void deserializeClient(CompoundTag nbt) {
        LIMITER_DATA.clear();
        ListTag tag = (ListTag) nbt.get("player");
        for (Tag tag1 : tag) {
            CompoundTag d1 = (CompoundTag) tag1;
            LIMITER_DATA.put(d1.getUUID("id"), d1.getInt("count"));
        }
    }
}
