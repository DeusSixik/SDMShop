package net.sixik.sdmshoprework.economy;

import com.spawnchunk.auctionhouse.AuctionHouse;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdmshoprework.SDMShopRework;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


public class EconomyManager {

    public static EconomyModule economy;

    public static void init() {
        SDMShopRework.LOGGER.info("Try to load economy from mods!");

        economy = new EconomyModule(
                (player) -> {},
                (player, aLong) -> CurrencyHelper.setMoney(player, "basic_money", aLong),
                (player) -> CurrencyHelper.getMoney(player, "basic_money")
        );

    }

    private static void initPlugins() {
        try {
            Class.forName("org.bukkit.Bukkit");
            Class.forName("dev.architectury.event.forge.EventHandlerImplCommon");
            Class.forName("org.bukkit.OfflinePlayer");
        } catch (ClassNotFoundException e) {
            return;
        }

        try {
            economy = new EconomyModule(
                    (player) -> {
                        try {
                            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                            Object bukkitInstance = bukkitClass.getMethod("getOnlinePlayers").invoke(null);


                            Optional<?> optional = Arrays.stream((Object[]) bukkitInstance)
                                    .filter(s -> {
                                        try {
                                            return s.getClass().getMethod("getUniqueId").invoke(s).equals(player.getUUID());
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    })
                                    .findFirst();

                            if (optional.isPresent()) {
                                Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
                                Method getBalance = economyClass.getMethod("getBalance", OfflinePlayer.class);
                                long balance = Double.doubleToLongBits((double) getBalance.invoke(optional.get()));
                                CurrencyHelper.setMoney(player, "basic_money", balance);
                            }
                        } catch (Exception e) {
                            SDMShopRework.LOGGER.warn("Failed to handle economy: " + e.getMessage());
                        }
                    },
                    (player, money) -> {
                        try {
                            // Обработка set
                            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                            Object bukkitInstance = bukkitClass.getMethod("getOnlinePlayers").invoke(null);

                            Optional<?> optional = Arrays.stream((Object[]) bukkitInstance)
                                    .filter(s -> {
                                        try {
                                            return s.getClass().getMethod("getUniqueId").invoke(s).equals(player.getUUID());
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    })
                                    .findFirst();

                            if (optional.isPresent()) {
                                Object bukkitPlayer = optional.get();
                                Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
                                Method depositPlayer = economyClass.getMethod("depositPlayer", OfflinePlayer.class, double.class);
                                depositPlayer.invoke(AuctionHouse.econ, bukkitPlayer, Double.longBitsToDouble(money));
                                economy.sync.accept(player); // Синхронизировать после депозита
                            }
                        } catch (Exception e) {
                            SDMShopRework.LOGGER.warn("Failed to handle set: " + e.getMessage());
                        }
                    },
                    (player) -> {
                        try {
                            // Обработка get
                            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                            Object bukkitInstance = bukkitClass.getMethod("getOnlinePlayers").invoke(null);

                            Optional<?> optional = Arrays.stream((Object[]) bukkitInstance)
                                    .filter(s -> {
                                        try {
                                            return s.getClass().getMethod("getUniqueId").invoke(s).equals(player.getUUID());
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    })
                                    .findFirst();

                            if (optional.isPresent()) {
                                Object bukkitPlayer = optional.get();
                                Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
                                Method getBalance = economyClass.getMethod("getBalance", OfflinePlayer.class);
                                return Double.doubleToLongBits((double) getBalance.invoke(AuctionHouse.econ, bukkitPlayer));
                            }
                        } catch (Exception e) {
                            SDMShopRework.LOGGER.warn("Failed to handle get: " + e.getMessage());
                        }
                        return 0L;
                    }
            );

        } catch (Exception ignored) {}
    }


    public record EconomyModule(Consumer<net.minecraft.world.entity.player.Player> sync, BiConsumer<net.minecraft.world.entity.player.Player, Long> set,
                                Function<net.minecraft.world.entity.player.Player, Long> get) {
    }
}
