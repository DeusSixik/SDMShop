package net.sixk.sdmshop.shop;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;


public class WalletRender extends Panel {
    Float balance;
    CurrencyPlayerData.PlayerCurrency currency;
    SimpleButton delete;
    TextField currencyTxt;
    TextField balanceTxt;

    public WalletRender(Panel panel, CurrencyPlayerData.PlayerCurrency currency, Float balance) {
        super(panel);
        this.setSize(67, 22);
        this.currency = currency;
        this.balance = balance;
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        Color4I.rgb(39, 50, 73).draw(graphics, x + 1, y, 66, 21);
        GuiHelper.drawHollowRect(graphics, x, y, 67, 22, Color4I.rgb(94, 106, 130), false);
    }

    public void addWidgets() {
        add(this.currencyTxt = new TextField(this));
        add(this.balanceTxt = new TextField(this));
        if (SDMShop.isEditMode()) {
            add(delete = new SimpleButton(this, Component.translatable("sdm_shop.delete"), Icons.REMOVE, (simpleButton, mouseButton) -> {
                EconomyAPI.deleteCurrencyOnClient(this.currency.currency);

                try {
                    Thread.sleep(300L);
                } catch (InterruptedException var4) {
                    InterruptedException e = var4;
                    throw new RuntimeException(e);
                }

                TovarList.CLIENT.tovarList.forEach((s) -> {
                    if (s.currency.equals(currency.currency.getName())) {
                        s.currency = "sdmcoin";
                    }

                });
                NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarList.CLIENT.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                getGui().refreshWidgets();
            }));
            delete.setPosAndSize(59, 2, 6, 6);
        }

    }

    public void alignWidgets() {
        String i = Component.translatable("sdm_shop.currency." + currency.currency.getName()).getString();
        if (i.equals("sdm_shop.currency." + currency.currency.getName())) {
            i = currency.currency.getName();
        }

        currencyTxt.setText(i + " " + currency.currency.symbol.value);
        currencyTxt.setPos(2, 3);
        balanceTxt.setText(balance.toString());
        balanceTxt.setPos(2, currencyTxt.height + 5);
    }
}
