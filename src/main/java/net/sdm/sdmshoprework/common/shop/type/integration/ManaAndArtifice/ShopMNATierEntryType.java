package net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.entities.construct.IConstruct;
import com.mna.capabilities.playerdata.progression.PlayerProgression;
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
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.common.ftb.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.utils.NBTUtils;

import java.util.List;

public class ShopMNATierEntryType extends AbstractShopEntryType {

    public int tierID;
    public boolean random = false;
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public boolean resetProgression = false;
    public ShopMNATierEntryType(int tierID){
        this.tierID = tierID;
    }
    protected ShopMNATierEntryType(int tierID, boolean random, ItemStack iconPath, boolean resetProgression){
        this.tierID = tierID;
        this.random = random;
        this.iconPath = iconPath;
        this.resetProgression = resetProgression;
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
    public void getConfig(ConfigGroup group) {
        group.addInt("mnatierID", tierID, v -> tierID = v, 1, 1, PlayerProgression.MAX_TIERS);
        group.addBool("mnarandom", random, v -> random = v, false);

        group.addBool("mnaresetProgression", resetProgression, v -> resetProgression = v, false);

        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("mna:textures/item/eldrin_sight_unguent.png");
    }

    @Override
    public String getModId() {
        return "mna";
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopMNATierEntryType(tierID, random, iconPath, resetProgression);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.mnatier");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("tierID", tierID);
        nbt.putBoolean("random", random);
        nbt.putBoolean("resetProgression", resetProgression);
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        tierID = nbt.getInt("tierID");
        random = nbt.getBoolean("random");
        resetProgression = nbt.getBoolean("resetProgression");
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
    }

    @Override
    public String getId() {
        return "mnaTierType";
    }

    @Override
    public String getModNameForContextMenu() {
        return "Mana And Artifice";
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(isSell){
            IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

            if(progression.getTier() != tierID) return 0;
            return 1;
        }

        long playerMoney = SDMShopR.getMoney(player);
        return (int) (playerMoney / entry.entryPrice) > 1 ? 1 : 0;
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

        if(!isSell) {
            if (SDMShopR.getMoney(player) < entry.entryPrice) return false;
            if (progression.getTier() != PlayerProgression.MAX_TIERS && progression.getTier() < tierID) return true;
            return false;
        } else {
            if(progression.getTier() == tierID) return true;
            return false;
        }
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
        List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(player.level(), progression.getTier());

        if(progression.getTier() < tierID && progression.getTier() != PlayerProgression.MAX_TIERS){
            if(random){
                RandomSource source = RandomSource.create();
                int d1 = source.nextInt(progression.getTier(), PlayerProgression.MAX_TIERS);
                progression.setTier(d1, player);
            } else progression.setTier(tierID, player);
            long money = SDMShopR.getMoney(player);
            SDMShopR.setMoney(player, money - ((long) entry.entryPrice));
        }
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);


        if(progression.getTier() == tierID && progression.getTier() > 1){
            progression.setTier(progression.getTier() - 1, player);

            List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(player.level(), progression.getTier() + 1);
            if(resetProgression) {
                for (ProgressionCondition condition : progressionConditionList) {
                    progression.getCompletedProgressionSteps().remove(condition.getId());
                }
                progressionConditionList = ProgressionCondition.get(player.level(), progression.getTier());
                for (ProgressionCondition condition : progressionConditionList) {
                    progression.getCompletedProgressionSteps().remove(condition.getId());
                }
            }
        }
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {
        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopMNATierEntryType(0);
        }
    }
}
