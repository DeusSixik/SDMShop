package net.sixk.sdmshop.shop;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixik.sdmeconomy.economy.CurrencySymbol;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.data.config.ConfigFile;

import static dev.ftb.mods.ftblibrary.ui.misc.NordColors.POLAR_NIGHT_0;
import static dev.ftb.mods.ftblibrary.ui.misc.NordColors.POLAR_NIGHT_4;


public class AddCurrencyPanel extends BaseScreen {

    public TextField title;
    public TextField currencyNameTxt;
    public TextField currencySignTxt;
    public TextField error;
    public SimpleTextButton apply;
    public SimpleTextButton cancel;
    public TextBox currencyName = new TextBox(this);
    public TextBox currencySign = new TextBox(this);

    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth());
        setHeight(getScreen().getGuiScaledHeight());

        return true;
    }

    public AddCurrencyPanel() {

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        POLAR_NIGHT_0.draw(graphics,w / 2 - 75 ,h / 2 - 37,150,75);
        GuiHelper.drawHollowRect(graphics,w / 2 - 76,h / 2 - 38,152,77, POLAR_NIGHT_4,true);

    }


    @Override
    public void addWidgets() {

        add(title = new TextField(this).setText(Component.translatable("sdm_shop.add_currency_panel.titel")));
        add(currencyNameTxt = new TextField(this).setText(Component.translatable("sdm_shop.add_currency_panel.currency_name")));
        add(currencySignTxt = new TextField(this).setText(Component.translatable("sdm_shop.add_currency_panel.currency_sign")));
        add(error = new TextField(this).setText(""));
        add(currencyName = new TextBox(this));
        add(currencySign = new TextBox(this));


        add(apply = new SimpleTextButton(this, Component.translatable("sdm_shop.apply"), Icon.empty()) {

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                if (ConfigFile.CLIENT.style) {
                    ShapesRenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(0, 0, 0, 127));
                } else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                    GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, true);
                }
            }


            @Override
            public void onClicked(MouseButton mouseButton) {
                if(currencyName.getText().isEmpty() || currencySign.getText().isEmpty()) return;

                if(currencySign.getText().toCharArray().length != 1) {

                    error.setText(Component.translatable("sdm_shop.add_currency_panel.error_1").getString());
                    error.setScale(0.7f);
                    return;
                };


                if(Character.isLetterOrDigit(currencySign.getText().charAt(0))) {

                    error.setText(Component.translatable("sdm_shop.add_currency_panel.error_2").getString());
                    error.setScale(0.7f);
                    return;
                };

                EconomyAPI.createCurrencyOnClient(new Currency(currencyName.getText(),new CurrencySymbol(currencySign.getText())));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getGui().refreshWidgets();
                getGui().closeGui();
            }
        });
        add(cancel = new SimpleTextButton(this, Component.translatable("sdm_shop.cancel"), Icon.empty()) {

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                GuiHelper.drawHollowRect(graphics,x,y ,w,h, POLAR_NIGHT_4,true);
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                getGui().closeGui();
            }
        });
    }

    @Override
    public void alignWidgets() {
        title.setPos(getWidth() / 2 - title.getWidth() / 2 + 2 ,getHeight() / 2 - 35);
        currencyNameTxt.setPos(getWidth() / 2 - 50,getHeight() / 2 - 22);
        currencyName.setPos(getWidth() / 2 - 50,getHeight() / 2 - 12);
        currencyName.setSize(100,10);
        currencySignTxt.setPos(getWidth() / 2 - 50,getHeight() / 2 );
        currencySign.setPos(getWidth() / 2 - 6,getHeight() / 2 + 10);
        currencySign.setSize(13,10);


        error.setSize(20,30);
        error.setColor(Color4I.RED);
        error.setMaxWidth(75);
        error.setPos(width / 2 - 72,getHeight() / 2 + 11);

        apply.setPosAndSize(getWidth() / 2 - 45, getHeight() / 2 + 25,42,12);
        cancel.setPosAndSize(getWidth() / 2 + 3, getHeight() / 2 + 25,42,12);

    }
}
