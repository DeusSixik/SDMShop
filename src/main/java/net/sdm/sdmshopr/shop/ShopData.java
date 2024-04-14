package net.sdm.sdmshopr.shop;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.network.SyncShopData;

public class ShopData implements INBTSerializable<CompoundTag> {
    public boolean isNew = false;
    public int countMoney = 0;

    protected ShopData(){}

    public static ShopData of(Player player){
        if(player.getPersistentData().contains("sdmshop")){
            return getData(player);
        }
        ShopData data = new ShopData();
        data.isNew = true;
        player.getPersistentData().put("sdmshop", data.serializeNBT());
        return data;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("money", countMoney);
        nbt.putBoolean("isNew", false);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        countMoney = nbt.getInt("money");
        isNew = nbt.getBoolean("isNew");
    }

    public void addMoney(int count){
        this.countMoney += count;
    }

    public void setMoney(int count){
        this.countMoney = count;
    }

    public void saveOnPlayerData(ServerPlayer player){
        CompoundTag nbt = serializeNBT();
        player.getPersistentData().put("sdmshop", nbt);
        new SyncShopData(nbt).sendTo(player);
    }

    public void saveOnPlayerData(Player player){
        CompoundTag nbt = serializeNBT();
        player.getPersistentData().put("sdmshop", nbt);
    }

    public static ShopData getData(Player player){
        CompoundTag nbt = player.getPersistentData().getCompound("sdmshop");
        ShopData data = new ShopData();
        if(!nbt.isEmpty()) data.deserializeNBT(nbt);
        return data;
    }
}
