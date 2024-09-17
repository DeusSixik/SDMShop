package net.sdm.sdmshoprework.common.shop.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.common.ftb.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;

import java.util.regex.Pattern;

public class ShopCommandEntryType extends AbstractShopEntryType {

    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public String command = "";
    public boolean elevatePerms;
    public boolean silent;

    public ShopCommandEntryType(String command, ItemStack iconPath){
        this.command = command;
        this.iconPath = iconPath;
    }

    public static ShopCommandEntryType of(String command, ItemStack iconPath){
        return new ShopCommandEntryType(command, iconPath);
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());

        group.addString("command", command, v -> command = v, "/time set day", Pattern.compile("^/.*"));
        group.addBool("elevatePerms", elevatePerms, v -> elevatePerms = v, false);
        group.addBool("silent", silent, v -> silent = v, false);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.command");
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopCommandEntryType(command, iconPath);
    }

    @Override
    public SellType getSellType() {
        return SellType.ONLY_BUY;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.put("iconPathNew", iconPath.serializeNBT());
        nbt.putString("command", command);
        nbt.putBoolean("elevatePerms", elevatePerms);
        nbt.putBoolean("silent", silent);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        iconPath = ItemStack.of(nbt.getCompound("iconPathNew"));
        command = nbt.getString("command");
        elevatePerms = nbt.getBoolean("elevatePerms");
        silent = nbt.getBoolean("silent");
    }

    @Override
    public String getId() {
        return "commandType";
    }


    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer){
            if(command.isEmpty()) return;
            CommandSourceStack source = serverPlayer.createCommandSourceStack();
            if (elevatePerms) source = source.withPermission(2);
            if (silent) source = source.withSuppressedOutput();

            try {
                ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(source, command);
                SDMShopR.setMoney(serverPlayer, SDMShopR.getMoney(serverPlayer) - entry.entryPrice * countBuy);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        long playerMoney = SDMShopR.getMoney(player);
        if(entry.entryPrice == 0) return 1;
        return (int) (playerMoney / entry.entryPrice) > 1 ? 1 : 0;
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopCommandEntryType("", ItemStack.EMPTY);
        }
    }
}
