package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.api.CustomIcon;
import net.sixik.sdmshop.api.shop.AbstractEntryType;
import net.sixik.sdmshop.api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopItemHelper;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagEntryType extends AbstractEntryType implements CustomIcon {

    protected static ResourceLocation DEFAULT = new ResourceLocation("minecraft", "anvil");
    protected ResourceLocation tagKey;
    protected boolean useIconFromTag = true;
    protected boolean showRandomIconFromTag = true;
    protected int currentRenderIndex = 0;

    public TagEntryType(ShopEntry shopEntry) {
        this(shopEntry, DEFAULT);
    }

    public TagEntryType(ShopEntry shopEntry, ResourceLocation tagKey) {
        super(shopEntry);
        this.tagKey = tagKey;
    }

    public void getConfig(ConfigGroup group) {
        group.addEnum("tags", this.tagKey.toString(), (v) -> {
            this.tagKey = new ResourceLocation(v);
            this.currentRenderIndex = 0;
        }, getTags());
        group.addBool("useIconFromTag", this.useIconFromTag, (v) -> this.useIconFromTag = v, true);
        group.addBool("showRandomIconFromTag", this.showRandomIconFromTag, (v) -> this.showRandomIconFromTag = v, true);
    }

    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.ANVIL);
    }

    public Optional<HolderSet.Named<Item>> getTag() {
        return BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, this.tagKey));
    }

    public static NameMap<String> getTags() {
        List<String> str = new ArrayList<>();
        BuiltInRegistries.ITEM.getTags().forEach((s) -> str.add(s.getFirst().location().toString()));
        return NameMap.of(DEFAULT.toString(), str).create();
    }

    @Override
    public EntryTypeProperty getProperty() {
        return EntryTypeProperty.ONLY_SELL_COUNTABLE;
    }

    @Override
    public AbstractEntryType copy() {
        return new TagEntryType(shopEntry, tagKey);
    }

    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.itemtag");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdm.shop.entry.creator.type.itemTag.description"));
        return list;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("tagKey", this.tagKey.toString());
        if (this.useIconFromTag)
            nbt.putBoolean("useIconFromTag", true);

        if(!showRandomIconFromTag)
            nbt.putBoolean("showRandomIconFromTag", false);

        return nbt;
    }

    public void deserialize(CompoundTag nbt) {
        this.tagKey = new ResourceLocation(nbt.getString("tagKey"));
        if (nbt.contains("useIconFromTag")) {
            this.useIconFromTag = nbt.getBoolean("useIconFromTag");
        }

        if(nbt.contains("showRandomIconFromTag"))
            this.showRandomIconFromTag = false;

    }

    @Override
    public String getId() {
        return "itemTag";
    }



    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        throw new NotImplementedException();
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, this.tagKey));
        if(tag.isEmpty()) return false;

        HolderSet.Named<Item> tagData = tag.get();

        return ShopItemHelper.shrinkItemByTag(player.getInventory(), tagData.key(), (int) (entry.getCount() * countBuy));
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        if(entry.getType().isBuy()) return false;


        Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, this.tagKey));
        if (tag.isEmpty()) return false;
        else {
            HolderSet.Named<Item> t = tag.get();
            return ShopItemHelper.countItem(player.getInventory(), t.key()) >= entry.getCount() * countBuy;
        }
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, this.tagKey));
        if (tag.isEmpty()) return 0;
        HolderSet.Named<Item> t = tag.get();
        return (int) (ShopItemHelper.countItem(player.getInventory(), t.key()) / entry.getCount());
    }

    @Override
    public void addEntryTooltip(TooltipList list, ShopEntry entry) {
        if(Screen.hasShiftDown()) {
            Optional<HolderSet.Named<Item>> tagOptional = this.getTag();
            if(tagOptional.isEmpty()) return;

            HolderSet.Named<Item> tagData = tagOptional.get();
            if (tagData.size() <= 0) {
                return;
            }

            list.add(Component.translatable("sdm.shop.entry.info.items").withStyle(ChatFormatting.GOLD));

            for(int i = 0; i < tagData.size(); ++i) {
                if (i % 2 == 0) {
                    list.add(tagData.get(i).value().getDefaultInstance().getHoverName().copy().withStyle(ChatFormatting.WHITE));
                } else {
                    list.add(tagData.get(i).value().getDefaultInstance().getHoverName().copy().withStyle(ChatFormatting.AQUA));
                }
            }
        } else {
            list.add(Component.translatable("sdm.shop.entry.info.tag.type", new Object[]{this.tagKey.toString()}));
            list.add(Component.translatable("sdm.shop.entry.info.pressshift").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    @Override
    public @Nullable Icon getCustomIcon(ShopEntry entry, int tick) {
        if (entry == null || !this.useIconFromTag || this.tagKey == null) {
            return null;
        }

        Optional<HolderSet.Named<Item>> oTag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, this.tagKey));
        if (oTag.isEmpty()) {
            return null;
        }

        HolderSet.Named<Item> tag = oTag.get();
        int size = tag.size();
        if (size <= 0) {
            return null;
        }

        if (showRandomIconFromTag) {
            if (tick % ShopUtilsClient.getShop().getShopParams().getChangeIconSpeed() == 0) {
                currentRenderIndex = (currentRenderIndex + 1) % size;
            }
            return ItemIcon.getItemIcon(tag.get(currentRenderIndex).value());
        }

        return ItemIcon.getItemIcon(tag.get(0).value());
    }

    @Override
    public boolean isSearch(String search) {
        Optional<HolderSet.Named<Item>> oTag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, this.tagKey));
        return oTag.filter(holders -> ShopItemHelper.isSearch(search, holders)).isPresent();

    }
}
