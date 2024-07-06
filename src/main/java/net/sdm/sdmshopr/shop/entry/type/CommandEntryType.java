package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommandEntryType implements IEntryType{

    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public String command = "";
    public boolean elevatePerms;
    public boolean silent;

    public CommandEntryType(String command, ItemStack iconPath){
        this.command = command;
        this.iconPath = iconPath;
    }

    public static CommandEntryType of(String command, ItemStack iconPath){
        return new CommandEntryType(command, iconPath);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.command");
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
        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());

        group.addString("command", command, v -> command = v, "/time set day", Pattern.compile("^/.*"));
        group.addBool("elevatePerms", elevatePerms, v -> elevatePerms = v, false);
        group.addBool("silent", silent, v -> silent = v, false);
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdmr.shop.entry.creator.type.commandType.description"));
        return list;
    }

    @Override
    public Icon getCreativeIcon() {
        return Icons.BOOK;
    }

    @Override
    public IEntryType copy() {
        return new CommandEntryType(command,iconPath);
    }

    @Override
    public String getID() {
        return "commandType";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", getID());
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        nbt.putString("command", command);
        nbt.putBoolean("elevatePerms", elevatePerms);
        nbt.putBoolean("silent", silent);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
        command = nbt.getString("command");
        elevatePerms = nbt.getBoolean("elevatePerms");
        silent = nbt.getBoolean("silent");
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {

        if(command.isEmpty()) return;
        CommandSourceStack source = player.server.createCommandSourceStack();
        if (elevatePerms) source = source.withPermission(2);
        if (silent) source = source.withSuppressedOutput();

        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(source, command);
    }

    @Override
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        long playerMoney = SDMShopR.getClientMoney();
        return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
    }
}
