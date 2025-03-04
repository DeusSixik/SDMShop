package net.sixik.sdmshoprework.api.shop;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.SDMSerializeParam;
import net.sixik.sdmshoprework.api.ShopSerializerHandler;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.common.data.LimiterData;
import net.sixik.sdmshoprework.common.shop.sellerType.MoneySellerType;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractShopEntry {

    public static final String DEFAULT_MONEY = "MONEY";

    public UUID entryUUID;

    public String title = "";

    public long entryPrice = 0;
    public int entryCount = 1;
    public int limit = 0;

    public boolean isSell = false;
    public boolean globalLimit = false;

    public ItemStack icon = Items.BARRIER.getDefaultInstance();

    public List<String> descriptionList = new ArrayList<>();

    private AbstractShopEntryType entryType = null;
    private AbstractShopTab shopTab;

    public AbstractShopSellerType<?> shopSellerType;
    public String sellerTypeID;

    private final List<AbstractShopEntryCondition> entryConditions = new ArrayList<>();

    public AbstractShopEntry(AbstractShopTab shopTab) {
        this.shopTab = shopTab;
        this.entryUUID = UUID.randomUUID();
        shopSellerType = new MoneySellerType();;
        sellerTypeID = shopSellerType.getId();

        for (IConstructor<AbstractShopEntryCondition> value : ShopContentRegister.SHOP_ENTRY_CONDITIONS.values()) {
            AbstractShopEntryCondition condition = value.createDefaultInstance();
            if(Platform.isModLoaded(condition.getModId())) {
                entryConditions.add(condition);
            }
        }
    }

    public void setEntryPrice(long entryPrice, int entryCount){
        this.entryPrice = entryPrice;
        this.entryCount = entryCount;
    }

    public void setEntryType(AbstractShopEntryType entryType) {
        this.entryType = entryType;
        this.entryType.setShopEntry(this);
    }

    public AbstractShopTab getShopTab() {
        return shopTab;
    }

    public AbstractShopEntryType getEntryType() {
        return entryType;
    }

    public List<AbstractShopEntryCondition> getEntryConditions() {
        return entryConditions;
    }

    public void getConfig(ConfigGroup config) {

        config.addString("title", title, v -> title = v, "");




        ConfigGroup seller = config.getOrCreateSubgroup("seller_type");
        seller.addEnum("seller_type", sellerTypeID, v -> {
            if(!Objects.equals(v, sellerTypeID)) {
                sellerTypeID = v;
                IConstructor<AbstractShopSellerType<?>> cont = ShopContentRegister.SELLER_TYPES.getOrDefault(sellerTypeID, null);
                shopSellerType = cont.createDefaultInstance();
            }
        }, getList());
        shopSellerType.getConfig(seller);
        seller.addLong("price", entryPrice, v -> entryPrice = v, 1, 0, Long.MAX_VALUE);
        if(entryType.getSellType() == AbstractShopEntryType.SellType.BOTH)
            seller.addBool("isSell", isSell, v -> isSell = v, false);

        config.addList("description", descriptionList, new StringConfig(null), "");

       ConfigGroup entryGroup = config.getOrCreateSubgroup("type");
       if(entryType != null) {
           entryType.getConfig(entryGroup);
       }
        if(entryType.isCountable())
            entryGroup.addInt("count", entryCount, v -> entryCount = v, 1, 1, Integer.MAX_VALUE);
        entryGroup.addInt("limit", limit, v -> limit = v, 0, 0, Integer.MAX_VALUE);

        entryGroup.addBool("globalLimit", globalLimit, v -> globalLimit = v, false);


       ConfigGroup dependenciesGroup = config.getOrCreateSubgroup("dependencies");
        for (AbstractShopEntryCondition entryCondition : entryConditions) {
            entryCondition.getConfig(dependenciesGroup);
        }

    }

    public NameMap<String> getList(){
        List<String> str = new ArrayList<>();

        for (Map.Entry<String, IConstructor<AbstractShopSellerType<?>>> stringIConstructorEntry : ShopContentRegister.SELLER_TYPES.entrySet()) {
            str.add(stringIConstructorEntry.getKey());
        }

        return NameMap.of(DEFAULT_MONEY, str).create();
    }

    public int getIndex() {
        return shopTab.getTabEntry().indexOf(this);
    }

    public boolean isLocked() {

        if(limit != 0 && LimiterData.CLIENT.ENTRY_DATA.getOrDefault(entryUUID,0) >= limit ) return true;
        for (AbstractShopEntryCondition entryCondition : entryConditions) {
            if(entryCondition.isLocked()) return true;
        }

        return false;
    }

    public CompoundTag serializeNBT() {
        return serializeNBT(SDMSerializeParam.SERIALIZE_ALL);
    }

    public CompoundTag serializeNBT(int bits) {
        return ShopSerializerHandler.serializeShopEntry(this, bits);
    }

    public void deserializeNBT(CompoundTag nbt) {
        deserializeNBT(nbt, SDMSerializeParam.SERIALIZE_ALL);
    }

    public void deserializeNBT(CompoundTag nbt, int bits) {
        ShopSerializerHandler.deserializeShopEntry(this, nbt, bits);
    }

    @Override
    public String toString() {
        return "AbstractShopEntry{" +
                "entryUUID=" + entryUUID +
                ", entryPrice=" + entryPrice +
                ", entryCount=" + entryCount +
//                ", entryIcon=" + entryIcon +
                ", entryType=" + entryType +
                ", shopTab=" + shopTab +
                ", entryConditions=" + entryConditions +
                '}';
    }

    public AbstractShopEntry copy() {
        AbstractShopEntry entry = new AbstractShopEntry(getShopTab()) {};
        entry.entryCount = this.entryCount;
        entry.descriptionList = this.descriptionList;
        entry.entryPrice = this.entryPrice;
        entry.title = this.title;
        entry.limit = this.limit;
        entry.setEntryType(getEntryType().copy());
        entry.isSell = this.isSell;
        entry.globalLimit = this.globalLimit;
        entry.icon = this.icon;
        entry.getEntryConditions().addAll(getEntryConditions());
        return  entry;
    }
}
