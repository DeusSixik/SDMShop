package net.sixik.sdmshoprework.common;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.common.config.ConfigFile;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.network.client.SendEditModeS2C;
import net.sixik.sdmshoprework.network.server.misc.SendConfigS2C;

public class ModEvents {


    public static void init(){
        LifecycleEvent.SERVER_STARTED.register(ModEvents::onServerStart);
        LifecycleEvent.SERVER_STOPPED.register(ModEvents::onServerStopped);
        PlayerEvent.PLAYER_JOIN.register(ModEvents::onPlayerLoggedIn);
    }

    public static void onServerStart(MinecraftServer server){
        Config.loadConfig(false);
        CompoundTag nbt = SNBT.read(SDMShopPaths.getFile());
        if(nbt != null) {
            ShopBase.SERVER = new ShopBase();
            ShopBase.SERVER.deserializeNBT(nbt);
            ShopBase.SERVER.saveShopToFile();
        } else {
            ShopBase.SERVER = new ShopBase();
        }
    }

    public static void onServerStopped(MinecraftServer server){
        if(ShopBase.SERVER != null){
            ShopBase.SERVER.saveShopToFile();
        }
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        if(!player.isLocalPlayer() || !player.level().isClientSide){
            ShopBase.SERVER.syncShop((ServerPlayer) player);
            new SendEditModeS2C(SDMShopR.isEditMode(player)).sendTo((ServerPlayer) player);
            new SendConfigS2C().sendTo(player);
        }
    }
}
