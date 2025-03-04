package net.sixik.sdmshoprework.api.shop;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.api.SDMSerializeParam;
import net.sixik.sdmshoprework.api.ShopSerializerHandler;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.common.data.LimiterData;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractShopTab implements INBTSerializable<CompoundTag> {

    public UUID shopTabUUID;

    private ShopBase shop;

    public Component title = Component.empty();

    public ItemStack icon = Items.BARRIER.getDefaultInstance();
    private final List<AbstractShopEntry> tabEntry = new ArrayList<>();
    private final List<AbstractShopEntryCondition> tabConditions = new ArrayList<>();

    public List<String> descriptionList = new ArrayList<>();

    public int limit = 0;
    public boolean globalLimit = false;

    public AbstractShopTab(ShopBase shop){
        this.shop = shop;
        this.shopTabUUID = UUID.randomUUID();

        for (IConstructor<AbstractShopEntryCondition> value : ShopContentRegister.SHOP_ENTRY_CONDITIONS.values()) {
            AbstractShopEntryCondition condition = value.createDefaultInstance();
            if(Platform.isModLoaded(condition.getModId())) {
                tabConditions.add(condition);
            }
        }
    }

    public void createShopEntry(CompoundTag nbt) {
        AbstractShopEntry entry = new ShopEntry(this);
        entry.deserializeNBT(nbt);
        tabEntry.add(entry);
    }

    public ShopBase getShop() {
        return shop;
    }

    public List<AbstractShopEntryCondition> getTabConditions() {
        return tabConditions;
    }

    public List<AbstractShopEntry> getTabEntry() {
        return tabEntry;
    }

    public boolean removeEntry(UUID uuid) {
       var it = getTabEntry().iterator();
       while (it.hasNext()) {
           AbstractShopEntry entry = it.next();
           if(entry.entryUUID.equals(uuid)) {
               it.remove();
               return true;
           }
       }
       return false;
    }

    public AbstractShopEntry getShopEntry(UUID uuid){
        for (AbstractShopEntry shopEntry : tabEntry) {
            if(Objects.equals(shopEntry.entryUUID, uuid))
                return shopEntry;
        }
        return null;
    }

    public void getConfig(ConfigGroup config){
        TooltipList list = new TooltipList();
        list.add(Component.translatable("sdmr.shop.tab.title.info"));

        config.addString("title", title.getString(), v -> title = Component.translatable(v), "");

        config.add("icon", new ConfigIconItemStack(), icon, v -> icon = v, Items.BARRIER.getDefaultInstance());

        config.addList("description", descriptionList, new StringConfig(null), "");

        config.addInt("limit", limit, v -> limit = v, 0, 0, Integer.MAX_VALUE);
        config.addBool("globalLimit", globalLimit, v -> globalLimit = v, false);

        ConfigGroup group = config.getOrCreateSubgroup("dependencies");

        for (AbstractShopEntryCondition tabCondition : tabConditions) {
            tabCondition.getConfig(group);
        }


    }

    public int getIndex() {
        return shop.getShopTabs().indexOf(this);
    }

    public Icon getIcon(){
        if(icon.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(icon);
        }
        return ItemIcon.getItemIcon(icon);
    }

    @Override
    public CompoundTag serializeNBT() {
        return serializeNBT(SDMSerializeParam.SERIALIZE_ALL);
    }

    public CompoundTag serializeNBT(int bits) {
        return ShopSerializerHandler.serializeShopTab(this, bits);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        deserializeNBT(nbt, SDMSerializeParam.SERIALIZE_ALL);
    }

    public void deserializeNBT(CompoundTag nbt, int bits) {
        ShopSerializerHandler.deserializeShopTab(this, nbt, bits);
    }

    public boolean isLocked() {
        if (SDMShopR.isEditMode()) return false;

        if(limit != 0 && LimiterData.CLIENT.TAB_DATA.getOrDefault(shopTabUUID,0) >= limit ) return true;

        for (AbstractShopEntryCondition tabCondition : tabConditions) {
            if(tabCondition.isLocked()) return true;
        }

        return false;
    }
}
