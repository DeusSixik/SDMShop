package net.sdm.sdmshopr.shop.tab;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRIntegration;
import net.sdm.sdmshopr.api.ConditionRegister;
import net.sdm.sdmshopr.api.IShopCondition;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopTab implements INBTSerializable<CompoundTag> {
    public Shop shop;
    public Component title = Component.empty();
    public ItemStack icon = ItemStack.EMPTY;
    public int lock = 0;
    public List<ShopEntry<?>> shopEntryList = new ArrayList<>();


    public final List<IShopCondition> conditions = new ArrayList<>();

    public final List<String> gameStages = new ArrayList<>();

    public ShopTab(Shop shop){
        this.shop = shop;

        for (Map.Entry<String, IShopCondition> d1 : ConditionRegister.CONDITIONS.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())){
                conditions.add(d1.getValue().create());
            }
        }
    }


    public CompoundTag serializeSettings(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("title", title.getString());
        NBTUtils.putItemStack(nbt, "icon", icon);

        for (Map.Entry<String, IShopCondition> d1 : ConditionRegister.CONDITIONS.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())) {
                IShopCondition condition = d1.getValue().create();
                condition.deserializeNBT(nbt);
                conditions.add(condition);
            }
        }

        return nbt;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = serializeSettings();


        ListTag entry = new ListTag();
        for (ShopEntry<?> shopEntry : shopEntryList) {
            entry.add(shopEntry.serializeNBT());
        }
        nbt.put("entries", entry);
        return nbt;
    }

    public void deserializeSettings(CompoundTag nbt){
        title = Component.translatable(nbt.getString("title"));
        icon = NBTUtils.getItemStack(nbt, "icon");

        for (Map.Entry<String, IShopCondition> d1 : ConditionRegister.CONDITIONS.entrySet()) {
            if(ModList.get().isLoaded(d1.getValue().getModID())) {
                IShopCondition condition = d1.getValue().create();
                condition.deserializeNBT(nbt);
                conditions.add(condition);
            }
        }

    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        shopEntryList.clear();
        deserializeSettings(nbt);

        ListTag entries = (ListTag) nbt.get("entries");
        for (Tag entry : entries) {
            ShopEntry<?> d1 = new ShopEntry<>(this);
            d1.deserializeNBT((CompoundTag) entry);
            shopEntryList.add(d1);
        }

    }

    public int getIndex(){
        return shop.shopTabs.indexOf(this);
    }

    public void getConfig(ConfigGroup config){
        TooltipList list = new TooltipList();
        list.add(Component.translatable("sdmr.shop.tab.tittle.info"));

        config.addString("title", title.getString(), v -> title = Component.translatable(v), "");

        config.addItemStack("icon", icon, v -> icon = v, ItemStack.EMPTY, true, true);

        ConfigGroup group = config.getOrCreateSubgroup("dependencies");

        for (IShopCondition condition : conditions) {
            condition.getConfig(group);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        if (SDMShopR.isEditModeClient()) return false;

        for (IShopCondition condition : conditions) {
            if(condition.isLocked()) return true;
        }

        return false;
    }
}
