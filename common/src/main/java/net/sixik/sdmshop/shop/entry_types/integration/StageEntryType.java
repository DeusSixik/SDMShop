package net.sixik.sdmshop.shop.entry_types.integration;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.compat.SDMShopIntegration;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.StagesUtils;

import java.util.ArrayList;
import java.util.List;

public class StageEntryType extends AbstractEntryType {

    protected String stage;

    public StageEntryType(ShopEntry shopEntry) {
        this(shopEntry, "");
    }

    public StageEntryType(ShopEntry shopEntry, String stage) {
        super(shopEntry);
        this.stage = stage;
    }

    @Override
    public AbstractEntryType copy() {
        return new StageEntryType(shopEntry, stage);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        return StagesUtils.addStage(player, stage);
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        return StagesUtils.removeStage(player, stage);
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        return StagesUtils.hasStage(player, stage);
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        if(entry.getType().isSell())
            return StagesUtils.hasStage(player, stage) ? 1 : 0;

        return !StagesUtils.hasStage(player, stage) ? 0 : 1;
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.integration.gamestage");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdm.shop.entry.creator.type.integration.gamestage.description"));
        return list;
    }

    @Override
    public String getModNameForContextMenu() {
        return "Game Stages";
    }

    @Override
    public Icon getCreativeIcon() {
        return Icons.CONTROLLER;
    }

    @Override
    public void getConfig(ConfigGroup group) {

    }

    @Override
    public String getModId() {
        return "gamestages";
    }

    @Override
    public boolean isModLoaded() {
        return Platform.isModLoaded(getModId()) || SDMShopIntegration.isKubeJSLoaded();
    }

    @Override
    public String getId() {
        return "stageType";
    }

    @Override
    public boolean isSearch(String search) {
        return false;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("gameStage", stage);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        stage = nbt.getString("gameStage");
    }
}
