package net.sdm.sdmshopr.shop.limiter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientLimiterData implements INBTSerializable<CompoundTag> {

    public Map<UUID, LimiteEntry> ENTIES_MAP = new HashMap<>();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (LimiteEntry value : ENTIES_MAP.values()) {
            listTag.add(value.serializeNBT());
        }
        nbt.put("entries", listTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    public static class LimiteEntry implements INBTSerializable<CompoundTag>{
        public UUID entryID;
        public int countBuys;

        public LimiteEntry(UUID entryID, int countBuys){
            this.entryID = entryID;
            this.countBuys = countBuys;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("entryID", entryID.toString());
            nbt.putInt("countBuys", countBuys);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.entryID = UUID.fromString(nbt.getString("entryID"));
            this.countBuys = nbt.getInt("countBuys");
        }

        @Override
        public String toString() {
            return "LimiteEntry{" +
                    "entryID=" + entryID +
                    ", countBuys=" + countBuys +
                    '}';
        }
    }
    public void clear(){
        ENTIES_MAP.clear();
    }

    @Override
    public String toString() {
        return "ClientLimiterData{" +
                "ENTIES_MAP=" + ENTIES_MAP +
                '}';
    }
}
