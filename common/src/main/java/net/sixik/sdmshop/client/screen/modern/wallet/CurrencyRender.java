package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;


public class CurrencyRender extends Panel {

    public TextField nameLabel;
    public TextField balanceLabel;
    public SimpleButton select;
    public BaseCurrency currency;
    public String name;
    public CurrencySymbol symbol;
    public Double balance;

    public CurrencyRender(Panel panel, CurrencyPlayerData.PlayerCurrency currency){
        super(panel);
        this.currency = currency.currency;
        name = this.currency.getName();
        symbol = this.currency.symbol;
        balance = currency.balance;
    }

    @Override
    public void addWidgets() {
        add(nameLabel = new TextField(this));
        add(balanceLabel = new TextField(this));
        add(select = new SimpleButton(this, Component.literal("null"), Icon.empty(),((simpleButton, mouseButton) -> {
            if(PlayerWallet.currency == null) PlayerWallet.currency = currency;
                else PlayerWallet.currency = null;
            parent.getGui().refreshWidgets();
        })));
    }

    @Override
    public void alignWidgets() {
        nameLabel.setText(name);
        nameLabel.setX(2);
        nameLabel.setY(1);
        balanceLabel.setText(balance+ " " +symbol.value);
        balanceLabel.setX(2);
        balanceLabel.setY(nameLabel.getHeight()+3);
        select.setPosAndSize(0,0,200, 22);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(PlayerWallet.currency == currency) RenderHelper.drawRoundedRect(graphics,x,y,w,h, 5, RGBA.create(255,255,255, 140));
            else RenderHelper.drawRoundedRect(graphics,x,y,w,h,5, RGBA.create(255,255,255, 110));
    }
}
