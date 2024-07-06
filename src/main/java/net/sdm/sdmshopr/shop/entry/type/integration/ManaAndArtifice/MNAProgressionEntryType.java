package net.sdm.sdmshopr.shop.entry.type.integration.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.recipes.progression.ProgressionCondition;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.List;

public class MNAProgressionEntryType implements IEntryType {

    public int progressionID;
    public boolean random = false;
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public MNAProgressionEntryType(int progressionID){
        this.progressionID = progressionID;
    }

    protected MNAProgressionEntryType(int progressionID, boolean random, ItemStack iconPath){
        this.progressionID = progressionID;
        this.random = random;
        this.iconPath = iconPath;
    }

    @Override
    public String getModID() {
        return "mna";
    }

    @Override
    public boolean isSellable() {
        return false;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup group) {
        IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
        List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(Minecraft.getInstance().player.level(), progression.getTier());
        group.addInt("mnaprogressionID", progressionID, v -> progressionID = v, 1, 1, progressionConditionList.size());
        group.addBool("mnarandom", random, v -> random = v, false);
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("mna:textures/item/eldrin_sight_unguent.png");
    }

    @Override
    public String getID() {
        return "mnaProgressionType";
    }

    @Override
    public IEntryType copy() {
        return new MNAProgressionEntryType(progressionID, random, iconPath);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.mnaprogression");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();
        nbt.putInt("progressionID", progressionID);
        nbt.putBoolean("random", random);
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        progressionID = nbt.getInt("progressionID");
        random = nbt.getBoolean("random");
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
    }

    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        long playerMoney = SDMShopR.getClientMoney();
        return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(SDMShopR.getClientMoney() < entry.price) return false;

        IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
        List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(Minecraft.getInstance().player.level(), progression.getTier());
        for (ProgressionCondition condition : progressionConditionList) {
            if(!progression.getCompletedProgressionSteps().contains(condition.getId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getModNameForContextMenu() {
        return "Mana And Artifice";
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
        List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(player.level(), progression.getTier());
        if(random){
            RandomSource source = RandomSource.create();
            int notFound = 0;
            while (true){
                if(notFound >= 40) return;

                int randomInt = source.nextInt(progressionConditionList.size());
                ProgressionCondition condition = progressionConditionList.get(randomInt);
                if(!progression.getCompletedProgressionSteps().contains(condition.getId())){
                    progression.addTierProgressionComplete(condition.getId());

                    long money = SDMShopR.getMoney(player);
                    SDMShopR.setMoney(player, money - ((long) entry.price));
                    return;
                } else notFound++;
            }
        } else {
            ProgressionCondition condition = progressionConditionList.get(progressionID);
            if(!progression.getCompletedProgressionSteps().contains(condition.getId())){
                progression.addTierProgressionComplete(condition.getId());
                long money = SDMShopR.getMoney(player);
                SDMShopR.setMoney(player, money - ((long) entry.price));

            }
        }
    }
}
