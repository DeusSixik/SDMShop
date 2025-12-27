package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

public class XPLevelEntryType extends AbstractEntryType implements CustomIcon {

    protected int xpLevel;

    public XPLevelEntryType(ShopEntry shopEntry) {
        this(shopEntry, 1);
    }

    public XPLevelEntryType(ShopEntry shopEntry, int xpLevel) {
        super(shopEntry);
        this.xpLevel = xpLevel;
    }

    @Override
    public AbstractEntryType copy() {
        return new XPLevelEntryType(shopEntry, xpLevel);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.setExperienceLevels(player.experienceLevel + this.xpLevel * countBuy);
            return true;
        }

        return false;
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.setExperienceLevels(player.experienceLevel - this.xpLevel * countBuy);
            return true;
        }

        return false;
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        if(entry.getType().isSell())
            return player.experienceLevel >= countBuy * this.xpLevel;

        double money = entry.getEntrySellerType().getMoney(player, entry);
        return money >= entry.getPrice() * countBuy;
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        if(entry.getType().isSell())
            return player.totalExperience != 0 && this.xpLevel != 0 ? player.experienceLevel / this.xpLevel : 0;

        double money = entry.getEntrySellerType().getMoney(player, entry);
        return entry.getPrice() == 0.0 ? 127 : (int) (money / entry.getPrice());
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.xp_level");
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addInt("xp_level", this.xpLevel, (v) -> this.xpLevel = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public String getId() {
        return "xpLevelType";
    }

    @Override
    public boolean isSearch(String search) {
        boolean find = search.contains("xp") || search.contains("level");
        if(find && search.contains(String.valueOf(xpLevel)))
            return true;

        return find;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("level", this.xpLevel);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        if(nbt.contains("level"))
            this.xpLevel = nbt.getInt("level");
    }

    @Override
    public @Nullable Icon getCustomIcon(ShopEntry entry, int tick) {
        if(entry.getRenderComponent().getIcon().isEmpty())
            return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);

        return null;
    }

    @Override
    public void addEntryTooltip(TooltipList list, ShopEntry entry) {
        list.add(Component.translatable("sdm.shop.entry.info.xp_level_entry", Component.literal(String.valueOf(this.xpLevel)).withStyle(ChatFormatting.GREEN)));
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
    }
}
