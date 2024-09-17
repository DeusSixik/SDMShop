package net.sdm.sdmshoprework.common;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sdm.sdmshoprework.SDMShopPaths;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.network.client.SendEditModeS2C;

public class ModEvents {


    @SubscribeEvent
    public void onServerLoad(ServerStartingEvent event) {
        CompoundTag nbt = SNBT.read(SDMShopPaths.getFile());
        if(nbt != null) {
            ShopBase.SERVER = new ShopBase();
            ShopBase.SERVER.deserializeNBT(nbt);
            ShopBase.SERVER.saveShopToFile();
        } else {
            ShopBase.SERVER = new ShopBase();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.getEntity().isLocalPlayer() || !event.getEntity().level().isClientSide){
            ShopBase.SERVER.syncShop((ServerPlayer) event.getEntity());
            new SendEditModeS2C(SDMShopR.isEditMode(event.getEntity())).sendTo((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event){
        if(ShopBase.SERVER != null){
            ShopBase.SERVER.saveShopToFile();
        }
    }
}
