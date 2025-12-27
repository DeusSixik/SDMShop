package net.sixik.sdmshop.shop.seller_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.shop.AbstractEntrySellerType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopItemHelper;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemSellerType extends AbstractEntrySellerType<ItemStack> {

    public static final String KEY = "money_item";

    public ItemSellerType() {
        this(Items.DIAMOND.getDefaultInstance());
    }

    public ItemSellerType(ItemStack objectType) {
        super(objectType);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry shopEntry, long countSell) {
        int countItems = ShopItemHelper.countItem(player.getInventory(), objectType, !objectType.hasTag());
        int sellSize = (int) (shopEntry.getPrice() * countSell);
        if(countItems >= sellSize) {
            return ShopItemHelper.shrinkItem(player.getInventory(), objectType.copy(), sellSize, !objectType.hasTag());
        }

        return false;
    }

    @Override
    public boolean onSell(Player player, ShopEntry shopEntry, long countSell) {
        return ShopItemHelper.giveItems(player, objectType.copyWithCount(1), (long) (shopEntry.getPrice() * countSell));
    }

    @Override
    public double getMoney(Player player, ShopEntry shopEntry) {
        return ShopItemHelper.countItem(player.getInventory(), objectType, !objectType.hasTag());
    }

    @Override
    public AbstractEntrySellerType<ItemStack> copy() {
        return new ItemSellerType(objectType);
    }

    @Override
    public String getId() {
        return "item_seller";
    }

    @Override
    public CompoundTag _serialize() {
        CompoundTag nbt = new CompoundTag();
        ShopNBTUtils.putItemStack(nbt, KEY, objectType);
        return nbt;
    }

    @Override
    public void _deserialize(CompoundTag tag) {
        objectType = ShopNBTUtils.getItemStack(tag, KEY);
    }

    @Override
    public String getEnumName() {
        return "ITEM";
    }

    @Override
    public String moneyToString(ShopEntry entry) {
        return entry.getPrice() + " ";
    }


    @Override
    public boolean isFractionalNumber() {
        return false;
    }

    @Override
    public void _getConfig(ConfigGroup configGroup) {
        configGroup.addItemStack("item", objectType, v -> objectType = v, Items.DIAMOND.getDefaultInstance(), true, false);
    }

    @Override
    public void addEntryTooltip(TooltipList list, ShopEntry entry) {
        if (shopTooltip) {
            List<Component> list1 = new ArrayList<>();
            if (entry.getType().isSell()) {
                list1.add(Component.translatable("sdm.shop.entry.sell.buy").withStyle(ChatFormatting.GOLD));
            } else {
                list1.add(Component.translatable("sdm.shop.entry.sell.sell").withStyle(ChatFormatting.GOLD));
            }

            list1.add(Component.literal("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾"));
            GuiHelper.addStackTooltip(objectType, list1);
            list1.add(Component.literal("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾"));
            Objects.requireNonNull(list);
            list1.forEach(list::add);
        }
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
        int size = height - height / 3 + additionSize;
        ItemIcon.getItemIcon(objectType).draw(graphics, x, y - 1, size, size);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf((int) count), x + height, y + 1, 0xFFFFFF);
    }

    @Override
    public int getRenderWight(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
        int s = height / 3;
        int size = height - s + additionSize;
        int textW = theme.getStringWidth(String.valueOf((int) count));
        return size + textW;
    }


}
