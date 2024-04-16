package net.sdm.sdmshopr.shop.entry.type.integration.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.recipes.progression.ProgressionCondition;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;

import java.util.List;

public class MNATierEntryType implements IEntryType {

    public int tierID;
    public boolean random = false;
    private String iconPath = "minecraft:item/barrier";
    public boolean resetProgression = false;
    public MNATierEntryType(int tierID){
        this.tierID = tierID;
    }
    protected MNATierEntryType(int tierID, boolean random, String iconPath, boolean resetProgression){
        this.tierID = tierID;
        this.random = random;
        this.iconPath = iconPath;
        this.resetProgression = resetProgression;
    }

    @Override
    public boolean isSellable() {
        return true;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        Icon getted = Icon.getIcon(iconPath);
        if(getted.isEmpty()) return Icons.BARRIER;
        return getted;
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addInt("mnatierID", tierID, v -> tierID = v, 1, 1, PlayerProgression.MAX_TIERS);
        group.addBool("mnarandom", random, v -> random = v, false);

        group.addBool("mnaresetProgression", resetProgression, v -> resetProgression = v, false);

        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("mna:textures/item/eldrin_sight_unguent.png");
    }

    @Override
    public String getModID() {
        return "mna";
    }

    @Override
    public String getID() {
        return "mnaTierType";
    }

    @Override
    public IEntryType copy() {
        return new MNATierEntryType(tierID, random, iconPath, resetProgression);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.mnatier");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();
        nbt.putInt("tierID", tierID);
        nbt.putBoolean("random", random);
        nbt.putBoolean("resetProgression", resetProgression);
        nbt.putString("iconPath", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        tierID = nbt.getInt("tierID");
        random = nbt.getBoolean("random");
        resetProgression = nbt.getBoolean("resetProgression");
        iconPath = nbt.getString("iconPath");
    }

    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell){
            IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

            if(progression.getTier() != tierID) return 0;
            return 1;
        }

        long playerMoney = SDMShopR.getClientMoney();
        return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
    }

    @Override
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

        if(!isSell) {
            if (SDMShopR.getClientMoney() < entry.price) return false;
            if (progression.getTier() != PlayerProgression.MAX_TIERS && progression.getTier() < tierID) return true;
            return false;
        } else {
            if(progression.getTier() == tierID) return true;
            return false;
        }

    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
        List<ProgressionCondition> progressionConditionList = ProgressionCondition.get(player.level(), progression.getTier());

        if(progression.getTier() < tierID && progression.getTier() != PlayerProgression.MAX_TIERS){
            if(random){
                RandomSource source = RandomSource.create();
                int d1 = source.nextInt(progression.getTier(), PlayerProgression.MAX_TIERS);
                progression.setTier(d1, player);
            } else progression.setTier(tierID, player);
            long money = SDMShopR.getMoney(player);
            SDMShopR.setMoney(player, money - ((long) entry.price));
        }
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
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
}
