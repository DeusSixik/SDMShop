package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class PayPanel extends Panel {

    public PayCardRender payCardRender;

    public PayPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        int i = 0;
        for (PlayerInfo onlinePlayer : Minecraft.getInstance().getConnection().getOnlinePlayers()) {
            if(onlinePlayer.getProfile().getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) continue;
            i++;
            add(payCardRender = new PayCardRender(this,onlinePlayer));
            payCardRender.setPosAndSize(2,2 *i,parent.width/2 - 11,20);
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
