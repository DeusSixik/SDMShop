package net.sdm.sdmshopr.shop.limiter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractLimiterData implements INBTSerializable<CompoundTag> {

    public Map<UUID, LimiteEntry> ENTIES_MAP = new HashMap<>();

    public void addEntry(LimiteEntry limiteEntry){
        ENTIES_MAP.put(limiteEntry.entryID , limiteEntry);
    }

    public void updatePlayer(UUID oldPlayer, UUID netPlayer){
        for (LimiteEntry value : ENTIES_MAP.values()) {
            if(value.usersData.containsKey(oldPlayer)) {
                Integer data = value.usersData.get(oldPlayer);
                value.usersData.remove(oldPlayer, data);
                value.usersData.put(netPlayer, data);
            }
        }
    }

    public int getCount(UUID uuid, @Nullable UUID playerUUID){
        LimiteEntry limiteEntry = ENTIES_MAP.get(uuid);
        if(limiteEntry == null) return 0;
        if(limiteEntry.isGlobal) {
            return limiteEntry.usersData.get(limiteEntry.entryID);
        }

        if(limiteEntry.usersData.containsKey(playerUUID)){
            return limiteEntry.usersData.get(playerUUID);
        }
        return 0;
    }

    public void addSellable(UUID uuid, boolean isGlobal, @Nullable UUID player, int count){
        LimiteEntry limiteEntry = ENTIES_MAP.get(uuid);
        if(limiteEntry == null) {
            LimiteEntry d1 = new LimiteEntry(uuid, isGlobal);
            if(isGlobal)
                d1.usersData.put(uuid, count);
            else d1.usersData.put(player, count);
            ENTIES_MAP.put(uuid, d1);
            return;
        }

        if(isGlobal) {
            limiteEntry.usersData.put(uuid, getCount(uuid, null));
        } else {
            limiteEntry.usersData.put(player, getCount(uuid, player) + count);
        }
    }

    public static class LimiteEntry implements INBTSerializable<CompoundTag>{
        public UUID entryID;
        public boolean isGlobal;
        public HashMap<UUID, Integer> usersData = new HashMap<>();


        public LimiteEntry(UUID entryID, boolean isGlobal){
            this.entryID = entryID;
            this.isGlobal = isGlobal;
        }

        public void addGlobal(int count){
            if(!usersData.containsKey(entryID)) usersData.put(entryID, count);
            else usersData.put(entryID, usersData.get(entryID) + count);
        }

        public void setGlobal(int count){
            usersData.put(entryID, count);
        }

        public void addBuys(UUID uuid, int count){
            Integer i = usersData.get(uuid);
            if(i != null) {
                usersData.put(uuid, i + count);
            } else {
                usersData.put(uuid, count);
            }
        }

        public void setBuys(UUID uuid, int count){
            usersData.put(uuid, count);
        }


        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("entryID", entryID.toString());
            nbt.putBoolean("isGlobal", isGlobal);

            ListTag users = new ListTag();
            for (Map.Entry<UUID, Integer> uuidIntegerEntry : usersData.entrySet()) {
                CompoundTag u = new CompoundTag();
                u.putString("userID", uuidIntegerEntry.getKey().toString());
                u.putInt("userBuys", uuidIntegerEntry.getValue());
                users.add(u);
            }
            nbt.put("users", users);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.entryID = UUID.fromString(nbt.getString("entryID"));
            this.isGlobal = nbt.getBoolean("isGlobal");
            usersData.clear();
            ListTag listTag = (ListTag) nbt.get("users");
            for (Tag tag : listTag) {
                CompoundTag u = (CompoundTag) tag;
                usersData.put(UUID.fromString(u.getString("userID")), u.getInt("userBuys"));

            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (LimiteEntry value : ENTIES_MAP.values()) {
            listTag.add(value.serializeNBT());
        }
        nbt.put("entry_list", listTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag listTag = (ListTag) nbt.get("entry_list");
        ENTIES_MAP.clear();
        for (Tag tag : listTag) {
            LimiteEntry limiteEntry = new LimiteEntry(null, false);
            limiteEntry.deserializeNBT((CompoundTag) tag);
            addEntry(limiteEntry);
        }
    }

    @Override
    public String toString() {
        return "AbstractLimiterData{" +
                "ENTIES_MAP=" + ENTIES_MAP +
                '}';
    }
}
