package net.sixik.sdmshoprework.forge.shop.condition.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryCondition;

public class ShopMNATierCondition extends AbstractShopEntryCondition {
    public int minLevel;
    public int maxLevel;
    public boolean useTierCondition = false;
    public ShopMNATierCondition(){

    }

    public ShopMNATierCondition(int minLevel, int maxLevel, boolean useTierCondition){
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.useTierCondition = useTierCondition;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        if(useTierCondition) {
            IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

            boolean any = false;
            if(maxLevel == 0) any = true;

            if(any){
                return !(progression.getTier() >= minLevel);
            }

            return !(progression.getTier() >= minLevel && progression.getTier() <= maxLevel);
        }
        return false;
    }

    @Override
    public AbstractShopEntryCondition copy() {
        return new ShopMNATierCondition(minLevel,maxLevel,useTierCondition);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        ConfigGroup group = config.getOrCreateSubgroup("mnatier");
        group.addInt("minLevel", minLevel, v -> minLevel = v, 0, 0, PlayerProgression.MAX_TIERS);
        group.addInt("maxLevel", maxLevel, v -> maxLevel = v, 0, 0, PlayerProgression.MAX_TIERS);
        group.addBool("useTierCondition", useTierCondition, v -> useTierCondition = v, false);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        CompoundTag data = new CompoundTag();
        data.putInt("minLevel", minLevel);
        data.putInt("maxLevel", maxLevel);
        data.putBoolean("useTierCondition", useTierCondition);
        nbt.put("mnaTier", data);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound("mnaTier");
        minLevel = data.getInt("minLevel");
        maxLevel = data.getInt("maxLevel");
        useTierCondition = data.getBoolean("useTierCondition");
    }

    @Override
    public String getModId() {
        return "mna";
    }

    @Override
    public String getId() {
        return "mnaTierCondition";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryCondition> {
        @Override
        public AbstractShopEntryCondition createDefaultInstance() {
            return new ShopMNATierCondition();
        }
    }

}
