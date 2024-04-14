package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

import java.util.regex.Pattern;

public class CommandEntryType implements IEntryType{

    private String iconPath = "minecraft:item/barrier";
    public String command = "";
    public boolean elevatePerms;
    public boolean silent;

    public CommandEntryType(String command, String iconPath){
        this.command = command;
        this.iconPath = iconPath;
    }

    public static CommandEntryType of(String command, String iconPath){
        return new CommandEntryType(command, iconPath);
    }

    @Override
    public boolean isSellable() {
        return false;
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public Icon getIcon() {
        Icon getted = Icon.getIcon(iconPath);
        if(getted.isEmpty()) return Icons.BARRIER;
        return getted;
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
        group.addString("command", command, v -> command = v, "/time set day", Pattern.compile("^/.*"));
        group.addBool("elevatePerms", elevatePerms, v -> elevatePerms = v, false);
        group.addBool("silent", silent, v -> silent = v, false);
    }

    @Override
    public Icon getCreativeIcon() {
        return Icons.BOOK;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", "commandType");
        nbt.putString("iconPath", iconPath);
        nbt.putString("command", command);
        nbt.putBoolean("elevatePerms", elevatePerms);
        nbt.putBoolean("silent", silent);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        iconPath = nbt.getString("iconPath");
        command = nbt.getString("command");
        elevatePerms = nbt.getBoolean("elevatePerms");
        silent = nbt.getBoolean("silent");
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {

        if(command.isEmpty()) return;
        CommandSourceStack source = player.createCommandSourceStack();
        if (elevatePerms) source = source.withPermission(2);
        if (silent) source = source.withSuppressedOutput();

        player.server.getCommands().performPrefixedCommand(source, command);
    }
}
