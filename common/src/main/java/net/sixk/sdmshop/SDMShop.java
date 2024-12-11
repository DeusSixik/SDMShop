package net.sixk.sdmshop;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdm_economy.api.ICustomData;
import net.sixik.sdmcore.impl.utils.serializer.DataIO;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixk.sdmshop.shop.ShopComands;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.ModNetwork;
import net.sixk.sdmshop.shop.network.server.SendEditModeS2C;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

public class SDMShop {

    public static final String MODID = "sdmshop";

    private static boolean isSerialize;

    public static void init(){

        ModNetwork.init();

        event();

        CommandRegistrationEvent.EVENT.register(ShopComands::registerCommands);

        EnvExecutor.runInEnv(Env.CLIENT,() ->SDMShopClient::init);
    }

    public static void event(){

        LifecycleEvent.SERVER_STARTED.register((server) -> {
            TovarTab.SERVER = new TovarTab();
            TovarList.SERVER = new TovarList();
            isSerialize = false;

            IData w1 = DataIO.read(server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toString());

            if (w1 != null) TovarTab.SERVER.deserialize(w1.asKeyMap());
        });

        LifecycleEvent.SERVER_STOPPED.register((server) -> {
            if( !server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().exists()) {

                server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().mkdir();
            }
            if(TovarTab.SERVER != null) {
                DataIO.write(TovarTab.SERVER.serialize(),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toString());
            }
            if(TovarList.SERVER != null) {
                DataIO.write(TovarList.SERVER.serialize(server.registryAccess()),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarList.sdm").toString());
            }
        });

        PlayerEvent.PLAYER_JOIN.register((serverPlayer) -> {
            IData w2 = DataIO.read(serverPlayer.getServer().getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarList.sdm").toString());
            if (w2 != null && !isSerialize) {
                isSerialize = true;
                TovarList.SERVER.deserialize(w2.asKeyMap(), serverPlayer.getServer().registryAccess());
            }

            NetworkManager.sendToPlayer((ServerPlayer) serverPlayer,new SendShopDataS2C(TovarList.SERVER.serialize(serverPlayer.registryAccess()).asNBT(),TovarTab.SERVER.serialize().asNBT()));
            NetworkManager.sendToPlayer((ServerPlayer) serverPlayer, new SendEditModeS2C(isEditMode(serverPlayer)));
        });

    }

    public static void saveData(MinecraftServer server){

        if( !server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().exists()) {

            server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().mkdir();
        }
        if(TovarTab.SERVER != null) {
            DataIO.write(TovarTab.SERVER.serialize(),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toString());
        }

        if(TovarList.SERVER != null) {
            DataIO.write(TovarList.SERVER.serialize(server.registryAccess()),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarList.sdm").toString());
        }
    }

    public static boolean isEditMode(Player player){
        if(((ICustomData) player).sdm$getCustomData().contains("edit_mode"))
            return ((ICustomData) player).sdm$getCustomData().getBoolean("edit_mode");
        else {
            ((ICustomData) player).sdm$getCustomData().putBoolean("edit_mode", false);
            return false;
        }
    }

    public static boolean isEditMode(){
        if(((ICustomData) Minecraft.getInstance().player).sdm$getCustomData().contains("edit_mode"))
            return ((ICustomData) Minecraft.getInstance().player).sdm$getCustomData().getBoolean("edit_mode");
        else {
            return false;
        }


    }

    public static void setEditMode(ServerPlayer player, boolean value){
        ((ICustomData) player).sdm$getCustomData().putBoolean("edit_mode", value);

        NetworkManager.sendToPlayer(player, new SendEditModeS2C(value));
    }
}
