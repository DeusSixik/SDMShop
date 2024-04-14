package net.sdm.sdmshopr.shop.entry;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.type.IEntryType;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.NBTUtils;

public class ShopEntry<T extends IEntryType> implements INBTSerializable<CompoundTag> {
    public ShopTab tab;
    public String tittle;
    public int count;
    public int price;
    public boolean isSell;
    public T type;

    public ShopEntry(){}
    public ShopEntry(ShopTab tab){
        this.tab = tab;
    }

    public ShopEntry(ShopTab tab, T type, int count, int price, boolean isSell){
        this.type = type;
        this.count = count;
        this.price = price;
        this.isSell = isSell;
        this.tab = tab;
        this.tittle = "";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("count", count);
        nbt.putInt("price", price);
        nbt.putBoolean("isSell", isSell);
        nbt.putString("tittle", tittle);
        nbt.put("type", type.serializeNBT());

        return nbt;
    }

    public ShopEntry<T> copy(){
        return new ShopEntry<>(null, type, count, price, isSell);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        count = nbt.getInt("count");
        price = nbt.getInt("price");
        isSell = nbt.getBoolean("isSell");
        tittle = nbt.getString("tittle");
        type = NBTUtils.getEntryType(nbt.getCompound("type"));
    }

    public void getConfig(ConfigGroup config){
        config.addString("tittle", tittle, v -> tittle = v, "");
        if(type.isCountable()) config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addInt("price", price, v -> price = v, 1, 0, Integer.MAX_VALUE);


        if(type.isSellable()) config.addBool("isSell", isSell, v -> isSell = v, false);

        ConfigGroup type = config.getOrCreateSubgroup("type");
        this.type.getConfig(type);
    }


    public int getIndex() {
        return tab.shopEntryList.indexOf(this);
    }

    public void execute(ServerPlayer player, int countBuy, ShopEntry<?> entry){
        if (isSell) {
            type.sell(player, countBuy, entry);
        } else {
            long playerMoney = SDMShopR.getMoney(player);
            int needMoney = entry.price * countBuy;
            if(playerMoney < needMoney || playerMoney - needMoney < 0) return;
            type.buy(player, countBuy, entry);
        }

    }
}
