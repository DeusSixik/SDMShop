package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class MainShopEntryButton extends SimpleTextButton {

    protected final MainShopEntryPanel entryPanel;
    protected final ShopEntry shopEntry;
    public boolean drawingBatch;
    public boolean endBatch;

    public String moneyText;
    public int textL;

    public MainShopEntryButton(MainShopEntryPanel panel, ShopEntry shopEntry) {
        super(panel, shopEntry.getTitle(), Icon.empty());
        this.entryPanel = panel;
        this.shopEntry = shopEntry;
        onInit();
    }

    public void onInit() {

        moneyText = shopEntry.getEntrySellerType().moneyToString(shopEntry);
        textL = Theme.DEFAULT.getStringWidth(moneyText);
    }

    @Override
    public void onClicked(MouseButton button) {

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(drawingBatch) {
            if(isMouseOver) ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_3_INT);
            else ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
        } else if(endBatch) drawAfterBatch(graphics, theme, x, y, w, h);
    }

    public void drawAfterBatch(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, y + (h - (theme.getFontHeight() + 2)) ,w, h, shopEntry.getPrice());
    }
}
