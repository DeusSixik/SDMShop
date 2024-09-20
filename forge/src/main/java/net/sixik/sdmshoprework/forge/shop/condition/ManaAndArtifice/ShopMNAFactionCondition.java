package net.sixik.sdmshoprework.forge.shop.condition.ManaAndArtifice;

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
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryCondition;

import java.util.ArrayList;
import java.util.List;

public class ShopMNAFactionCondition extends AbstractShopEntryCondition {

    public List<String> factionID = new ArrayList<>();

    public ShopMNAFactionCondition(){

    }

    protected ShopMNAFactionCondition(List<String> factionID){
        this.factionID = factionID;
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
    public AbstractShopEntryCondition copy() {
        return new ShopMNAFactionCondition(factionID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        config.addList("factionID", factionID, new StringConfig(null), "");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        ListTag d1 = new ListTag();
        for (String gameStage : factionID) {
            d1.add(StringTag.valueOf(gameStage));
        }
        nbt.put("factionID", d1);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        factionID.clear();
        ListTag d1 = (ListTag) nbt.get("factionID");
        for (Tag tag : d1) {
            StringTag f1 = (StringTag) tag;
            factionID.add(f1.getAsString());
        }
    }

    @Override
    public String getModId() {
        return "mna";
    }

    @Override
    public String getId() {
        return "mnaFactionCondition";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryCondition> {
        @Override
        public AbstractShopEntryCondition createDefaultInstance() {
            return new ShopMNAFactionCondition();
        }
    }
}
