//package net.sdm.sdmshopr.shop.entry.type.integration.ManaAndArtifice;
//
//import com.mna.Registries;
//import com.mna.api.capabilities.IPlayerProgression;
//import com.mna.api.faction.IFaction;
//import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
//import dev.ftb.mods.ftblibrary.config.ConfigGroup;
//import dev.ftb.mods.ftblibrary.icon.Icon;
//import dev.ftb.mods.ftblibrary.icon.Icons;
//import net.minecraft.client.Minecraft;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.registries.IForgeRegistry;
//import net.sdm.sdmshopr.SDMShopR;
//import net.sdm.sdmshopr.shop.entry.ShopEntry;
//import net.sdm.sdmshopr.api.IEntryType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MNAFactionEntryType implements IEntryType {
//    public String factionID;
//    public boolean random = false;
//    public boolean needNonFaction = false;
//    private String iconPath = "minecraft:item/barrier";
//    public MNAFactionEntryType(String factionID){
//        this.factionID = factionID;
//    }
//
//    protected MNAFactionEntryType(String factionID, boolean random, String iconPath, boolean needNonFaction){
//        this.factionID = factionID;
//        this.random = random;
//        this.iconPath = iconPath;
//        this.needNonFaction = needNonFaction;
//    }
//
//
//    @Override
//    public boolean isSellable() {
//        return true;
//    }
//
//    @Override
//    public boolean isCountable() {
//        return false;
//    }
//
//    @Override
//    public Icon getIcon() {
//        Icon getted = Icon.getIcon(iconPath);
//        if(getted.isEmpty()) return Icons.BARRIER;
//        return getted;
//    }
//
//    @Override
//    public void getConfig(ConfigGroup group) {
//        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
//        group.addString("mnafactionID", factionID, v -> factionID = v, "mna:none");
//        group.addBool("mnaneedNonFaction", needNonFaction, v -> needNonFaction = v, false);
//    }
//
//    @Override
//    public Icon getCreativeIcon() {
//        return Icon.getIcon("mna:textures/item/eldrin_sight_unguent.png");
//    }
//
//    @Override
//    public String getID() {
//        return "mnaFactionType";
//    }
//
//    @Override
//    public String getModID() {
//        return "mna";
//    }
//
//    @Override
//    public IEntryType copy() {
//        return new MNAFactionEntryType(factionID, random, iconPath, needNonFaction);
//    }
//
//    @Override
//    public Component getTranslatableForContextMenu() {
//        return Component.translatable("sdm.shop.entry.add.context.mnafaction");
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag nbt = IEntryType.super.serializeNBT();
//        nbt.putString("factionID", factionID);
//        nbt.putBoolean("random", random);
//        nbt.putBoolean("needNonFaction", needNonFaction);
//        nbt.putString("iconPath", iconPath);
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        factionID = nbt.getString("factionID");
//        random = nbt.getBoolean("random");
//        needNonFaction = nbt.getBoolean("needNonFaction");
//        iconPath = nbt.getString("iconPath");
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public int howMany(boolean isSell, ShopEntry<?> entry) {
//        try {
//            IFaction faction = (IFaction) ((IForgeRegistry<?>) Registries.Factions.get()).getValue(new ResourceLocation(factionID));
//            IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
//
//            if (faction != null) {
//                if (isSell) {
//                    if (progression.getAlliedFaction().is(new ResourceLocation(factionID))) {
//                        return 1;
//                    }
//                }
//
//                if (needNonFaction && !progression.getAlliedFaction().is(new ResourceLocation("mna:none"))) return 0;
//                long playerMoney = SDMShopR.getClientMoney();
//                return (int) (playerMoney / entry.price) > 1 ? 1 : 0;
//            }
//        } catch (Exception e){
//            SDMShopR.LOGGER.error(e.toString());
//        }
//
//        return 0;
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
//        IFaction faction = (IFaction)((IForgeRegistry)Registries.Factions.get()).getValue(new ResourceLocation(factionID));
//        IPlayerProgression progression = (IPlayerProgression) Minecraft.getInstance().player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
//
//        if(faction != null) {
//            if (isSell) {
//                if (progression.getAlliedFaction().is(new ResourceLocation(factionID))) {
//                    return true;
//                }
//            }
//
//            if (needNonFaction && !progression.getAlliedFaction().is(new ResourceLocation("mna:none"))) return false;
//            long playerMoney = SDMShopR.getClientMoney();
//            return (int) (playerMoney / entry.price) > 1 ? true : false;
//        }
//        return false;
//    }
//
//    @Override
//    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
//        IFaction faction = (IFaction)((IForgeRegistry)Registries.Factions.get()).getValue(new ResourceLocation(factionID));
//        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
//
//        progression.setAlliedFaction(faction, player);
//        long playerMoney = SDMShopR.getMoney(player);
//        SDMShopR.setMoney(player, playerMoney - entry.price);
//
//    }
//
//    @Override
//    public String getModNameForContextMenu() {
//        return "Mana And Artifice";
//    }
//
//    @Override
//    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
//        IFaction faction = (IFaction)((IForgeRegistry)Registries.Factions.get()).getValue(new ResourceLocation("mna:none"));
//        IPlayerProgression progression = (IPlayerProgression) player.getCapability(PlayerProgressionProvider.PROGRESSION).orElse((IPlayerProgression) null);
//
//        progression.setAlliedFaction(faction, player);
//        long playerMoney = SDMShopR.getMoney(player);
//        SDMShopR.setMoney(player, playerMoney + entry.price);
//    }
//}
