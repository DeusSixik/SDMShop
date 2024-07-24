package net.sdm.sdmshopr.data;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.network.SyncShopGlobalData;
import net.sdm.sdmshopr.shop.limiter.AbstractLimiterData;
import net.sdm.sdmshopr.shop.limiter.ServerLimiterData;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class ServerShopData implements INBTSerializable<CompoundTag> {

    public static ServerShopData INSTANCE = null;

    public final MinecraftServer server;

    public ServerLimiterData limiterData;

    public ServerShopData(MinecraftServer server){
        this.server = server;
        limiterData = new ServerLimiterData();

        INSTANCE = this;
    }


    public AbstractLimiterData.LimiteEntry getEntry(UUID uuid){
        return getEntry(uuid, true);
    }

    public AbstractLimiterData.LimiteEntry getEntry(UUID uuid, boolean isGlobal){
        AbstractLimiterData.LimiteEntry limiteEntry = limiterData.ENTIES_MAP.get(uuid);
        if(limiteEntry == null) {
            limiterData.addEntry(new AbstractLimiterData.LimiteEntry(uuid, isGlobal));
        }

        return limiteEntry;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("limiterData", limiterData.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        limiterData.deserializeNBT(nbt.getCompound("limiterData"));
    }



    public void saveOnFile(){
        File file = server.getWorldPath(LevelResource.ROOT).resolve("shopData").toFile();
        if(!file.isFile()) file.mkdirs();

        file = file.toPath().resolve("shopData.snbt").toFile();
        SNBT.write(file.toPath(), serializeNBT());
    }

    public void loadFromFile(){
        File file = server.getWorldPath(LevelResource.ROOT).resolve("shopData").toFile();


        Tag nbt = SNBT.read(file.toPath().resolve("shopData.snbt"));
        if(nbt != null){
            deserializeNBT((CompoundTag) nbt);
        } else {
            saveOnFile();
        }
    }

    public void syncDataWithClient(){
        new SyncShopGlobalData(serializeNBT()).sendToAll(server);
    }

    public void syncDataWithClient(ServerPlayer player){
        new SyncShopGlobalData(serializeNBT()).sendTo(player);
    }
}
