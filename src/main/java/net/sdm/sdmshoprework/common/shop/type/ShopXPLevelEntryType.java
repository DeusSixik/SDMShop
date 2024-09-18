package net.sdm.sdmshoprework.common.shop.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.api.shop.AbstractShopTab;
import net.sdm.sdmshoprework.common.ftb.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.utils.NBTUtils;

public class ShopXPLevelEntryType extends AbstractShopEntryType {

    public int xpLevel;
    public ItemStack iconPath = Items.EXPERIENCE_BOTTLE.getDefaultInstance();

    public ShopXPLevelEntryType(int xpLevel){
        this.xpLevel = xpLevel;
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.setExperienceLevels(player.experienceLevel - (xpLevel * countSell));
            SDMShopR.addMoney(player, entry.entryPrice * (countSell));
        }
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            long playerMoney = SDMShopR.getMoney(player);
            long needMoney = entry.entryPrice * countBuy;

            serverPlayer.setExperienceLevels(player.experienceLevel + (xpLevel * countBuy));

            SDMShopR.setMoney(player, playerMoney - needMoney);
        }
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if(isSell){
            return player.experienceLevel > (countSell * xpLevel);
        }

        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(isSell){
            if(player.totalExperience == 0 || xpLevel == 0) return 0;
            return (int) (player.experienceLevel / xpLevel);
        } else {
            long playerMoney = SDMShopR.getMoney(player);
            if(entry.entryPrice == 0) return Byte.MAX_VALUE;
            return (int) (playerMoney / entry.entryPrice);
        }
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        ItemStack icon = iconPath.copy();
        icon.setCount(shopEntry.entryCount);
        return ItemIcon.getItemIcon(icon);
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {
        configGroup.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        configGroup.addInt("level", xpLevel, v -> xpLevel = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopXPLevelEntryType(xpLevel);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.xpleveltype");
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();

        nbt.putInt("level", xpLevel);
        NBTUtils.putItemStack(nbt, "iconPath", iconPath);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.xpLevel = nbt.getInt("level");
        this.iconPath = NBTUtils.getItemStack(nbt, "iconPath");
    }

    @Override
    public String getId() {
        return "xpLevelType";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopXPLevelEntryType(0);
        }
    }
}
