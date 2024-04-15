package net.sdm.sdmshopr;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.sdm.sdmshopr.network.SyncShopData;
import net.sdm.sdmshopr.shop.ShopData;

import java.util.Collection;
import java.util.Collections;

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
                                .then(Commands.argument("money", LongArgumentType.longArg(1L))
                                        .executes(context -> pay(context.getSource(), context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "money")))
                                )
                        )
                )
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("money", LongArgumentType.longArg(0L))
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
                                )
                        )
                )
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("money", LongArgumentType.longArg())
                                        .executes(context -> add(context.getSource(), EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
                                )
                        )
                )
                .then(Commands.literal("edit_mode")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> editMode(context.getSource()))
                )
        );
    }

    private static int editMode(CommandSourceStack source){
        if(source.getPlayer() != null) {
            SDMShopR.setEditMode(source.getPlayer(), !SDMShopR.isEditMode(source.getPlayer()));
            source.sendSuccess(() -> Component.literal("Edit mode is " + SDMShopR.isEditMode(source.getPlayer())), false);
        }
        return 1;
    }

    private static int balance(CommandSourceStack source, ServerPlayer profiles) {
        source.sendSuccess(() -> Component.literal(SDMShopR.moneyString(SDMShopR.getMoney(profiles))), false);
        return 1;
    }


    private static int pay(CommandSourceStack source, ServerPlayer from, ServerPlayer to, long money) {
        if(from.getUUID().equals(to.getUUID())) {
            source.sendFailure(Component.literal("Not enough money"));
            return 1;
        }
        if(SDMShopR.getMoney(from) >= money){
            SDMShopR.setMoney(from, SDMShopR.getMoney(from) - money);
            SDMShopR.setMoney(to, SDMShopR.getMoney(to) + money);
            source.sendFailure(Component.literal("Money sended !"));
            return 0;
        }
        source.sendFailure(Component.literal("Not enough money"));
        return 1;
    }

    private static int set(CommandSourceStack source, Collection<ServerPlayer> players, long money) {
        for (ServerPlayer player : players) {
            source.sendSuccess(() -> Component.literal(player.getScoreboardName() + ": ").append(SDMShopR.moneyString(money)), false);
            SDMShopR.setMoney(player, money);
        }

        return players.size();
    }

    private static int add(CommandSourceStack source, Collection<ServerPlayer> players, long money) {
        if (money == 0L) {
            return 0;
        }

        for (ServerPlayer player : players) {
            source.sendSuccess(() -> Component.literal(player.getScoreboardName() + (money > 0L ? ": +" : ": -")).append(SDMShopR.moneyString(Math.abs(money))), false);
            SDMShopR.addMoney(player, money);
        }

        return players.size();
    }

}
