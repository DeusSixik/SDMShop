package net.sixik.sdmshop.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.config.ShopConfig;

import java.util.Locale;

public final class ShopAdminUtils {

    public static final boolean SEND_MESSAGES = ShopConfig.SHOW_ADMIN_MESSAGES.get();

    private ShopAdminUtils() {}

    private static final String MOD_NAME = "SDMShop";
    private static final MutableComponent PREFIX =
            Component.literal("[").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(MOD_NAME).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD))
                    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY));

    public enum Level {
        INFO("ℹ ", ChatFormatting.AQUA),
        WARN("⚠ ", ChatFormatting.GOLD),
        ERROR("✖ ", ChatFormatting.RED);

        final String icon;
        final ChatFormatting color;

        Level(String icon, ChatFormatting color) {
            this.icon = icon;
            this.color = color;
        }
    }

    public static void info(Player player, Component message)  { send(player, Level.INFO, message, null); }
    public static void warn(Player player, Component message)  { send(player, Level.WARN, message, null); }
    public static void error(Player player, Component message) { send(player, Level.ERROR, message, null); }

    public static void info(Player player, String fmt, Object... args)  { send(player, Level.INFO, format(fmt, args), fmt); }
    public static void warn(Player player, String fmt, Object... args)  { send(player, Level.WARN, format(fmt, args), fmt); }
    public static void error(Player player, String fmt, Object... args) { send(player, Level.ERROR, format(fmt, args), fmt); }


    private static MutableComponent format(String fmt, Object... args) {
        return Component.literal(String.format(Locale.ROOT, fmt, args));
    }

    private static void send(Player player, Level level, Component message, String rawFmtForCopy) {
        if (player == null || message == null || !SEND_MESSAGES) return;

        MutableComponent line = PREFIX.copy()
                .append(Component.literal(level.icon).withStyle(level.color))
                .append(message.copy().withStyle(ChatFormatting.WHITE));

        String copyText = message.getString();
        line = line.withStyle(Style.EMPTY
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Component.literal("Click to copy\n").withStyle(ChatFormatting.DARK_GRAY)
                                .append(Component.literal(MOD_NAME + " admin log").withStyle(ChatFormatting.GRAY))
                ))
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copyText))
        );

        player.sendSystemMessage(line);
    }
}
