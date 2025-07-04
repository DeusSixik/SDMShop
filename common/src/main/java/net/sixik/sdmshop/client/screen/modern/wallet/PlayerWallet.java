package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class PlayerWallet extends BaseScreen {

    public CurrenciesPanel currenciesPanel;

    @Override
    public void addWidgets() {
       add(currenciesPanel = new CurrenciesPanel(this));
    }

    @Override
    public void alignWidgets() {
        currenciesPanel.setPosAndSize(4,24,this.width/2, height - 29);
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
        //RenderHelper.drawRoundedRect(graphics,x + 4,y + 24,width/2 - 19, h - 29,2,RGBA.create(0,0,0, 180));
        //RenderHelper.drawRoundedRect(graphics,x + width/2 - 11 ,y + 24,width/2 + 6, h - 29,2, RGBA.create(0,0,0, 180));
    }
}
