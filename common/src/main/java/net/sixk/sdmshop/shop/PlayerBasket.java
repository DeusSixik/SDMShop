package net.sixk.sdmshop.shop;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.shop.modern.ModernAddCurrencyPanel;
import net.sixk.sdmshop.shop.modern.ModernWalletRender;

import java.util.ArrayList;
import java.util.List;


public class PlayerBasket extends BaseScreen {

    public static PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getGameProfile(),false);
    TextField walletTxt;
    Panel walletPanel;
    List<WalletRender> walletRenderasList = new ArrayList<>();
    SimpleButton addCurrency;

    @Override
    public boolean onInit() {
        refreshWidgets();
        return  true;
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        //blit(ResourceLocation texture, int x, int y, int width, int height, int textureX, int textureY, int textureW, int textureH, int textureSizeX, int textureSizeY)
        graphics.blit(info.getSkin().texture(), x + 2 , y + 2, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(info.getSkin().texture(), x + 2, y + 2, 15, 15, 40.0f, 8, 8, 8, 64, 64);
        NordColors.POLAR_NIGHT_0.draw(graphics,x + 5,y + 25,width/2 - 20, h - 30);
        GuiHelper.drawHollowRect(graphics,x + 4,y + 24,width/2 - 19, h - 29, NordColors.POLAR_NIGHT_4, false);
        NordColors.POLAR_NIGHT_0.draw(graphics,x + width/2 - 10,y + 25,width/2 + 5, h - 30);
        GuiHelper.drawHollowRect(graphics,x + width/2 - 11 ,y + 24,width/2 + 6, h - 29, NordColors.POLAR_NIGHT_4, false);
    }



    @Override
    public void addWidgets() {
        add(walletTxt = new TextField(this));
        add(walletPanel = new Panel(this) {


            @Override
            public void addWidgets() {

                walletRenderasList.clear();

                for (CurrencyPlayerData.PlayerCurrency w : EconomyAPI.getPlayerCurrencyClientData().currencies) {

                    WalletRender  walletRender = null;
                    if(ConfigFile.CLIENT.style){
                        walletRender = new ModernWalletRender(walletPanel,w, (float) w.balance);
                    }else {
                        walletRender = new WalletRender(walletPanel,w, (float) w.balance);
                    }

                    walletPanel.add(walletRender);

                    walletRenderasList.add(walletRender);
                }


                if(SDMShop.isEditMode())
                    walletPanel.add(addCurrency = new SimpleButton(walletPanel, Component.translatable("sdm_shop.player_basket.add_currency"), Icons.ADD,((simpleButton, mouseButton) ->{
                        if(ConfigFile.CLIENT.style){
                            new ModernAddCurrencyPanel().openGui();
                        }else {
                            new AddCurrencyPanel().openGui();
                        }
                    })));

                for (int n = 0; n < walletRenderasList.size(); n++) {

                    WalletRender w = walletRenderasList.get(n);
                    w.setPos(0,23 * n);

                }
                if(SDMShop.isEditMode()) addCurrency.setPos(1,23 * walletRenderasList.size());
            }


            @Override
            public void alignWidgets() {

            }

        });

        walletPanel.addWidgets();

    }

    @Override
    public void alignWidgets() {

        walletTxt.setText(Component.translatable("sdm_shop.player_basket.wallet"));
        walletTxt.setScale(0.6f);
        walletTxt.setPos(width/4 - 15 ,18);
        walletPanel.setPos(5,25);
        walletPanel.setSize(width/2 - 20,height - 30);

    }


}
