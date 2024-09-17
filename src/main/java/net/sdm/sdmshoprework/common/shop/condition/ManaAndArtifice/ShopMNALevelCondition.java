package net.sdm.sdmshoprework.common.shop.condition.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryCondition;

public class ShopMNALevelCondition extends AbstractShopEntryCondition {

    public int minLevel;
    public int maxLevel;
    public boolean useLevelCondition = false;

    public ShopMNALevelCondition(){

    }

    public ShopMNALevelCondition(int minLevel, int maxLevel, boolean useLevelCondition){
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.useLevelCondition = useLevelCondition;
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        if(useLevelCondition) {
            IPlayerMagic playerMagic = (IPlayerMagic) Minecraft.getInstance().player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
            boolean anyLevel = false;
            if (maxLevel == 0) anyLevel = true;

            if (anyLevel) {
                return !(playerMagic.getMagicLevel() >= minLevel);
            }

            return !(playerMagic.getMagicLevel() >= minLevel && playerMagic.getMagicLevel() <= maxLevel);
        }
        return false;
    }

    @Override
    public AbstractShopEntryCondition copy() {
        return new ShopMNALevelCondition(minLevel, maxLevel, useLevelCondition);
    }

    @Override
    public void getConfig(ConfigGroup config) {
        ConfigGroup group = config.getOrCreateSubgroup("mnalevel");
        group.addInt("minLevel", minLevel, v -> minLevel = v, 0, 1, 74);
        group.addInt("maxLevel", maxLevel, v -> maxLevel = v, 0, 1, 75);
        group.addBool("useLevelCondition", useLevelCondition, v -> useLevelCondition = v, false);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        CompoundTag data = new CompoundTag();
        data.putInt("minLevel", minLevel);
        data.putInt("maxLevel", maxLevel);
        data.putBoolean("useLevelCondition", useLevelCondition);
        nbt.put("mnaLevel", data);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound("mnaLevel");
        minLevel = data.getInt("minLevel");
        maxLevel = data.getInt("maxLevel");
        useLevelCondition = data.getBoolean("useLevelCondition");
    }

    @Override
    public String getModId() {
        return "mna";
    }

    @Override
    public String getId() {
        return "mnaLevelCondition";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryCondition> {
        @Override
        public AbstractShopEntryCondition createDefaultInstance() {
            return new ShopMNALevelCondition();
        }
    }

}
