package net.sixik.sdmshop.old_api.shop;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.ModObjectIdentifier;
import net.sixik.sdmshop.old_api.SearchSupport;
import net.sixik.sdmshop.client.render.BuyerRenderVariable;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerScreen;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;

import java.util.List;

public abstract class AbstractEntryType implements DataSerializerCompoundTag, ModObjectIdentifier, SearchSupport, ConfigSupport, ShopObject {

    protected final ShopEntry shopEntry;

    protected AbstractEntryType(ShopEntry shopEntry) {
        this.shopEntry = shopEntry;
    }

    public EntryTypeProperty getProperty() {
        return EntryTypeProperty.DEFAULT_COUNTABLE;
    }

    public abstract AbstractEntryType copy();

    public boolean isCountable() {
        return true;
    }

    public abstract boolean onBuy(Player player, ShopEntry entry, int countBuy);

    public abstract boolean onSell(Player player, ShopEntry entry, int countBuy);

    public abstract boolean canExecute(Player player, ShopEntry entry, int countBuy);

    public abstract int howMany(Player player, ShopEntry entry);

    public void sendNotifiedMessage(Player player, ShopEntry entry,  int count) {}

    public Icon getCreativeIcon() {
        return Icons.DIAMOND;
    }

    public abstract Component getTranslatableForCreativeMenu();

    public void addEntryTooltip(TooltipList list, ShopEntry entry) {}

    public List<Component> getDescriptionForContextMenu(){
        return List.of(Component.translatable("sdm.shop.entry.creator.type." + getId() + ".description"));
    }

    public String getModNameForContextMenu(){
        return "";
    }

    public AbstractEntryType updateIcon(ItemStack icon) {
        this.shopEntry.updateIcon(icon);
        return this;
    }

    @Environment(EnvType.CLIENT)
    public void drawIcon(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, SimpleTextButton widget, int tick) {
        widget.drawIcon(graphics, theme, x,y,w,h);
    }

    @Environment(EnvType.CLIENT)
    public void drawTitle(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, BuyerRenderVariable variable, AbstractBuyerScreen screen) {
        final String title = entry.getTitle().getString();

        if(title.isEmpty()) return;

        final Vector2 pos = variable.pos;

        w = TextHelper.getTextWidth(title);
        final int w1 = (screen.width - 10 - 2 - variable.iconSize * 2) - w;
        final int w2 = w1 / 2;

        theme.drawString(graphics, title, pos.x + w2, pos.y + 1, Color4I.WHITE, 2);
    }

    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.ENTRY_TYPE;
    }
}
