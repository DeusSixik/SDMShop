package net.sixik.sdmshoprework.forge.shop.type.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.recipes.progression.ProgressionCondition;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

import java.util.List;

public class ShopMNAProgressionEntryType extends AbstractShopEntryType {

    public int progressionID;
    public boolean random = false;
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public ShopMNAProgressionEntryType(int progressionID){
        this.progressionID = progressionID;
    }

    protected ShopMNAProgressionEntryType(int progressionID, boolean random, ItemStack iconPath){
        this.progressionID = progressionID;
        this.random = random;
        this.iconPath = iconPath;
    }

    @Override
    public String getModId() {
        return "mna";
    }

    @Override
    public SellType getSellType() {
        return SellType.ONLY_BUY;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
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
    public AbstractShopEntryType copy() {
        return new ShopMNAProgressionEntryType(progressionID, random, iconPath);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.mnaprogression");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
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
    public String getId() {
        return "mnaProgressionType";
    }

    @Override
    public String getModNameForContextMenu() {
        return "Mana And Artifice";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        long playerMoney = SDMShopR.getMoney(player);
        return (int) (playerMoney / entry.entryPrice) >= 1 ? 1 : 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if(SDMShopR.getMoney(player) < entry.entryPrice) return false;

        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
        List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(player.level(), progression.getTier());
        for (ProgressionCondition condition : progressionConditionList) {
            if(!progression.getCompletedProgressionSteps().contains(condition.getId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
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
                    SDMShopR.setMoney(player, money - ((long) entry.entryPrice));
                    return;
                } else notFound++;
            }
        } else {
            ProgressionCondition condition = progressionConditionList.get(progressionID);
            if(!progression.getCompletedProgressionSteps().contains(condition.getId())){
                progression.addTierProgressionComplete(condition.getId());
                long money = SDMShopR.getMoney(player);
                SDMShopR.setMoney(player, money - ((long) entry.entryPrice));

            }
        }
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopMNAProgressionEntryType(0);
        }
    }
}
