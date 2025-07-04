package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;


public class CurrencyReder extends Panel {

    public TextField nameLabel;
    public TextField balanceLabel;

    public String name;
    public CurrencySymbol symbol;
    public Double balance;

    public CurrencyReder(Panel panel, CurrencyPlayerData.PlayerCurrency currency){
        super(panel);
        name = currency.currency.getName();
        symbol = currency.currency.symbol;
        balance = currency.balance;
    }

    @Override
    public void addWidgets() {
        add(nameLabel = new TextField(this));
        add(balanceLabel = new TextField(this));
    }

    @Override
    public void alignWidgets() {
        nameLabel.setText(name);
        nameLabel.setX(2);
        nameLabel.setY(1);
        balanceLabel.setText(balance+ " " +symbol.value);
        balanceLabel.setX(2);
        balanceLabel.setY(nameLabel.getHeight()+3);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        //RenderHelper.drawHollowRect(graphics,x,y,w/2 -1,h/2 -1, RGBA.create(255,255,255, 255/2), false);
        RenderHelper.drawRoundedRect(graphics,x,y,w,h,5,RGBA.create(255,255,255, 220/2));
    }
}
