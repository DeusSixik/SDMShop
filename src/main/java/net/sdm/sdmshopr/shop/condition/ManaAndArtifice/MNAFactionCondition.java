package net.sdm.sdmshopr.shop.condition.ManaAndArtifice;

import com.mna.Registries;
import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.faction.IFaction;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import net.sdm.sdmshopr.api.IShopCondition;

import java.util.ArrayList;
import java.util.List;

public class MNAFactionCondition implements IShopCondition {
    public List<String> factionID = new ArrayList<>();

    public MNAFactionCondition(){

    }

    protected MNAFactionCondition(List<String> factionID){
        this.factionID = factionID;
    }

    @Override
    public IShopCondition create() {
        return new MNAFactionCondition();
    }

    @Override
    public IShopCondition copy() {
        return new MNAFactionCondition(factionID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isLocked() {
        for (String s : factionID) {
            IFaction faction = (IFaction) ((IForgeRegistry<?>) Registries.Factions.get()).getValue(new ResourceLocation(s));
            IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
            if(faction == null) return false;
            return !progression.getAlliedFaction().is(new ResourceLocation(s));
        }
        return false;
    }

    @Override
    public void getConfig(ConfigGroup config) {
        config.addList("factionID", factionID, new StringConfig(null), "");
    }

    @Override
    public String getID() {
        return "mnaFactionCondition";
    }

    @Override
    public void serializeNBT(CompoundTag nbt) {

        ListTag d1 = new ListTag();
        for (String gameStage : factionID) {
            d1.add(StringTag.valueOf(gameStage));
        }
        nbt.put("factionID", d1);

    }

    @Override
    public String getModID() {
        return "mna";
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("factionID")) {
            factionID.clear();
            ListTag d1 = (ListTag) nbt.get("factionID");
            for (Tag tag : d1) {
                StringTag f1 = (StringTag) tag;
                factionID.add(f1.getAsString());
            }
        }
    }
}
