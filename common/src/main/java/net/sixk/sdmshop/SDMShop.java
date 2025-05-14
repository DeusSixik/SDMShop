package net.sixk.sdmshop;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdmcore.impl.utils.serializer.DataIO;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmeconomy.api.CustomCurrencies;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixk.sdmshop.data.config.Config;
import net.sixk.sdmshop.shop.ShopComands;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarCommand;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarItem;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarTypeRegister;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarXP;
import net.sixk.sdmshop.shop.network.ModNetwork;
import net.sixk.sdmshop.shop.network.server.SendEditModeS2C;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

public class SDMShop {

    public static final String MODID = "sdmshop";

    private static boolean isSerialize;

    public static void init(){
       Config.init();
       ModNetwork.init();
       event();

       CommandRegistrationEvent.EVENT.register(ShopComands::registerCommands);
       CustomCurrencies.CURRENCIES.put("sdmcoin", () -> new Currency("sdmcoin").canDelete(false));
       EnvExecutor.runInEnv(Env.CLIENT,() ->SDMShopClient::init);
       TovarTypeRegister.registerTovar(new TovarItem.Constructor());
       TovarTypeRegister.registerTovar(new TovarXP.Constructor());
       TovarTypeRegister.registerTovar(new TovarCommand.Constructor());
    }

    public static void event(){

        LifecycleEvent.SERVER_STARTED.register((server) -> {
            TovarTab.SERVER = new TovarTab();
            TovarList.SERVER = new TovarList();
            isSerialize = false;

            if(!server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toFile().exists()) saveData(server);

            IData w1 = DataIO.read(server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toString());

            if (w1 != null) TovarTab.SERVER.deserialize(w1.asKeyMap(),server.registryAccess());
        });

        LifecycleEvent.SERVER_STOPPED.register((server) -> {
            if( !server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().exists()) {

                server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().mkdir();
            }
            if(TovarTab.SERVER != null) {
                DataIO.write(TovarTab.SERVER.serialize(server.registryAccess()),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toString());
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



            NetworkManager.sendToPlayer((ServerPlayer) serverPlayer,new SendShopDataS2C(TovarList.SERVER.serialize(serverPlayer.registryAccess()).asNBT(),TovarTab.SERVER.serialize(serverPlayer.registryAccess()).asNBT()));
            NetworkManager.sendToPlayer((ServerPlayer) serverPlayer, new SendEditModeS2C(isEditMode(serverPlayer)));
        });

    }

    public static void saveData(MinecraftServer server){
        if( !server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().exists()) {

            server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().mkdir();
        }
        if(TovarTab.SERVER != null) {
            DataIO.write(TovarTab.SERVER.serialize(server.registryAccess()),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toString());
        }

        if(TovarList.SERVER != null) {
            DataIO.write(TovarList.SERVER.serialize(server.registryAccess()),server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarList.sdm").toString());
        }
    }

    public static boolean isEditMode(Player player){
        CompoundTag nbt = EconomyAPI.getCustomData(player);
        if(nbt.contains("edit_mode"))
            return (nbt.getBoolean("edit_mode"));
        else {
            nbt.putBoolean("edit_mode", false);
            return false;
        }
    }

    public static boolean isEditMode(){
       CompoundTag nbt = EconomyAPI.getCustomData(SDMShopClient.getPlayer());
       if(nbt.contains("edit_mode"))
           return nbt.getBoolean("edit_mode");
       else {
           return false;
       }
   }

    public static void setEditMode(ServerPlayer player, boolean value){
        EconomyAPI.updateCustomData(player,s ->{
            s.putBoolean("edit_mode", value);
        });
    }
}
