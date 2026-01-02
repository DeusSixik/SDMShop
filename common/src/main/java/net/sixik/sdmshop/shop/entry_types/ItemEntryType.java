package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.client.render.BuyerRenderVariable;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerScreen;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopItemHelper;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import net.sixik.sdmuilib.client.utils.TextHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemEntryType extends AbstractEntryType implements CustomIcon {

    protected ItemStack itemStack;
    protected boolean strictNbt = true;
    protected boolean ignoreDamage = true;

    public ItemEntryType(ShopEntry shopEntry) {
        this(shopEntry, Items.AIR.getDefaultInstance());

    }

    public ItemEntryType(ShopEntry shopEntry, ItemStack itemStack) {
        super(shopEntry);
        this.itemStack = itemStack;
        updateIcon(itemStack);
    }

    @Override
    public AbstractEntryType copy() {
        return new ItemEntryType(shopEntry, itemStack);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        if(countBuy <= 0) return false;
        final long totalCount = entry.getCount() * countBuy;
        if (totalCount > Integer.MAX_VALUE) {
            return false;
        }

        return ShopItemHelper.giveItems(player, itemStack, totalCount);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        if (countBuy <= 0) return false;

        long totalNeeded = (long) entry.getCount() * countBuy;
        if (totalNeeded > Integer.MAX_VALUE) return false;

        // ВАЖНО: Используем strictNbt = true.
        // Это решает проблему "Осколков души" и "Зачарованных органов".
        // Если itemStack (в магазине) чистый, мы ищем у игрока ТОЛЬКО чистые предметы.
        int available = ShopItemHelper.countItem(player.getInventory(), itemStack, strictNbt, ignoreDamage);

        if (available < totalNeeded) {
            // Можно отправить сообщение игроку "Недостаточно предметов"
            return false;
        }

        boolean success = ShopItemHelper.shrinkItem(player.getInventory(), itemStack, (int) totalNeeded, strictNbt, ignoreDamage);

        if (success) {
            // Обязательно обновляем инвентарь клиента, иначе будут "фантомные" предметы
            player.containerMenu.broadcastChanges();
        }

        return success;
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        if (entry.getType().isSell()) {
            long totalNeeded = entry.getCount() * countBuy;
            if (totalNeeded > Integer.MAX_VALUE) return false;

            int countItems = ShopItemHelper.countItem(player.getInventory(), itemStack, strictNbt, ignoreDamage);
            return countItems >= totalNeeded;
        } else {
            double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
            double needMoney = entry.getPrice() * countBuy;
            return playerMoney >= needMoney;
        }
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        if(entry.getType().isSell()){
            int countItems = ShopItemHelper.countItem(player.getInventory(), itemStack, strictNbt, ignoreDamage);
            if (entry.getCount() == 0) return 0;
            return Math.toIntExact(countItems / entry.getCount());
        } else {
            double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
            if(entry.getPrice() == 0) return Byte.MAX_VALUE * 4;
            return (int) (playerMoney / entry.getPrice());
        }
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.item");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdm.shop.entry.creator.type.item.description"));
        return list;
    }

    @Override
    public void sendNotifiedMessage(Player player, ShopEntry entry,  int count) {
        Component text;
        if(shopEntry.getType().isSell()) {
            text = Component.translatable("sdm.shop.entry.sell.info.item", itemStack.getDisplayName().getString(), entry.getCount() * count, entry.getEntrySellerType().getMoney(player, entry)).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        } else {
            text = Component.translatable("sdm.shop.entry.buy.info.item", itemStack.getDisplayName().getString(), shopEntry.getCount(), entry.getEntrySellerType().getMoney(player, entry)).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        }
        player.displayClientMessage(text, false);
    }

    @Override
    public String getId() {
        return "shopItemEntryType";
    }

    @Override
    public boolean isSearch(String search) {
        return ShopItemHelper.isSearch(search, itemStack);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        ShopNBTUtils.putItemStack(nbt, "itemStack", itemStack);
        nbt.putBoolean("strictNbt", strictNbt);
        nbt.putBoolean("ignoreDamage", ignoreDamage);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.itemStack = ShopNBTUtils.getItemStack(tag, "itemStack");
        if(tag.contains("ignoreDamage"))
            ignoreDamage = tag.getBoolean("ignoreDamage");
        if(tag.contains("strictNbt"))
            strictNbt = tag.getBoolean("strictNbt");
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addItemStack("item", itemStack, v -> {
            itemStack = v;
            updateIcon(itemStack);
        }, ItemStack.EMPTY, true, false);
        group.addBool("ignoreDamage", ignoreDamage, v -> ignoreDamage = v, true);
        group.addBool("strictNbt", strictNbt, v -> strictNbt = v, true);
    }

    @Override
    public @Nullable Icon getCustomIcon(ShopEntry entry, int tick) {
        return ItemIcon.getItemIcon(itemStack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawIcon(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, SimpleTextButton widget, int tick) {
        if(getCustomIcon(entry, tick) instanceof ItemIcon itemIcon)
            ItemIcon.getItemIcon(itemIcon.getStack().copyWithCount((int) entry.getCount())).draw(graphics, x,y,w,h);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawTitle(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, BuyerRenderVariable variable, AbstractBuyerScreen screen) {
        var pos = variable.pos;

        w = TextHelper.getTextWidth(itemStack.getDisplayName().getString());
        int w1 = (screen.width - 10 - 2 - variable.iconSize * 2) - w;
        int w2 = w1 / 2;


        String d = itemStack.getDisplayName().getString();
        d = d.replace("[", "").replace("]", "");
        theme.drawString(graphics, d, pos.x + w2, pos.y + 1, Color4I.WHITE, 2);
    }

    @Override
    public void drawTitleCentered(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        drawComponentCentered(graphics, itemStack.getHoverName(), theme, x, y, w, h);
    }

    @Override
    public void addEntryTooltip(TooltipList list, ShopEntry entry) {
        List<Component> list1 = new ArrayList();
        GuiHelper.addStackTooltip(this.itemStack, list1);
        Objects.requireNonNull(list);
        list1.forEach(list::add);
    }
}
