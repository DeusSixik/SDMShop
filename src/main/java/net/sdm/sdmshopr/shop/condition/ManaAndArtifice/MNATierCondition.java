package net.sdm.sdmshopr.shop.condition.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import com.mna.capabilities.playerdata.progression.PlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.sdm.sdmshopr.api.IShopCondition;

import javax.print.DocFlavor;

public class MNATierCondition implements IShopCondition {

    public int minLevel;
    public int maxLevel;
    public boolean useTierCondition = false;
    public MNATierCondition(){

    }

    public MNATierCondition(int minLevel, int maxLevel, boolean useTierCondition){
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.useTierCondition = useTierCondition;
    }

    @Override
    public IShopCondition create() {
        return new MNATierCondition();
    }

    @Override
    public IShopCondition copy() {
        return new MNATierCondition(minLevel,maxLevel,useTierCondition);
    }

    @Override
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
    public void getConfig(ConfigGroup config) {
        ConfigGroup group = config.getOrCreateSubgroup("mnatier");
        group.addInt("minLevel", minLevel, v -> minLevel = v, 0, 0, PlayerProgression.MAX_TIERS);
        group.addInt("maxLevel", maxLevel, v -> maxLevel = v, 0, 0, PlayerProgression.MAX_TIERS);
        group.addBool("useTierCondition", useTierCondition, v -> useTierCondition = v, false);
    }

    @Override
    public String getModID() {
        return "mna";
    }

    @Override
    public String getID() {
        return "mnaTierCondition";
    }

    @Override
    public void serializeNBT(CompoundTag nbt) {
        CompoundTag data = new CompoundTag();
        data.putInt("minLevel", minLevel);
        data.putInt("maxLevel", maxLevel);
        data.putBoolean("useTierCondition", useTierCondition);
        nbt.put("mnaTier", data);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("mnaTier")){
            CompoundTag data = nbt.getCompound("mnaTier");
            minLevel = data.getInt("minLevel");
            maxLevel = data.getInt("maxLevel");
            useTierCondition = data.getBoolean("useTierCondition");
        }
    }
}
