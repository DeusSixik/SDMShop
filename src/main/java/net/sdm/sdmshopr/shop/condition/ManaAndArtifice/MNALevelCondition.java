package net.sdm.sdmshopr.shop.condition.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.api.IShopCondition;

public class MNALevelCondition implements IShopCondition {

    public int minLevel;
    public int maxLevel;
    public boolean useLevelCondition = false;

    public MNALevelCondition(){

    }

    public MNALevelCondition(int minLevel, int maxLevel, boolean useLevelCondition){
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.useLevelCondition = useLevelCondition;
    }

    @Override
    public IShopCondition create() {
        return new MNALevelCondition();
    }

    @Override
    public IShopCondition copy() {
        return new MNALevelCondition(minLevel, maxLevel, useLevelCondition);
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
    public void getConfig(ConfigGroup config) {
        ConfigGroup group = config.getGroup("mnalevel");
        group.addInt("minLevel", minLevel, v -> minLevel = v, 0, 1, 74);
        group.addInt("maxLevel", maxLevel, v -> maxLevel = v, 0, 1, 75);
        group.addBool("useLevelCondition", useLevelCondition, v -> useLevelCondition = v, false);
    }

    @Override
    public String getModID() {
        return "mna";
    }

    @Override
    public String getID() {
        return "mnaLevelCondition";
    }

    @Override
    public void serializeNBT(CompoundTag nbt) {
        CompoundTag data = new CompoundTag();
        data.putInt("minLevel", minLevel);
        data.putInt("maxLevel", maxLevel);
        data.putBoolean("useLevelCondition", useLevelCondition);
        nbt.put("mnaLevel", data);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("mnaLevel")){
            CompoundTag data = nbt.getCompound("mnaLevel");
            minLevel = data.getInt("minLevel");
            maxLevel = data.getInt("maxLevel");
            useLevelCondition = data.getBoolean("useLevelCondition");
        }
    }
}
