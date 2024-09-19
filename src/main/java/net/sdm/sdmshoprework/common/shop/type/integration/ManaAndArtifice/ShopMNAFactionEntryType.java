package net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice;

import com.mna.Registries;
import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.faction.IFaction;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.IForgeRegistry;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.utils.NBTUtils;

public class ShopMNAFactionEntryType extends AbstractShopEntryType {

    public String factionID;
    public boolean random = false;
    public boolean needNonFaction = false;
    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
    public ShopMNAFactionEntryType(String factionID){
        this.factionID = factionID;
    }

    protected ShopMNAFactionEntryType(String factionID, boolean random, ItemStack iconPath, boolean needNonFaction){
        this.factionID = factionID;
        this.random = random;
        this.iconPath = iconPath;
        this.needNonFaction = needNonFaction;
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
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addString("mnafactionID", factionID, v -> factionID = v, "mna:none");
        group.addBool("mnaneedNonFaction", needNonFaction, v -> needNonFaction = v, false);
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
        return new ShopMNAFactionEntryType(factionID, random, iconPath, needNonFaction);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.mnafaction");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("factionID", factionID);
        nbt.putBoolean("random", random);
        nbt.putBoolean("needNonFaction", needNonFaction);
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        factionID = nbt.getString("factionID");
        random = nbt.getBoolean("random");
        needNonFaction = nbt.getBoolean("needNonFaction");
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        try {
            IFaction faction = (IFaction) ((IForgeRegistry<?>) Registries.Factions.get()).getValue(new ResourceLocation(factionID));
            IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

            if (faction != null) {
                if (isSell) {
                    if (progression.getAlliedFaction().is(new ResourceLocation(factionID))) {
                        return 1;
                    }
                }

                if (needNonFaction && !progression.getAlliedFaction().is(new ResourceLocation("mna:none"))) return 0;
                long playerMoney = SDMShopR.getMoney(player);
                if(entry.entryPrice == 0) return 1;
                return (int) (playerMoney / entry.entryPrice) >= 1 ? 1 : 0;
            }
        } catch (Exception e){
            SDMShopRework.LOGGER.error(e.toString());
        }

        return 0;
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        IFaction faction = (IFaction)((IForgeRegistry)Registries.Factions.get()).getValue(new ResourceLocation(factionID));
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

        if(faction != null) {
            if (isSell) {
                if (progression.getAlliedFaction().is(new ResourceLocation(factionID))) {
                    return true;
                }
            }

            if (needNonFaction && !progression.getAlliedFaction().is(new ResourceLocation("mna:none"))) return false;
            long playerMoney = SDMShopR.getMoney(player);
            return (int) (playerMoney / entry.entryPrice) > 1 ? true : false;
        }
        return false;
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        IFaction faction = (IFaction)((IForgeRegistry)Registries.Factions.get()).getValue(new ResourceLocation(factionID));
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

        progression.setAlliedFaction(faction, player);
        long playerMoney = SDMShopR.getMoney(player);
        SDMShopR.setMoney(player, playerMoney - entry.entryPrice);
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        IFaction faction = (IFaction)((IForgeRegistry)Registries.Factions.get()).getValue(new ResourceLocation("mna:none"));
        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);

        progression.setAlliedFaction(faction, player);
        long playerMoney = SDMShopR.getMoney(player);
        SDMShopR.setMoney(player, playerMoney + entry.entryPrice);
    }

    @Override
    public String getId() {
        return "mnaFactionType";
    }

    @Override
    public String getModNameForContextMenu() {
        return "Mana And Artifice";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopMNAFactionEntryType("");
        }
    }
}
