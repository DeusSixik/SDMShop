package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.shop.WalletRender;

public class ModernWalletRender extends WalletRender {

    public ModernWalletRender(Panel panel, CurrencyPlayerData.PlayerCurrency currency, Float balance) {
        super(panel, currency, balance);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShapesRenderHelper.drawRoundedRect(graphics,x,y,w,h,5, RGBA.create(255,255,255, 255/3));
    }

}
