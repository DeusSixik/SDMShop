package net.sdm.sdmshopr;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.sdm.sdmshopr.events.SDMPlayerEvents;
import net.sdm.sdmshopr.network.mainshop.ReloadClientData;

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
                .then(Commands.literal("reloadClient")
                        .requires(source -> source.hasPermission(1))
                        .executes(context -> reloadClient(context.getSource()))
                )

        );
    }

    private static int reloadClient(CommandSourceStack source){
        if(source.getPlayer() != null) {
            source.sendSuccess(() -> Component.literal("Start Reload Client"), false);
            new ReloadClientData().sendTo(source.getPlayer());
            source.sendSuccess(() -> Component.literal("End Reload Client"), false);
            return 0;
        }

        return 1;
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
            source.sendFailure(Component.literal("You can't send money to yourself"));
            return 1;
        }
        if(SDMShopR.getMoney(from) >= money){
            SDMPlayerEvents.PayEvent event = new SDMPlayerEvents.PayEvent(from, to, money);
            MinecraftForge.EVENT_BUS.post(event);

            if(!event.isCanceled()) {
                SDMShopR.setMoney((ServerPlayer) event.getEntity(), SDMShopR.getMoney(event.getEntity()) - event.getCountMoney());
                SDMShopR.setMoney((ServerPlayer) event.payablePlayer, SDMShopR.getMoney(event.payablePlayer) + event.getCountMoney());
                source.sendSuccess(() -> Component.literal("Money sended !"), false);
                return 0;
            }
            return 1;
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
