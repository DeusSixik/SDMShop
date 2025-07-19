package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class PayCardRender extends Panel {

    public PlayerInfo player;
    public TextField nick;
    public SimpleButton select;

    public PayCardRender(Panel panel, PlayerInfo player) {
        super(panel);
        this.player = player;
    }

    @Override
    public void addWidgets() {
        add(nick = new TextField(this));
        add(select = new SimpleButton(this, Component.literal("null"), Icon.empty(),((simpleButton, mouseButton) -> {
            if(PlayerWallet.recipient == null) PlayerWallet.recipient = player;
                else PlayerWallet.recipient = null;
            parent.getGui().refreshWidgets();
        })));
    }

    @Override
    public void alignWidgets() {
        nick.setText(player.getProfile().getName());
        nick.setPos(22, 3);
        select.setPosAndSize(0,0,200, 22);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(PlayerWallet.recipient == player) RenderHelper.drawRoundedRect(graphics,x,y,w,h, 5, RGBA.create(255,255,255, 140));
            else RenderHelper.drawRoundedRect(graphics,x,y,w,h,5, RGBA.create(255,255,255, 110));
        graphics.blit(player.getSkinLocation(), x + 3 , y + 3, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(player.getSkinLocation(), x + 3, y + 3, 15, 15, 40.0f, 8, 8, 8, 64, 64);

    }
}
