package net.sixik.sdmshoprework.client.screen.legacy.widget;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractShopTabButton;
import net.sixik.sdmshoprework.common.shop.ShopTab;

import java.util.Objects;

public class LegacyShopTabButton extends AbstractShopTabButton {

    public LegacyShopTabButton(Panel panel, ShopTab shopTab) {
        super(panel, shopTab);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);

        if(shopTab == null) return;

        if(getShopScreen().selectedTab != null && Objects.equals(getShopScreen().selectedTab.shopTabUUID, shopTab.shopTabUUID)) {
            drawSelected(graphics, x, y, w, h);
        }
    }
}
