package net.sdm.sdmshopr.shop.entry;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.ConditionRegister;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.api.IShopCondition;
import net.sdm.sdmshopr.api.customization.APIShopEntry;
import net.sdm.sdmshopr.api.register.ShopEntryButtonsRegister;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopEntry<T extends IEntryType> implements INBTSerializable<CompoundTag> {



    public ShopTab tab;
    public String tittle;
    public int count;
    public int price;
    public boolean isSell;

    public T type;

    public final List<IShopCondition> conditions = new ArrayList<>();

    public final List<String> gameStages = new ArrayList<>();

    public List<String> TAGS = new ArrayList<>();

    public String buttonStyle = "";

    public ShopEntry(){}
    public ShopEntry(ShopTab tab){
        this.tab = tab;
    }

    public ShopEntry(ShopTab tab, T type, int count, int price, boolean isSell){
        this.type = type;
        this.count = count;
        this.price = price;
        if(type.isOnlySell()) this.isSell = true;
        else this.isSell = isSell;
        this.tab = tab;
        this.tittle = "";

        for (Map.Entry<String, IShopCondition> d1 : ConditionRegister.CONDITIONS.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())){
                conditions.add(d1.getValue().create());
            }
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("count", count);
        nbt.putInt("price", price);
        nbt.putBoolean("isSell", isSell);
        nbt.putString("tittle", tittle);
        if(type != null)
            nbt.put("type", type.serializeNBT());
        nbt.putString("buttonStyle", buttonStyle);

        ListTag f1 = new ListTag();
        for (String tag : TAGS) {
            f1.add(StringTag.valueOf(tag));
        }
        nbt.put("tags", f1);

        for (IShopCondition condition : conditions) {
            condition.serializeNBT(nbt);
        }


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
        if(nbt.contains("buttonStyle"))
            buttonStyle = nbt.getString("buttonStyle");

        if(nbt.contains("tags")) {
            TAGS.clear();
            ListTag f1 = (ListTag) nbt.get("tags");
            for (Tag tag : f1) {
                TAGS.add(tag.getAsString());
            }
        }

        for (Map.Entry<String, IShopCondition> d1 : ConditionRegister.CONDITIONS.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())) {
                IShopCondition condition = d1.getValue().create();
                condition.deserializeNBT(nbt);
                conditions.add(condition);
            }
        }
    }

    public APIShopEntry getButton(){
        if(buttonStyle.isEmpty())
            return ShopEntryButtonsRegister.BASE;
        return ShopEntryButtonsRegister.TYPES.get(buttonStyle);
    }

    public void getConfig(ConfigGroup config){

        config.addString("tittle", tittle, v -> tittle = v, "");
        if(type.isCountable()) config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addInt("price", price, v -> price = v, 1, 0, Integer.MAX_VALUE);


        if(type.isSellable() && !type.isOnlySell()) config.addBool("isSell", isSell, v -> isSell = v, false);

        config.addList("tags", TAGS, new StringConfig(null), "");
        config.addEnum("buttonStyle", buttonStyle.isEmpty() ? "BASE" : buttonStyle, v -> buttonStyle = v, getIDs());

        ConfigGroup type = config.getOrCreateSubgroup("type");
        this.type.getConfig(type);

        ConfigGroup group = config.getOrCreateSubgroup("dependencies");

        for (IShopCondition condition : conditions) {
            condition.getConfig(group);
        }
    }

    public NameMap<String> getIDs(){
        List<String> ids = new ArrayList<>();
        for (Map.Entry<String, APIShopEntry> stringAPIShopEntryButtonEntry : ShopEntryButtonsRegister.TYPES.entrySet()) {
            ids.add(stringAPIShopEntryButtonEntry.getKey());
        }

        return NameMap.<String>of("BASE", ids).create();
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

    @OnlyIn(Dist.CLIENT)
    public boolean isLocked(){
        if(SDMShopR.isEditModeClient()) return false;

        for (IShopCondition condition : conditions) {
            if(condition.isLocked()) return true;
        }

        return type.isLocked();
    }
}
