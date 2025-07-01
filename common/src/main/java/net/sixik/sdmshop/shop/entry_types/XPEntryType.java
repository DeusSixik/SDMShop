package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.api.CustomIcon;
import net.sixik.sdmshop.api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

public class XPEntryType extends AbstractEntryType implements CustomIcon {

    protected int xpCount;

    public XPEntryType(ShopEntry shopEntry) {
        this(shopEntry, 1);
    }

    public XPEntryType(ShopEntry shopEntry, int xpCount) {
        super(shopEntry);
        this.xpCount = xpCount;
    }

    @Override
    public AbstractEntryType copy() {
        return new XPEntryType(shopEntry, xpCount);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        double money = entry.getEntrySellerType().getMoney(player, entry);
        if(entry.getPrice() * countBuy > money) return false;
        int experience = ShopUtils.getPlayerXP(player) + this.xpCount * countBuy;
        player.totalExperience = experience;
        player.experienceLevel = ShopUtils.getLevelForExperience(experience);
        int expForLevel = ShopUtils.getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float)(experience - expForLevel) / (float)player.getXpNeededForNextLevel();
        return false;
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        int experience = ShopUtils.getPlayerXP(player) - this.xpCount * countBuy;
        player.totalExperience = experience;
        player.experienceLevel = ShopUtils.getLevelForExperience(experience);
        int expForLevel = ShopUtils.getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float)(experience - expForLevel) / (float)player.getXpNeededForNextLevel();
        return true;
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        double money = shopEntry.getEntrySellerType().getMoney(player, entry);
        return entry.getPrice() * countBuy <= money && howMany(player, entry) > 0;
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        if(entry.getType().isSell())
            return player.totalExperience != 0 && this.xpCount != 0 ? player.totalExperience / this.xpCount : 0;

        return entry.getPrice() == 0 ? 127 : (int) (shopEntry.getEntrySellerType().getMoney(player, entry) / entry.getPrice());
    }

    @Override
    public void addEntryTooltip(TooltipList list, ShopEntry entry) {
        list.add(Component.translatable("sdm.shop.entry.info.xp_entry", Component.literal(String.valueOf(this.xpCount)).withStyle(ChatFormatting.GREEN)));
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.xp");
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addInt("xp", this.xpCount, (v) -> this.xpCount = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public String getId() {
        return "xpType";
    }

    @Override
    public boolean isSearch(String search) {
        boolean find = search.contains("xp");
        if(find && search.contains(String.valueOf(xpCount)))
            return true;

        return find;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("xp", xpCount);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if(tag.contains("xp"))
            this.xpCount = tag.getInt("xp");
    }

    @Override
    public @Nullable Icon getCustomIcon(ShopEntry entry, int tick) {
        if(entry.getRenderComponent().getIcon().isEmpty())
            return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);

        return null;
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
    }
}
