package net.sixik.sdmshoprework.common.shop.type;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.utils.StructureUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ShopLocateBetaEntryType extends AbstractShopEntryType {

    public ResourceLocation location;
    public Type type;

    public ShopLocateBetaEntryType(ResourceLocation location, Type type){
        this.location = location;
        this.type = type;
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            try {
                switch (type) {
                    case STRUCTURE -> locateStructure(serverPlayer);
//                case POI -> {}
                    case BIOME -> locateBiome(serverPlayer);
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public SellType getSellType() {
        return SellType.ONLY_BUY;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        return Icons.ART;
    }

    private void locateBiome(ServerPlayer player){
        ResourceKey<Biome> resourceKey = null;
        Registry<Biome> registry = player.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);

        for (Map.Entry<ResourceKey<Biome>, Biome> entry : registry.entrySet()) {
            if(registry.getKey(entry.getValue()).toString().equals(location.toString())){
                resourceKey = entry.getKey();
            }
        }

        if(resourceKey == null || registry.getHolder(resourceKey).isEmpty()) return;

        BlockPos blockpos = new BlockPos(player.position());
        Holder<Holder<Biome>> featureHolderSet = registry.getHolder(resourceKey).map(Holder::direct).orElse(null);
        Predicate<Holder<Biome>> b = v -> v == featureHolderSet.value();
        Pair<BlockPos, Holder<Biome>> pair = ((ServerLevel) player.level).findClosestBiome3d(b, blockpos, 6400, 32, 64);
        if (pair == null) {
            player.sendSystemMessage(Component.literal("Biome not founded ! Are you exactly in the dimension where she might be?"));
        } else {
            String f = "x = " + pair.getFirst().getX() + ",  z = " + pair.getFirst().getZ();
            player.sendSystemMessage(Component.literal("The biome is located at the following coordinates " + f));
        }
    }

    private void locateStructure(ServerPlayer player) throws CommandSyntaxException {

        ResourceKey<Structure> resourceKey = null;

        Registry<Structure> registry = player.level.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);

        for (Map.Entry<ResourceKey<Structure>, Structure> resourceKeyStructureEntry : registry.entrySet()) {
            if(registry.getKey(resourceKeyStructureEntry.getValue()).toString().equals(location.toString())) {
                resourceKey = resourceKeyStructureEntry.getKey();
                break;
            }
        }

        if(resourceKey == null) return;

        if(registry.getHolder(resourceKey).isEmpty()) return;

        BlockPos blockpos = new BlockPos(player.position());
        ServerLevel serverlevel = (ServerLevel) player.level;

        HolderSet<Structure> featureHolderSet = registry.getHolder(resourceKey).map(HolderSet::direct).orElse(null);
        Pair<BlockPos, Holder<Structure>> pair = StructureUtil.findNearestMapStructure(serverlevel, featureHolderSet, blockpos, 100, false);
        if (pair == null) {
            player.sendSystemMessage(Component.literal("Structure not founded ! Are you exactly in the dimension where she might be?"));
        } else {
            String f = "x = " + pair.getFirst().getX() + ", y = ?,  z = " + pair.getFirst().getZ();
            player.sendSystemMessage(Component.literal("The structure is located at the following coordinates " + f));
        }
    }


    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("locate_id", location.toString(), v -> location = new ResourceLocation(v), "minecraft:iglooe");
        group.addEnum("locate_type", type.toString(), v -> type = Type.valueOf(v), getIDs());
    }

    public NameMap<String> getIDs(){
        List<String> ids = new ArrayList<>();
        for (Type value : Type.values()) {
            ids.add(value.name());
        }
        return NameMap.of(Type.BIOME.name(), ids).create();
    }

    @Override
    public Icon getCreativeIcon() {
        return Icons.ART;
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopLocateBetaEntryType(location, type);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.locationtype");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("locate_id", location.toString());
        nbt.putString("locate_type", type.name());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.location = new ResourceLocation(nbt.getString("locate_id"));
        this.type = Type.valueOf(nbt.getString("locate_type"));
    }

    @Override
    public String getId() {
        return "locateType";
    }

    public enum Type {
        BIOME,
        STRUCTURE;
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopLocateBetaEntryType(new ResourceLocation("minecraft:iglooe"), Type.BIOME);
        }
    }
}
