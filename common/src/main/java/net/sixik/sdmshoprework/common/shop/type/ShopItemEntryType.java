package net.sixik.sdmshoprework.common.shop.type;

import de.cadentem.quality_food.util.OverlayUtils;
import de.cadentem.quality_food.util.QualityUtils;
import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.utils.NBTUtils;
import net.sixik.sdmshoprework.common.utils.SDMItemHelper;
import net.sixik.sdmshoprework.common.utils.item.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopItemEntryType extends AbstractShopEntryType {

    public ItemStack itemStack;
    public boolean ignoreNBT = false;

    public ShopItemEntryType(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addItemStack("item", itemStack, v -> itemStack = v, ItemStack.EMPTY, true, true);
//        group.addBool("ignore_nbt", ignoreNBT, v -> ignoreNBT = v, false);
    }

    @Override
    public boolean isSearch(String search) {
        return itemStack.getDisplayName().getString().contains(search) || BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString().contains(search);
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopItemEntryType(itemStack);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.item");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdmr.shop.entry.creator.type.itemType.description"));
        return list;
    }

    @Override
    public void sendNotifiedMessage(Player player) {
        if(shopEntry.isSell) {
            Component text = Component.translatable("sdm.shop.entry.sell.info.item", itemStack.getDisplayName().getString(), shopEntry.entryCount, SDMShopRework.moneyString(SDMShopR.getMoney(player))).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
            player.displayClientMessage(text, false);
        } else {
            Component text = Component.translatable("sdm.shop.entry.buy.info.item", itemStack.getDisplayName().getString(), shopEntry.entryCount, SDMShopRework.moneyString(SDMShopR.getMoney(player))).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
            player.displayClientMessage(text, false);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        NBTUtils.putItemStack(nbt,"itemStack", itemStack);
        if(ignoreNBT)
            nbt.putBoolean("ignoreNBT", true);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("ignoreNBT"))
            this.ignoreNBT = nbt.getBoolean("ignoreNBT");

        this.itemStack = NBTUtils.getItemStack(nbt, "itemStack");
    }

    @Override
    public String getId() {
        return "shopItemEntryType";
    }

    @Override
    public Icon getIcon() {
        ItemStack d1 = itemStack.copy();
        d1.setCount(shopEntry.entryCount);

        if(Platform.isModLoaded("quality_food")) {
            if(QualityUtils.hasQuality(d1)) {
                d1 = OverlayUtils.getOverlay(d1);
                d1.setCount(shopEntry.entryCount);
            }
        }

        return ItemIcon.getItemIcon(d1);
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
//        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countBuy;

        for (int i = 0; i < countBuy; i++) {
            ItemStack d1 = itemStack.copy();
            d1.setCount(entry.entryCount);
            ItemHandlerHelper.giveItemToPlayer(player, d1);
        }

        entry.shopSellerType.buy(player, entry, needMoney);
//        SDMShopR.setMoney(player, playerMoney - needMoney);
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        ItemStack stack = itemStack.copy();

        int amountItems = SDMItemHelper.countItems(player, stack);

        int amount = amountItems >= entry.entryCount * countSell ? entry.entryCount * countSell : 0;
        if(amountItems == 0 || amount == 0) return;

        if (amount <= 0) return;
        if(SDMItemHelper.sellItem(player, amount, stack, !itemStack.hasTag()))
            entry.shopSellerType.buy(player, entry, entry.entryPrice * (countSell));
//            SDMShopR.addMoney(player, entry.entryPrice * (countSell));
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if (isSell) {
            int countItems = SDMItemHelper.countItems(player, itemStack);
            return countItems >= (entry.entryCount * countSell);
        } else {
            long playerMoney = entry.shopSellerType.getCount(player);
            long needMoney = entry.entryPrice * countSell;
            return playerMoney >= needMoney;
        }
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(isSell){
            int countItems = SDMItemHelper.countItems(player, itemStack);;
            return countItems / entry.entryCount;
        } else {
            long playerMoney = entry.shopSellerType.getCount(player);
            if(entry.entryPrice == 0) return Byte.MAX_VALUE;
            return (int) (playerMoney / entry.entryPrice);
        }
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {
        @Override
        public ShopItemEntryType createDefaultInstance() {
            return new ShopItemEntryType(ItemStack.EMPTY);
        }
    }

}
