package net.sixk.sdmshop;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
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
import net.sixk.sdmshop.shop.currency.SDMCoin;
import net.sixk.sdmshop.shop.network.ModNetwork;
import net.sixk.sdmshop.shop.network.server.SendEditModeS2C;
import net.sixk.sdmshop.utils.ShopNetworkUtils;
import org.slf4j.Logger;

import java.io.IOException;

public class SDMShop {
    public static final String MODID = "sdmshop";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static boolean isSerialize;

    public SDMShop() {
    }

    public static void init() {
        Config.init();
        ModNetwork.init();
        modEvents();
        CommandRegistrationEvent.EVENT.register(ShopComands::registerCommands);
        CustomCurrencies.CURRENCIES.put("sdmcoin", SDMCoin::new);
        EnvExecutor.runInEnv(Env.CLIENT, () -> SDMShopClient::init);

        TovarTypeRegister.register("ItemType",TovarItem::new);
        TovarTypeRegister.register("XPType",TovarXP::new);
        TovarTypeRegister.register("CommandType",TovarCommand::new);
    }

    public static void modEvents() {
        LifecycleEvent.SERVER_STARTED.register((server) -> {
            TovarTab.SERVER = new TovarTab();
            TovarList.SERVER = new TovarList();
            isSerialize = false;
            if (!server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm").toFile().exists()) {
                saveData(server);
            }

            try {
                CompoundTag w1 = NbtIo.read(server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm"));
                if (w1 != null) {
                    TovarTab.SERVER.deserializeNBT(w1, server.registryAccess());
                }
            } catch (IOException e) {
                LOGGER.error(e.toString());
            }

        });
        LifecycleEvent.SERVER_STOPPED.register(SDMShop::saveData);
        PlayerEvent.PLAYER_JOIN.register((serverPlayer) -> {

            try {
                CompoundTag w2 = NbtIo.read(serverPlayer.getServer().getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarList.sdm"));
                if (w2 != null && !isSerialize) {
                    isSerialize = true;
                    TovarList.SERVER.deserializeNBT(w2, serverPlayer.getServer().registryAccess());
                }

                ShopNetworkUtils.sendShopDataS2C(serverPlayer, serverPlayer.registryAccess());
                NetworkManager.sendToPlayer(serverPlayer, new SendEditModeS2C(isEditMode(serverPlayer)));
            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
        });
    }

    public static void saveData(MinecraftServer server) {
        if (!server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().exists()) {
            server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").toFile().mkdir();
        }

        if (TovarTab.SERVER != null) {
            try {
                NbtIo.write(TovarTab.SERVER.serializeNBT(server.registryAccess()), server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarTab.sdm"));
            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
        }

        if (TovarList.SERVER != null) {
            try {
                NbtIo.write(TovarList.SERVER.serializeNBT(server.registryAccess()), server.getWorldPath(LevelResource.ROOT).resolve("SDMShopData").resolve("SDMTovarList.sdm"));
            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
        }

    }

    public static boolean isEditMode(Player player) {
        CompoundTag nbt = EconomyAPI.getCustomData(player);
        if (nbt.contains("edit_mode")) {
            System.out.println("1:" + nbt.getBoolean("edit_mode"));
            return nbt.getBoolean("edit_mode");
        } else {
            nbt.putBoolean("edit_mode", false);
            return false;
        }

    }

    public static boolean isEditMode() {
        CompoundTag nbt = EconomyAPI.getCustomData(SDMShopClient.getPlayer());
        return nbt.contains("edit_mode") && nbt.getBoolean("edit_mode");
    }

    public static void setEditMode(ServerPlayer player, boolean value) {
        EconomyAPI.updateCustomData(player, (s) -> s.putBoolean("edit_mode", value));
    }
}
