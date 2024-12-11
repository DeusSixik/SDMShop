//package net.sixik.sdmreskillable.common.commands;
//
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.IntegerArgumentType;
//import com.mojang.brigadier.context.CommandContext;
//import net.minecraft.commands.CommandBuildContext;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.commands.arguments.EntityArgument;
//import net.minecraft.commands.arguments.ResourceLocationArgument;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.sixik.sdmreskillable.api.SkillHelper;
//import net.sixik.sdmreskillable.common.capability.SkillModel;
//import net.sixik.sdmreskillable.common.register.SDMReskillableRegister;
//import net.sixik.sdmreskillable.common.skill.Skill;
//
//import java.util.Map;
//
//public class SDMReskillableCommands {
//
//    public static void registerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
//        registerCommands(commandSourceStackCommandDispatcher);
//    }
//
//    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("reskillable")
//                .then(Commands.literal("skill")
//                        .then(Commands.literal("level")
//                                .then(Commands.literal("set")
//                                        .then(Commands.argument("skillId", ResourceLocationArgument.id()).suggests((context, builder) -> {
//                                                            for (Map.Entry<ResourceLocation, Skill> entry : SDMReskillableRegister.getSkills().entrySet()) {
//                                                                builder.suggest(entry.getKey().toString());
//                                                            }
//                                                            return builder.buildFuture();
//                                                        })
//                                                        .then(Commands.argument("skillLevel", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
//                                                                .then(Commands.argument("player", EntityArgument.player())
//                                                                        .executes(SDMReskillableCommands::setSkillLevel)
//                                                                ))
//                                        )
//                                )
//                                .then(Commands.literal("get")
//                                        .then(Commands.argument("skillId", ResourceLocationArgument.id()).suggests((context, builder) -> {
//                                                    for (Map.Entry<ResourceLocation, Skill> entry : SDMReskillableRegister.getSkills().entrySet()) {
//                                                        builder.suggest(entry.getKey().toString());
//                                                    }
//                                                    return builder.buildFuture();
//                                                })
//                                                .then(Commands.argument("player", EntityArgument.player())
//                                                        .executes(SDMReskillableCommands::getSkillLevel)
//                                                ))
//                                )
//                                .then(Commands.literal("reset")
//                                        .then(Commands.argument("player", EntityArgument.player())
//                                                .executes(SDMReskillableCommands::resetSkills)
//                                        )
//                                )
//                        )
//                )
//        );
//    }
//
//    private static int resetSkills(CommandContext<CommandSourceStack> context) {
//        try {
//            CommandSourceStack commandSource = context.getSource();
//            ServerPlayer player = EntityArgument.getPlayer(context, "player");
//
//            SkillModel skillModel = SkillModel.get(player);
//            skillModel.createDefault();
//            commandSource.sendSuccess(() -> Component.literal("[" + player.getName().getString() + "] Skills reset!"), false);
//            return 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//    private static int setSkillLevel(CommandContext<CommandSourceStack> context) {
//        try {
//            CommandSourceStack commandSource = context.getSource();
//            ResourceLocation skillId = ResourceLocationArgument.getId(context, "skillId");
//            ServerPlayer player = EntityArgument.getPlayer(context, "player");
//
//            int level = IntegerArgumentType.getInteger(context, "skillLevel");
//
//            SkillModel skillModel = SkillModel.get(player);
//            skillModel.setSkillLevel(skillId, level);
//            commandSource.sendSuccess(() -> Component.literal("[" + player.getName().getString() + "] Skill " + skillId + " level: " + skillModel.getSkillLevel(skillId)), false);
//            return 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//    private static int getSkillLevel(CommandContext<CommandSourceStack> context) {
//        try {
//            CommandSourceStack commandSource = context.getSource();
//            ResourceLocation skillId = ResourceLocationArgument.getId(context, "skillId");
//            ServerPlayer player = EntityArgument.getPlayer(context, "player");
//
//            int level = SkillHelper.getPlayerData(player).getSkillLevel(skillId);
//
//            commandSource.sendSuccess(() -> Component.literal("[" + player.getName().getString() + "] Skill " + skillId + " level: " + level), false);
//            return 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//}
