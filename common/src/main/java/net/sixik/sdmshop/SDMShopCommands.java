package net.sixik.sdmshop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.network.ASK.SyncAndOpenShopASK;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.Collection;

public class SDMShopCommands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sdmshop")
                .then(Commands.literal("balance")
                    .executes(context -> balance(context.getSource(), context.getSource().getPlayerOrException()))
                    .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> balance(context.getSource(), context.getSource().getPlayerOrException()))
                    )
                )
                .then(Commands.literal("pay")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("money", DoubleArgumentType.doubleArg(1L))
                            .executes(context -> pay(context.getSource(), context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "money")))
                        )
                    )
                )
                .then(Commands.literal("set")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("money", DoubleArgumentType.doubleArg(0L))
                                .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "player"), DoubleArgumentType.getDouble(context, "money")))
                        )
                    )
                )
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.players())
                            .then(Commands.argument("money", DoubleArgumentType.doubleArg())
                                .executes(context -> add(context.getSource(), EntityArgument.getPlayers(context, "player"), DoubleArgumentType.getDouble(context, "money")))
                            )
                        )
                )
                .then(Commands.literal("edit_mode")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> editMode(context.getSource()))
                )
                .then(Commands.literal("create_shop")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("id", StringArgumentType.string())
                            .executes(context -> createShop(context.getSource(), StringArgumentType.getString(context, "id")))
                        )
                )
                .then(Commands.literal("delete_shop")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("shop_id", StringArgumentType.greedyString()).suggests(((commandContext, builder) -> SharedSuggestionProvider.suggest(SDMShopServer.Instance().getAllShopIDs().stream(), builder)))
                            .executes(context -> removeShop(context.getSource(), StringArgumentType.getString(context, "shop_id")))
                    )
                )

//                .then(Commands.literal("reloadConfig")
//                        .requires(source -> source.hasPermission(2))
//                        .executes(context -> reloadClient(context.getSource()))
//                )
                        .then(Commands.literal("open_shop")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> openShop(context.getSource(), StringArgumentType.getString(context, "shop_id"), null))
                                .then(Commands.argument("shop_id", StringArgumentType.greedyString()).suggests(((commandContext, builder) -> SharedSuggestionProvider.suggest(SDMShopServer.Instance().getAllShopIDs().stream(), builder)))
                                        .executes(context -> openShop(context.getSource(), StringArgumentType.getString(context, "shop_id"), EntityArgument.getPlayers(context, "player")))
                                )
                        )
                    )
        );
    }


    private static int openShop(CommandSourceStack source, String shopId, Collection<ServerPlayer> profiles) throws CommandSyntaxException {
        if(!SDMShopServer.Instance().exists(shopId)) {
            throw new CommandSyntaxException(
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(),
                    new LiteralMessage("Shop with ID " + shopId + " does not exist")
            );
        }

        if (profiles != null) {
            for(ServerPlayer profile : profiles) {
                new SyncAndOpenShopASK(null).startRequest(profile,
                        SDMShopServer.Instance().getShop(SDMShopServer.fromString(shopId)).get().getId()
                );
//                (new SendOpenShopS2C(true)).sendTo(profile);
            }
        } else if (source.getPlayer() != null) {
            new SyncAndOpenShopASK(null).startRequest(source.getPlayer(),
                    SDMShopServer.Instance().getShop(SDMShopServer.fromString(shopId)).get().getId()
            );

//            (new SendOpenShopS2C(true)).sendTo(source.getPlayer());
        }

        return 1;
    }
//    private static int reloadClient(CommandSourceStack source) {
//        if (source.getPlayer() != null) {
//            source.sendSuccess(() -> Component.literal("Start Reload"), false);
//            (new SendConfigS2C()).sendToAll(source.m_81377_());
//            (new SendReloadConfigS2C()).sendToAll(source.m_81377_());
//        }
//
//        return 1;
//    }

    private static int createShop(CommandSourceStack source, String shopId) {
        if(source.getPlayer() == null) return 0;

        if(SDMShopServer.Instance().exists(shopId)) {
            source.sendFailure(Component.literal("Shop ").append(shopId).append(" already exists"));
            return 0;
        }

        SDMShopServer.Instance().createShop(shopId);
        source.sendSuccess(() -> Component.literal("Shop ").append(Component.literal(shopId).withStyle(ChatFormatting.GOLD)).append(" created!"), false);
        return 1;
    }

    private static int removeShop(CommandSourceStack source, String shopId) {
        if(source.getPlayer() == null) return 0;

        if(SDMShopServer.Instance().removeShop(shopId)) {
            source.sendSuccess(() -> Component.literal("Shop ").append(Component.literal(shopId).withStyle(ChatFormatting.GOLD)).append(" removed!"), false);
            return 1;
        }

        return 0;
    }

    private static int editMode(CommandSourceStack source) {
        if (source.getPlayer() != null) {
            ShopUtils.changeEditMode(source.getPlayer(), !ShopUtils.isEditMode(source.getPlayer()));
            source.sendSuccess(() -> Component.literal("Edit mode is " + ShopUtils.isEditMode(source.getPlayer())), false);
        }

        return 1;
    }

    private static int balance(CommandSourceStack source, ServerPlayer profiles) {
        source.sendSuccess(() -> Component.literal(ShopUtils.moneyToString(profiles)), false);
        return 1;
    }

    private static int pay(CommandSourceStack source, ServerPlayer from, ServerPlayer to, double money) {
        if (from.getUUID().equals(to.getUUID())) {
            source.sendFailure(Component.literal("You can't send money to yourself"));
            return 1;
        } else if (ShopUtils.getMoney(from) >= money) {
            ShopUtils.setMoney(from, ShopUtils.getMoney(from) - money);
            ShopUtils.setMoney(to, ShopUtils.getMoney(to) + money);
            source.sendSuccess(() -> Component.literal("Money sended !"), false);
            return 0;
        } else {
            source.sendFailure(Component.literal("Not enough money"));
            return 1;
        }
    }

    private static int set(CommandSourceStack source, Collection<ServerPlayer> players, double money) {
        for(ServerPlayer player : players) {
            ShopUtils.setMoney(player, money);
            source.sendSuccess(() -> Component.literal(player.getScoreboardName() + ": ").append(ShopUtils.moneyToString(player)), false);
        }

        return players.size();
    }

    private static int add(CommandSourceStack source, Collection<ServerPlayer> players, double money) {
        if (money == 0L) {
            return 0;
        } else {
            for(ServerPlayer player : players) {
                source.sendSuccess(() -> {
                    String var10000 = player.getScoreboardName();
                    return Component.literal(var10000 + (money > 0L ? ": +" : ": -")).append(ShopUtils.moneyToString(Math.abs(money), SDMCoin.getId()));
                }, false);
                ShopUtils.addMoney(player, money);
            }

            return players.size();
        }
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        registerCommands(commandSourceStackCommandDispatcher);
    }
}
