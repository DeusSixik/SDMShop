package net.sixk.sdmshop.shop;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixk.sdmshop.SDMShop;


public class WalletRender extends Panel {

    Float balance;
    AbstractCurrency currency;
    SimpleButton delete;
    TextField currencyTxt;
    TextField balanceTxt;

    public WalletRender(Panel panel, AbstractCurrency currency, Float balance) {

        super(panel);
        setSize(67,22);
        this.currency = currency;
        this.balance = balance;

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        Color4I.rgb(39,50,73).draw(graphics,x + 1, y , 66, 21);
        GuiHelper.drawHollowRect(graphics, x , y , 67, 22, Color4I.rgb(94,106,130), false);

    }

    @Override
    public void addWidgets() {

        add(currencyTxt = new TextField(this));
        add(balanceTxt = new TextField(this));
        if(SDMShop.isEditMode()) {
            add(delete = new SimpleButton(this, Component.translatable("sdm_shop.delete"), Icons.REMOVE, ((simpleButton, mouseButton) -> {
                CurrencyHelper.deleteCustomCurrency(currency.getID());
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getGui().refreshWidgets();
            })));
            delete.setPosAndSize(59, 2, 6, 6);
        }
    }

    @Override
    public void alignWidgets() {
        String i = Component.translatable("sdm_shop.currency." + currency.getID()).getString();
        if(i.equals("sdm_shop.currency." + currency.getID())) i = currency.getID();
        currencyTxt.setText( i + " " + currency.specialSymbol);
        currencyTxt.setPos(2,3);
        balanceTxt.setText(balance.toString());
        balanceTxt.setPos(2,currencyTxt.height + 5);

    }
}
