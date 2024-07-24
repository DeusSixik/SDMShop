package net.sdm.sdmshopr.shop.entry;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.api.ISpecialEntryCondition;
import net.sdm.sdmshopr.api.limiter.ILimiter;
import net.sdm.sdmshopr.api.register.ConditionRegister;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.api.IShopCondition;
import net.sdm.sdmshopr.api.customization.APIShopEntry;
import net.sdm.sdmshopr.api.register.ShopEntryButtonsRegister;
import net.sdm.sdmshopr.data.ServerShopData;
import net.sdm.sdmshopr.shop.limiter.AbstractLimiterData;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopEntry<T extends IEntryType> implements ILimiter, INBTSerializable<CompoundTag> {


    public UUID entryID;
    public ShopTab tab;
    public String tittle;
    public int count;
    public int price;
    public boolean isSell;
    public boolean isSpecialEntry = false;
    public boolean isHaveLimiter = false;
    public boolean isGlobalLimiter = false;
    public int limitOnEntry = 0;

    public List<ISpecialEntryCondition> specialEntryConditions = new ArrayList<>();

    public T type;

    public final List<IShopCondition> conditions = new ArrayList<>();

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
        this.entryID = UUID.randomUUID();

        for (Map.Entry<String, IShopCondition> d1 : ConditionRegister.CONDITIONS.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())){
                conditions.add(d1.getValue().create());
            }
        }
    }

    public boolean isSpecialConditionSuccess(){

        if(isSpecialEntry && !SDMShopR.isEditModeClient()){
            for (ISpecialEntryCondition specialEntryCondition : specialEntryConditions) {
                if(!specialEntryCondition.isConditionSuccess()) return false;
            }
        }

        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("count", count);
        nbt.putInt("price", price);
        nbt.putBoolean("isSell", isSell);
        nbt.putString("tittle", tittle);
        nbt.putBoolean("isSpecialEntry", isSpecialEntry);
        nbt.putBoolean("isHaveLimiter", isHaveLimiter);
        nbt.putBoolean("isGlobalLimiter", isGlobalLimiter);
        nbt.putInt("limitOnEntry", limitOnEntry);
        if(entryID != null)
            nbt.putUUID("entryID", entryID);
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

        isSpecialEntry = nbt.getBoolean("isSpecialEntry");
        isHaveLimiter = nbt.getBoolean("isHaveLimiter");
        isGlobalLimiter = nbt.getBoolean("isGlobalLimiter");
        limitOnEntry = nbt.getInt("limitOnEntry");

        if(nbt.contains("entryID")){
            entryID = nbt.getUUID("entryID");
        } else entryID = UUID.randomUUID();
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

        ConfigGroup limiter = config.getOrCreateSubgroup("limiter");
//        config.addBool("isSpecialEntry", isSpecialEntry, v -> isSpecialEntry = v, false);
        limiter.addBool("isHaveLimiter", isHaveLimiter, v -> isHaveLimiter = v, false);
        limiter.addBool("isGlobalLimiter", isGlobalLimiter, v -> isGlobalLimiter = v, false);
        limiter.addInt("limitOnEntry", limitOnEntry, v -> limitOnEntry = v, 0, 0, Integer.MAX_VALUE);


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

    @Override
    public boolean canUseEntryOnServer(Player player) {
        if(!isHaveLimit()) return true;

        if(getLimitOnEntry() > ServerShopData.INSTANCE.limiterData.getCount(entryID, player.getUUID())) return true;

        return false;
    }

    @Override
    public boolean canUseEntryOnClient() {
        if(!isHaveLimit()) return true;

        if(SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.get(entryID) == null) return true;

        if(getLimitOnEntry() > SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.get(entryID).countBuys) return true;

        return false;
    }

    public boolean isHaveMore(){

        if(!isHaveLimit()) return true;

        if(SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.get(entryID) != null)
            return getLimitOnEntry() > SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.get(entryID).countBuys;
        return true;
    }


    public int getLeftEntry(){
        if(SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.get(entryID) != null)
            return getLimitOnEntry() - SDMShopRClient.clientShopData.limiterData.ENTIES_MAP.get(entryID).countBuys;
        return getLimitOnEntry();
    }

    @Override
    public int getLimitOnEntry() {
        return limitOnEntry;
    }

    @Override
    public boolean isHaveLimit() {
        return isHaveLimiter;
    }

    @Override
    public boolean isGlobal() {
        return isGlobalLimiter;
    }
}
