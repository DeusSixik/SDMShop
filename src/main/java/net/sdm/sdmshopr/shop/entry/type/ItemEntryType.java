package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.network.UpdateMoney;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemEntryType implements IEntryType{
    public ItemStack itemStack;
    protected ItemEntryType(ItemStack itemStack){
        this.itemStack = itemStack;
    }

    public static ItemEntryType of(ItemStack itemStack){
        return new ItemEntryType(itemStack);
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
        return ItemIcon.getItemIcon(itemStack);
    }

    @Override
    public CompoundTag getIconNBT() {
        CompoundTag nbt = new CompoundTag();
        NBTUtils.putItemStack(nbt, "item", itemStack);
        return nbt;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addItemStack("item", itemStack, v -> itemStack = v, ItemStack.EMPTY, true, true);

    }

    @Override
    public Icon getCreativeIcon() {
        return Icons.DIAMOND;
    }

    @Override
    public CompoundTag serializeNBT() {
        SNBTCompoundTag nbt = new SNBTCompoundTag();
        nbt.putString("type", "itemType");
        NBTUtils.putItemStack(nbt, "item", itemStack);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        itemStack = NBTUtils.getItemStack(nbt, "item");
    }


    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        int countFreeSlots = 0;

        long playerMoney = SDMShopR.getMoney(player);
        int needMoney = entry.price * countBuy;

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if(inventory.getItem(i).equals(ItemStack.EMPTY)) {
                countFreeSlots++;
            }
        }

        if((itemStack.getMaxStackSize() * countFreeSlots > countBuy)) return;


        for (int i = 0; i < countBuy; i++) {
            ItemStack d1 = itemStack.copy();
            d1.setCount(entry.count);
            ItemHandlerHelper.giveItemToPlayer(player, d1);
//            if(!inventory.add(d1)){
//                player.drop(d1, false);
//            }
        }

        SDMShopR.setMoney(player, playerMoney - needMoney);

    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
        int countItems = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if(inventory.getItem(i).equals(itemStack)) {
                countItems += inventory.getItem(i).getCount();
            }
        }

        if(countItems < (entry.count * countSell)) return;

        int needDelete = (entry.count * countSell);

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if(needDelete == 0) break;

            if(inventory.getItem(i).equals(itemStack)) {
                int f1 = inventory.getItem(i).getCount();
                inventory.removeItem(i, f1);
                needDelete -= f1;
            }
        }

        SDMShopR.addMoney(player, (long) entry.count * entry.price);
    }
}
