package net.sixik.sdmshoprework.common;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.common.data.LimiterData;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.economy.EconomyManager;
import net.sixik.sdmshoprework.network.client.SendEditModeS2C;
import net.sixik.sdmshoprework.network.client.SendEntryLimitS2C;
import net.sixik.sdmshoprework.network.server.misc.SendConfigS2C;

public class ModEvents {


    public static void init(){
        LifecycleEvent.SERVER_STARTED.register(ModEvents::onServerStart);
        LifecycleEvent.SERVER_STOPPED.register(ModEvents::onServerStopped);
        PlayerEvent.PLAYER_JOIN.register(ModEvents::onPlayerLoggedIn);
    }

    public static void onServerStart(MinecraftServer server){
        EconomyManager.init();

        Config.loadConfig(false);

        if(SDMShopPaths.getFile().toFile().exists()) {
            CompoundTag nbt = SNBT.read(SDMShopPaths.getFile());
            if (nbt != null) {
                ShopBase.SERVER = new ShopBase();
                ShopBase.SERVER.deserializeNBT(nbt);
                ShopBase.SERVER.saveShopToFile();
            } else {
                ShopBase.SERVER = new ShopBase();
            }
        }

        LimiterData.SERVER = new LimiterData();
        LimiterData.SERVER.load(server);
    }

    public static void onServerStopped(MinecraftServer server){
        if(ShopBase.SERVER != null){
            ShopBase.SERVER.saveShopToFile();
        }

        if(LimiterData.SERVER != null){
            LimiterData.SERVER.save(server);
        }
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        if(!player.isLocalPlayer() || !player.level().isClientSide){
            ShopBase.SERVER.syncShop((ServerPlayer) player);
            new SendEditModeS2C(SDMShopR.isEditMode(player)).sendTo((ServerPlayer) player);
            new SendConfigS2C().sendTo(player);
            new SendEntryLimitS2C(LimiterData.SERVER.serializeClient(player.getGameProfile().getId())).sendTo(player);
        }
    }
}
