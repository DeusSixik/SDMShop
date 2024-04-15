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
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class ShopTab implements INBTSerializable<CompoundTag> {
    public Shop shop;
    public Component title = Component.empty();
    public ItemStack icon = ItemStack.EMPTY;
    public int lock = 0;
    public List<ShopEntry<?>> shopEntryList = new ArrayList<>();

    protected final List<String> gameStages = new ArrayList<>();
    protected final List<String> questID = new ArrayList<>();

    public ShopTab(Shop shop){
        this.shop = shop;
    }


    public CompoundTag serializeSettings(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("title", title.getString());
        NBTUtils.putItemStack(nbt, "icon", icon);

        if(SDMShopRIntegration.FTBQuestLoaded && !questID.isEmpty()){
            ListTag d1 = new ListTag();
            for (String gameStage : questID) {
                d1.add(StringTag.valueOf(gameStage));
            }
            nbt.put("questID", d1);
        }
        if(SDMShopRIntegration.GameStagesLoaded && !gameStages.isEmpty()){
            ListTag d1 = new ListTag();
            for (String gameStage : gameStages) {
                d1.add(StringTag.valueOf(gameStage));
            }
            nbt.put("gameStages", d1);
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

        if(SDMShopRIntegration.GameStagesLoaded && nbt.contains("gameStages")){
            gameStages.clear();
            ListTag d1 = (ListTag) nbt.get("gameStages");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                gameStages.add(f1.getAsString());
            }
        }

        if(SDMShopRIntegration.FTBQuestLoaded && nbt.contains("questID")) {
            questID.clear();
            ListTag d1 = (ListTag) nbt.get("questID");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                questID.add(f1.getAsString());
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


        if(SDMShopRIntegration.FTBQuestLoaded) config.addList("questID", questID, new StringConfig(null), "");

        if(SDMShopRIntegration.GameStagesLoaded) config.addList("gameStages", gameStages, new StringConfig(null), "");
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        if (SDMShopR.isEditModeClient()) return false;

        if (SDMShopRIntegration.GameStagesLoaded) {
            for (String gameStage : gameStages) {
                if (!GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return true;
            }
        }
        if (SDMShopRIntegration.FTBQuestLoaded) {
            TeamData data = TeamData.get(Minecraft.getInstance().player);
            for (String s : questID) {
                Quest quest = FTBQuestsClient.getClientQuestFile().getQuest(ClientQuestFile.parseCodeString(s));
                if (quest != null) {
                    if (!data.isCompleted(quest)) return true;
                }
            }
        }
        return false;
    }
}
