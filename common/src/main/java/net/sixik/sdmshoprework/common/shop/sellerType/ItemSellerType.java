package net.sixik.sdmshoprework.common.shop.sellerType;

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
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopSellerType;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.utils.NBTUtils;
import net.sixik.sdmshoprework.common.utils.SDMItemHelper;
import net.sixik.sdmshoprework.common.utils.item.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemSellerType extends AbstractShopSellerType<ItemStack> {

    protected boolean showTooltip = false;

    public ItemSellerType(ItemStack type) {
        super(type);
    }

    @Override
    public String getEnumName() {
        return "ITEM";
    }

    @Override
    public long getCount(Player player) {
        return SDMItemHelper.countItems(player, type);
    }

    @Override
    public boolean buy(Player player, AbstractShopEntry shopEntry, long countSell) {
        if(shopEntry.isSell) {
            try {
                ItemHandlerHelper.giveItemToPlayer(player, type.copyWithCount((int) countSell));
                return true;
            } catch (Exception e) {
                SDMShopRework.printStackTrace("Error when buying: ", e);
                return false;
            }
        }

        long itemCount = getCount(player);
        if(itemCount >= countSell) {
            ItemStack copy = type.copyWithCount(1);
            return SDMItemHelper.sellItem(player, (int) countSell, copy, !copy.hasTag());
        }

        return false;
    }

    @Override
    public String getId() {
        return "item";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        NBTUtils.putItemStack(nbt, "item", type);

        if(showTooltip) nbt.putBoolean("showTooltip", true);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("item")) type = NBTUtils.getItemStack(nbt, "item");
        if(nbt.contains("showTooltip")) showTooltip = nbt.getBoolean("showTooltip");
    }

    public static class Constructor implements IConstructor<AbstractShopSellerType<?>> {
        @Override
        public AbstractShopSellerType<?> createDefaultInstance() {
            return new ItemSellerType(Items.DIAMOND.getDefaultInstance());
        }
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {
        configGroup.addItemStack("item", type, v -> type = v, Items.DIAMOND.getDefaultInstance(), true, false);
        configGroup.addBool("showTooltip", showTooltip, v -> showTooltip = v, false);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, long count, @Nullable Widget widget, int additionSize) {
        int size = height - height / 3 + additionSize;

        ItemIcon.getItemIcon(getType()).draw(graphics, x, y - 1, size, size);
        graphics.drawString(Minecraft.getInstance().font, String.format("%d", count), x + height, y + 1, 0xFFFFFF);
    }

    @Override
    public void addTooltip(TooltipList tooltipList, AbstractShopEntry entry) {
        if(showTooltip) {
            List<Component> list1 = new ArrayList<>();

            if(entry.isSell) {
                list1.add(Component.translatable("sdmr.shop.ehtry.sell.by").withStyle(ChatFormatting.GOLD));
            }else {
                list1.add(Component.translatable("sdmr.shop.ehtry.sell.off").withStyle(ChatFormatting.GOLD));
            }

            list1.add(Component.literal("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾"));
            GuiHelper.addStackTooltip(type, list1);
            list1.add(Component.literal("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾"));
            list1.forEach(tooltipList::add);
        }
    }
}
