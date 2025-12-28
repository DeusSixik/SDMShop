package net.sixik.sdmshop.client.screen.base.buyer;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.old_api.screen.RefreshSupport;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.exceptions.TabNotFoundException;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractBuyerScreen extends BaseScreen implements RefreshSupport {

    @Override public boolean drawDefaultBackground(GuiGraphics graphics) { return false; }

    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final int lineHeight = minecraft.font.lineHeight;


    protected AbstractBuyerBuyButton buyButton;
    protected AbstractBuyerCancelButton cancelButton;

    protected ShopTab shopTab;
    protected ShopEntry shopEntry;
    protected int count = 0;

    protected AbstractShopScreen shopScreen;

    public AbstractBuyerScreen(AbstractShopScreen shopScreen, AbstractShopEntryButton shopEntry) {
        this.shopScreen = shopScreen;
        this.shopEntry = shopEntry.getShopEntry();
        if(this.shopEntry != null)
            this.shopTab = shopScreen.currentShop.getTabOptional(this.shopEntry.getTab()).orElseThrow(TabNotFoundException::new);
    }

    @Override
    public void onClosed() {
        super.onClosed();
        shopScreen.currentShop.onChange();
    }

    public ShopLimiterData getShopLimit() {
        return ShopUtils.getShopLimit(shopTab, shopEntry, Minecraft.getInstance().player);
    }

    public int getMaxEntryOfferSize() {
        return getMaxEntryOfferSize(getShopLimit().value());
    }

    public int getMaxEntryOfferSize(int size) {
        return ShopUtils.getMaxEntryOfferSize(shopEntry, Minecraft.getInstance().player, size);
    }

    protected static boolean isDigitsInRange(String s, int min, int max) {
        if (s == null || s.isEmpty()) return false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }

        long v;
        try {
            v = Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }

        return v >= min && v <= max;
    }
}
