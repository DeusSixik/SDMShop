package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.network.UpdateMoney;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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


        long playerMoney = SDMShopR.getMoney(player);
        int needMoney = entry.price * countBuy;

        for (int i = 0; i < countBuy; i++) {
            ItemStack d1 = itemStack.copy();
            d1.setCount(entry.count);
            ItemHandlerHelper.giveItemToPlayer(player, d1);
        }

        SDMShopR.setMoney(player, playerMoney - needMoney);

    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {

        ItemStack stack = itemStack;
        List<ItemStack> stackList = new ArrayList<>();

        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            if(ItemStack.matches(player.getInventory().getItem(index), (stack.copy())) || ItemStack.isSameItem(player.getInventory().getItem(index), (stack.copy()))){
                stackList.add(player.getInventory().getItem(index));
            }
        }

        int amountItems = 0;
        for (ItemStack item : stackList){
            amountItems += item.getCount();
        }

        int amount = amountItems >= entry.count * countSell ? entry.count * countSell : 0;
        if(amountItems == 0 || amount == 0) return;

        if (amount <= 0) return;
        if(this.sellItem(player, amount, stack)) SDMShopR.addMoney(player, entry.price * (countSell));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(isSell){
            int countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).equals(itemStack)) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            if(countItems < (entry.count * countSell)) return false;
            return true;
        } else {
            long playerMoney = SDMShopR.getClientMoney();
            int needMoney = entry.price * countSell;
            if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
            return true;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell){
            int countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).is(itemStack.getItem()) && Objects.equals(inventory.getItem(i).getTag(), itemStack.getTag())) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            SDMShopR.LOGGER.info("Founded items " + countItems);
            SDMShopR.LOGGER.info("Calculated items  " + countItems / entry.count);
            return countItems / entry.count;
        } else {
            long playerMoney = SDMShopR.getClientMoney();
            return (int) (playerMoney / entry.price);
        }
    }


    public static boolean sellItem(ServerPlayer p, int amm, ItemStack item) {
        int totalamm = 0; //общее количество вещей в инвентаре
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) { //считаем эти вещи
            if (p.getInventory().getItem(a)!=null){
                /*весь ItemStack можно описать тремя параметрами. item.getData, item.getItemMeta и item.getAmmaount.
                 *При item.equas(item2)ammount тоже сравнивается, поэтому видим такое сравнение
                 */
                if (ItemStack.isSameItem(p.getInventory().getItem(a), item) || ItemStack.matches(p.getInventory().getItem(a), item)){
                    totalamm += p.getInventory().getItem(a).getCount();
                }
            }
        }
        if (totalamm==0) {
            return false;
        }
        if (totalamm<amm) {
            return false;
        }
        int ammountleft =amm; //эта переменная не очень нужна, но мне с ней удобнее
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) {
            if (ammountleft==0){return true;}
            if (p.getInventory().getItem(a)==null) continue;
            if (ItemStack.isSameItem(p.getInventory().getItem(a), item) || ItemStack.matches(p.getInventory().getItem(a), item)) {
                if (p.getInventory().getItem(a).getCount()<ammountleft) {
                    ammountleft-=p.getInventory().getItem(a).getCount();
                    p.getInventory().setItem(a, ItemStack.EMPTY);
                }
                if (p.getInventory().getItem(a)!=null&&p.getInventory().getItem(a).getCount()==ammountleft) {
                    p.getInventory().setItem(a, ItemStack.EMPTY);
                    return true;
                }

                if (p.getInventory().getItem(a).getCount()>ammountleft&&p.getInventory().getItem(a)!=null) {
                    p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount()-ammountleft);
                    return true;
                }
            }
        }
        return false;
    }
}
