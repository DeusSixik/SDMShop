package net.sixk.sdmshop.shop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.data.config.Config;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.server.SendConfigS2C;
import net.sixk.sdmshop.shop.network.server.SendOpenShopScreenS2C;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

import java.util.Collection;
import java.util.Optional;

public class ShopComands {
    public ShopComands() {
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sdmshop")
            .then(Commands.literal("edit_mode")
                .requires(source -> source.hasPermission(2))
                .executes(context -> editMode(context.getSource()))
            )
            .then(Commands.literal("reloadConfig")
                .requires(source -> source.hasPermission(2))
                .executes(context -> reloadClient(context.getSource()))
            )
            .then(Commands.literal("pay")
                .then(Commands.argument("player", EntityArgument.player())
                )
                .then(Commands.argument("currency", StringArgumentType.string()).suggests((context, builder) -> {
                    for (String key : (EconomyAPI.getAllCurrency().value).currencies.stream().map(Currency::getName).toList()) {
                    builder.suggest(key);
                    }
                    return builder.buildFuture();
                    })
                )
                .then(Commands.argument("money", LongArgumentType.longArg(1L)))
                    .executes(context -> pay(context, (context.getSource()).getPlayerOrException(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "money")))
            )
            .then(Commands.literal("add")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("currency", StringArgumentType.string()).suggests((context, builder) -> {
                           for (String key : (EconomyAPI.getAllCurrency().value).currencies.stream().map(Currency::getName).toList()) {
                               builder.suggest(key);
                           }
                           return builder.buildFuture();
                        }))
                    )
                    .then(Commands.argument("money", LongArgumentType.longArg()))
                        .executes((context) -> add(context, EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
            )
            .then(Commands.literal("set")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.players()))
                        .then(Commands.argument("currency", StringArgumentType.string()).suggests((context, builder) -> {
                               for (String key : (EconomyAPI.getAllCurrency().value).currencies.stream().map(Currency::getName).toList()) {
                                   builder.suggest(key);
                               }
                               return builder.buildFuture();
                            })
                        )
                            .then(Commands.argument("money", LongArgumentType.longArg(0L)))
                                .executes((context) -> set(context, EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
            )
            .then(Commands.literal("open_shop")
                .requires(source -> source.hasPermission(2))
                .executes(context -> openShop(context.getSource(),null))
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> openShop(context.getSource(), EntityArgument.getPlayers(context, "player")))
                )
            )
        );
    }

    private static int openShop(CommandSourceStack source, Collection<ServerPlayer> profiles) {
        if (profiles != null) {

            for (ServerPlayer profile : profiles) {
                NetworkManager.sendToPlayer(profile, new SendOpenShopScreenS2C(true));
            }
        } else if (source.getPlayer() != null) {
            NetworkManager.sendToPlayer(source.getPlayer(), new SendOpenShopScreenS2C(true));
        }

        return 1;
    }

    private static int editMode(CommandSourceStack source) {
        if (source.getPlayer() != null) {
            SDMShop.setEditMode(source.getPlayer(), !SDMShop.isEditMode(source.getPlayer()));
            source.sendSuccess(() -> Component.literal("Edit mode is " + SDMShop.isEditMode(source.getPlayer())), false);
        }

        return 1;
    }

    private static int reloadClient(CommandSourceStack source) {
        if (source.getPlayer() != null) {
            source.sendSuccess(() -> Component.literal("Start Reload"), false);
            Config.loadConfig(false);
            NetworkManager.sendToPlayer(source.getPlayer(), new SendConfigS2C(true));
        }

        return 1;
    }

    private static int pay(CommandContext<CommandSourceStack> context, ServerPlayer from, ServerPlayer to, long money) {
        String currency = StringArgumentType.getString(context, "currency");
        CommandSourceStack source = context.getSource();
        if (from.getUUID().equals(to.getUUID())) {
            source.sendFailure(Component.literal("You can't send money to yourself"));
            return 1;
        } else if (getMoney(from, currency) >= money) {
            setMoney(from, currency, getMoney(from, currency) - money);
            setMoney(to, currency, getMoney(to, currency) + money);
            source.sendSuccess(() -> Component.literal("Money sended !"), false);
            return 0;
        } else {
            source.sendFailure(Component.literal("Not enough money"));
            return 1;
        }
    }

    private static int add(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, long money) {
        if (money == 0L) {
            return 0;
        } else {
            String currency = StringArgumentType.getString(context, "currency");

            for (ServerPlayer player : players) {
                EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, currency, (double) money);
                EconomyAPI.syncPlayer(player);
                NetworkManager.sendToPlayer(player, new SendShopDataS2C(TovarList.SERVER.serialize(Minecraft.getInstance().level.registryAccess()).asNBT(), TovarTab.SERVER.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                (context.getSource()).sendSuccess(() -> {
                    MutableComponent var10000 = Component.literal(player.getScoreboardName() + ": ");
                    Optional var10001 = EconomyAPI.getPlayerCurrencyServerData().getPlayerCurrency(player, currency);
                    return var10000.append(Component.literal(((CurrencyPlayerData.PlayerCurrency) var10001.get()).currency.symbol.value + " ").append(String.valueOf(getMoney(player, currency))));
                }, false);
            }

            return players.size();
        }
    }

    private static int set(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, long money) {
        String currency = StringArgumentType.getString(context, "currency");

        for (ServerPlayer player : players) {
            setMoney(player, currency, money);
            (context.getSource()).sendSuccess(() -> {
                MutableComponent var10000 = Component.literal(player.getScoreboardName() + ": ");
                Optional var10001 = EconomyAPI.getPlayerCurrencyServerData().getPlayerCurrency(player, currency);
                return var10000.append(Component.literal(((CurrencyPlayerData.PlayerCurrency) var10001.get()).currency.symbol.value + " ").append(String.valueOf(getMoney(player, currency))));
            }, false);
        }

        return players.size();
    }

    public static void setMoney(Player player, String currency, long money) {
        EconomyAPI.getPlayerCurrencyServerData().setCurrencyValue(player, currency, (double) money);
        EconomyAPI.syncPlayer((ServerPlayer) player);
        NetworkManager.sendToPlayer((ServerPlayer) player, new SendShopDataS2C(TovarList.SERVER.serialize(Minecraft.getInstance().level.registryAccess()).asNBT(), TovarTab.SERVER.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
    }

    public static long getMoney(Player player, String currency) {
        return (EconomyAPI.getPlayerCurrencyServerData().getBalance(player, currency).value).longValue();
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        registerCommands(commandSourceStackCommandDispatcher);
    }
}
