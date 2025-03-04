package net.sixik.sdmshoprework.common.shop.type;

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
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

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
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        nbt.putString("command", command);
        if(elevatePerms)
            nbt.putBoolean("elevatePerms", true);
        if(silent)
            nbt.putBoolean("silent", true);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        iconPath = ItemStack.of(nbt.getCompound("iconPathNew"));
        command = nbt.getString("command");
        if(nbt.contains("elevatePerms"))
            elevatePerms = nbt.getBoolean("elevatePerms");
        if(nbt.contains("silent"))
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

            if(command.contains("{player}")) {
                command = command.replace("{player}", serverPlayer.getName().getString());
            }

            CommandSourceStack source = serverPlayer.createCommandSourceStack();
            if (elevatePerms) source = source.withPermission(2);
            if (silent) source = source.withSuppressedOutput();

            try {
                player.getServer().getCommands().performPrefixedCommand(source, command);

                entry.shopSellerType.buy(player, entry, entry.entryPrice * countBuy);
//                SDMShopR.setMoney(serverPlayer, SDMShopR.getMoney(serverPlayer) - entry.entryPrice * countBuy);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        long playerMoney = entry.shopSellerType.getCount(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        long playerMoney = entry.shopSellerType.getCount(player);
        if(entry.entryPrice == 0) return 1;
        return (int) (playerMoney / entry.entryPrice) >= 1 ? 1 : 0;
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopCommandEntryType("", ItemStack.EMPTY);
        }
    }
}
