package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class PlayerWallet extends BaseScreen {

    public CurrenciesPanel currenciesPanel;
    public PayPanel payPanel;
    public TransferPanel transferPanel = new TransferPanel();
    public ModernSimpleTextButton transaction;
    public static BaseCurrency currency;
    public static PlayerInfo recipient;

    @Override
    public void addWidgets() {
       add(currenciesPanel = new CurrenciesPanel(this));
       add(payPanel = new PayPanel(this));
       if(currency != null && recipient != null)
            add(transaction = new ModernSimpleTextButton(this, Component.literal("transaction"), Icon.empty()) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    if(!widgets.contains(transferPanel)) {
                        transferPanel.openGui();
                    }
                }
            });
    }

    @Override
    public void alignWidgets() {
        currenciesPanel.setPosAndSize(4,24,this.width/2 - 7, height - 29);
        payPanel.setPosAndSize(currenciesPanel.width + 8, 24,this.width/2 - 7 , height - 29);
        if(currency != null && recipient != null)
            transaction.setPosAndSize(width/2 - 25,7,50,10);
    }

    @Override
    public boolean drawDefaultBackground(GuiGraphics graphics) {
        return false;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRect(graphics,x,y,w,h,5, RGBA.create(0,0,0, 255/2));
        PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getUser().getGameProfile(), false);
        graphics.blit(info.getSkinLocation(), x + 3 , y + 3, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(info.getSkinLocation(), x + 3, y + 3, 15, 15, 40.0f, 8, 8, 8, 64, 64);
    }
}
