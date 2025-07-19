package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class TransferPanel extends BaseScreen {

    public TextField titel;
    public PlayerInfo player;
    public TextField label;
    public ModernTextBox count;
    public ModernSimpleTextButton accept;

    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth()/3);
        setHeight(getScreen().getGuiScaledHeight()/3);

        return true;
    }

    @Override
    public void addWidgets() {
        add(titel = new TextField(this));
        add(label = new TextField(this));
        add(count =  new ModernTextBox(this));
        add(accept = new ModernSimpleTextButton(this, Component.literal("null"), Icon.empty()){
            @Override
            public void onClicked(MouseButton mouseButton) {

            }
        });
    }

    @Override
    public void alignWidgets() {
        titel.setText(Component.literal("Transfer of funds"));
        titel.setPos(width/2-titel.getWidth()/2,5);
        label.setText(Component.literal("Transfer amount:"));
        label.setScale(0.7f);
        label.setPos(10,height/2);
        count.setPosAndSize(label.width +13, height/2-2,50, 10);
        accept.setPos(width/2- accept.width/2,height-accept.height - 4);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRect(graphics,x,y,w, h,2, RGBA.create(0,0,0, 255/2));
        PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getUser().getGameProfile(), false);
        graphics.blit(info.getSkinLocation(), x +20 , y + h/5, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(info.getSkinLocation(), x +20, y + h/5, 15, 15, 40.0f, 8, 8, 8, 64, 64);
        Icons.RIGHT.draw(graphics,x +w/2 - 10, y +h/5,20,15);
        graphics.blit(PlayerWallet.recipient.getSkinLocation(), x +w -35 , y + h/5, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(PlayerWallet.recipient.getSkinLocation(), x +w -35, y + h/5, 15, 15, 40.0f, 8, 8, 8, 64, 64);
    }

}
