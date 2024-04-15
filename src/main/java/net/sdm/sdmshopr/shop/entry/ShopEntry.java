package net.sdm.sdmshopr.shop.entry;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftbquests.FTBQuests;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRIntegration;
import net.sdm.sdmshopr.shop.entry.type.IEntryType;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class ShopEntry<T extends IEntryType> implements INBTSerializable<CompoundTag> {
    public ShopTab tab;
    public String tittle;
    public int count;
    public int price;
    public boolean isSell;

    public T type;

    protected final List<String> gameStages = new ArrayList<>();
    protected final List<String> questID = new ArrayList<>();

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

        if(!questID.isEmpty()){
            ListTag d1 = new ListTag();
            for (String gameStage : questID) {
                d1.add(StringTag.valueOf(gameStage));
            }
            nbt.put("questID", d1);
        }
        if(!gameStages.isEmpty()){
            ListTag d1 = new ListTag();
            for (String gameStage : gameStages) {
                d1.add(StringTag.valueOf(gameStage));
            }
            nbt.put("gameStages", d1);
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

        if(nbt.contains("gameStages")){
            gameStages.clear();
            ListTag d1 = (ListTag) nbt.get("gameStages");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                gameStages.add(f1.getAsString());
            }
        }

        if(nbt.contains("questID")){
            questID.clear();
            ListTag d1 = (ListTag) nbt.get("questID");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                questID.add(f1.getAsString());
            }
        }
    }

    public void getConfig(ConfigGroup config){
        config.addString("tittle", tittle, v -> tittle = v, "");
        if(type.isCountable()) config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addInt("price", price, v -> price = v, 1, 0, Integer.MAX_VALUE);
        if(SDMShopRIntegration.FTBQuestLoaded) config.addList("questID", questID, new StringConfig(null), "");
        if(SDMShopRIntegration.GameStagesLoaded) config.addList("gameStages", gameStages, new StringConfig(null), "");
        if(type.isSellable()) config.addBool("isSell", isSell, v -> isSell = v, false);

        ConfigGroup type = config.getGroup("type");
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

    @OnlyIn(Dist.CLIENT)
    public boolean isLocked(){
        if(SDMShopR.isEditModeClient()) return false;

        if(SDMShopRIntegration.GameStagesLoaded){
            for (String gameStage : gameStages) {
                if(!GameStageHelper.hasStage(Minecraft.getInstance().player, gameStage)) return true;
            }
        }
        if(SDMShopRIntegration.FTBQuestLoaded){
            TeamData data = TeamData.get(Minecraft.getInstance().player);
            for (String s : questID) {
                Quest quest = FTBQuests.PROXY.getClientQuestFile().getQuest(ClientQuestFile.parseCodeString(s));
                if(quest != null){
                    if(!data.isCompleted(quest)) return true;
                }
            }
        }
        return type.isLocked();
    }
}
