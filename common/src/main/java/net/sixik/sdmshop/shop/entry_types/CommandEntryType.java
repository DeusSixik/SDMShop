package net.sixik.sdmshop.shop.entry_types;


import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommandEntryType extends AbstractEntryType {

    protected static final String DEFAULT_KEY = "/time set day";

    protected String command;
    protected boolean elevatePerms;
    protected boolean silent;
    protected int maxBuyCount;

    public CommandEntryType(ShopEntry shopEntry) {
        this(shopEntry, DEFAULT_KEY, true, true, 1);
    }

    public CommandEntryType(ShopEntry shopEntry, String command, boolean elevatePerms, boolean silent, int maxBuyCount) {
        super(shopEntry);
        this.command = command;
        this.elevatePerms = elevatePerms;
        this.silent = silent;
        this.maxBuyCount = maxBuyCount;
    }

    @Override
    public AbstractEntryType copy() {
        return new CommandEntryType(shopEntry, command, elevatePerms, silent, maxBuyCount);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        if(player instanceof ServerPlayer serverPlayer) {
            if(command.isEmpty()) return false;

            var copyCommand = command;

            if(copyCommand.contains("{player}"))
                copyCommand = copyCommand.replace("{player}", serverPlayer.getName().getString());

            CommandSourceStack commandSource = serverPlayer.createCommandSourceStack();
            if(elevatePerms)
                commandSource = commandSource.withPermission(2);

            if(this.silent)
                commandSource = commandSource.withSuppressedOutput();

            for (int i = 0; i < countBuy; i++) {
                player.getServer().getCommands().performPrefixedCommand(commandSource, copyCommand);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        throw new NotImplementedException();
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        return howMany(player, entry) > 0;
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        double price = entry.getEntrySellerType().getMoney(player, entry);
        return (int) Math.max(0, Math.min(price / entry.getPrice(), maxBuyCount));
    }



    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.command");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdm.shop.entry.creator.type.command.description"));
        return list;
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.COMMAND_BLOCK);
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("command", this.command, (v) -> this.command = v, "/time set day", Pattern.compile("^/.*"));
        group.addBool("elevatePerms", this.elevatePerms, (v) -> this.elevatePerms = v, false);
        group.addBool("silent", this.silent, (v) -> this.silent = v, false);
        group.addInt("maxBuyCount", this.maxBuyCount, v -> this.maxBuyCount = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public EntryTypeProperty getProperty() {
        return EntryTypeProperty.ONLY_BUY;
    }

    @Override
    public String getId() {
        return "commandType";
    }

    @Override
    public boolean isSearch(String search) {
        return command.contains(search);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("command", command);
        nbt.putBoolean("elevatePerms", true);
        nbt.putBoolean("silent", true);
        nbt.putInt("maxBuyCount", maxBuyCount);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        command = nbt.getString("command");
        if(nbt.contains("elevatePerms"))
            elevatePerms = nbt.getBoolean("elevatePerms");
        if(nbt.contains("silent"))
            silent = nbt.getBoolean("silent");
        if(nbt.contains("maxBuyCount"))
            maxBuyCount = nbt.getInt("maxBuyCount");
    }
}
