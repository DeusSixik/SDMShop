package net.sixk.sdmshop.shop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;
import net.sixk.sdmshop.SDMShop;

import java.util.Collection;
import java.util.Objects;

public class ShopComands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sdmshop")

                .then(Commands.literal("edit_mode")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> editMode(context.getSource()))
                )
                .then(Commands.literal("pay")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("currency", StringArgumentType.string()).suggests((context, builder) -> {
                                            for (String key : CurrencyHelper.getAllCurrencyKeys()) {
                                                builder.suggest(key);
                                            }
                                            return builder.buildFuture();
                                        })
                                    .then(Commands.argument("money", LongArgumentType.longArg(1L))
                                            .executes(context -> pay(context, context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "money")))
                                    )
                            )
                        )

                )
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("currency", StringArgumentType.string()).suggests((context, builder) -> {
                                            for (String key : CurrencyHelper.getAllCurrencyKeys()) {
                                                builder.suggest(key);
                                            }
                                            return builder.buildFuture();
                                        })
                                    .then(Commands.argument("money", LongArgumentType.longArg(0L))
                                            .executes(context -> set(context, EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
                                    )
                                )
                        )
                )
        );
    }




    private static int editMode(CommandSourceStack source){
        if(source.getPlayer() != null) {
            SDMShop.setEditMode(source.getPlayer(), !SDMShop.isEditMode(source.getPlayer()));
            source.sendSuccess(() -> Component.literal("Edit mode is " + SDMShop.isEditMode(source.getPlayer())), false);
        }
        return 1;
    }

    private static int pay(CommandContext<CommandSourceStack> context, ServerPlayer from, ServerPlayer to, long money) {

        String currency = StringArgumentType.getString(context,"currency");
        CommandSourceStack source = context.getSource();
        if (from.getUUID().equals(to.getUUID())) {
            source.sendFailure(Component.literal("You can't send money to yourself"));
            return 1;
        }
        if(getMoney(from,currency) >= money){
            setMoney((ServerPlayer) from, currency ,getMoney(from,currency) - money);
            setMoney((ServerPlayer) to, currency,getMoney(to,currency) + money);
            source.sendSuccess(() -> Component.literal("Money sended !"), false);
            return 0;
        }
        source.sendFailure(Component.literal("Not enough money"));
        return 1;
    };

    private static int set(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, long money) {

        String currency = StringArgumentType.getString(context,"currency");
        CommandSourceStack source = context.getSource();
        String specialSing = " ";
        for (ServerPlayer player : players) {
            for (AbstractCurrency c : CurrencyHelper.getPlayerData(player).currencies) {
               if (Objects.equals(c.getID(), currency)) {
                   specialSing = c.specialSymbol;
                   break;
               }
            }
            setMoney(player, currency, money);
            String finalSpecialSing = specialSing;
            source.sendSuccess(() -> Component.literal(player.getScoreboardName() + ": ").append(Component.literal(finalSpecialSing + " ").append(String.valueOf(getMoney(player,currency)))), false);
        }

        return players.size();
    }

    public static void setMoney(Player player, String currency, long money) {
        CurrencyHelper.setMoney(player, currency, money);
    }

    public static long getMoney(Player player,String currency) {
        return CurrencyHelper.getMoney(player, currency);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        registerCommands(commandSourceStackCommandDispatcher);
    }
}
