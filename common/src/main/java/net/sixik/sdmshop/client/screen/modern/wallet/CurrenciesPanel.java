package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.data.CurrencyData;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class CurrenciesPanel extends Panel {

    public CurrencyReder currencyReder;

    public CurrenciesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        int i = 0;
        for (CurrencyPlayerData.PlayerCurrency currency : EconomyAPI.getPlayerCurrencyClientData().currencies) {
            i++;
            add(currencyReder = new CurrencyReder(this,currency));
            currencyReder.setPosAndSize(2,2*i,parent.width/2 -4,20);
        }
    }

    @Override
    public void alignWidgets() {


    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRect(graphics,x,y,w, h,2, RGBA.create(0,0,0, 180));

    }
}
