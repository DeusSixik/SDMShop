package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

public class XPLevelEntryType implements IEntryType {


    public int xpLevel;
    public ItemStack iconPath = Items.EXPERIENCE_BOTTLE.getDefaultInstance();

    public XPLevelEntryType(int xpLevel){
        this.xpLevel = xpLevel;
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {


        player.setExperienceLevels(player.experienceLevel - (xpLevel * countSell));
        SDMShopR.addMoney(player, entry.price * (countSell));
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        long playerMoney = SDMShopR.getMoney(player);
        int needMoney = entry.price * countBuy;

        player.setExperienceLevels(player.experienceLevel + (xpLevel * countBuy));

        SDMShopR.setMoney(player, playerMoney - needMoney);

    }

    @Override
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        Player player = Minecraft.getInstance().player;

        if(isSell){
            return player.experienceLevel > (countSell * xpLevel);
        }

        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell){
            Player player = Minecraft.getInstance().player;
            if(player.totalExperience == 0 || xpLevel == 0) return 0;
            return (int) (player.experienceLevel / xpLevel);
        } else {
            long playerMoney = SDMShopR.getClientMoney();
            return (int) (playerMoney / entry.price);
        }
    }

    @Override
    public boolean isSellable() {
        return true;
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
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addInt("level", xpLevel, v -> xpLevel = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    public String getID() {
        return "xpLevelType";
    }

    @Override
    public IEntryType copy() {
        return new XPLevelEntryType(xpLevel);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.xpleveltype");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();

        nbt.putInt("level", xpLevel);
        NBTUtils.putItemStack(nbt, "iconPath", iconPath);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.xpLevel = nbt.getInt("level");
        this.iconPath = NBTUtils.getItemStack(nbt, "iconPath");
    }
}
