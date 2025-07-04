package net.sixik.sdmshop.client.screen.modern.wallet;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public class OpenWalletButton extends SimpleButton {

    public float alpha = 1.0F;
    public boolean s = false;

    public OpenWalletButton(Panel panel, Component text, Icon icon, Callback c) {
        super(panel, text, icon, c);
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getUser().getGameProfile(), false);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blit(info.getSkinLocation(), x, y, w, h, 8.0F, 8.0F, 8, 8, 64, 64);
        graphics.blit(info.getSkinLocation(), x, y, w, h, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        if (s) {
            alpha += 0.002F;
        } else {
            alpha -= 0.002F;
        }

        if (alpha >= 1.0F || alpha <= 0.5F) {
            s = !s;
        }
    }

    @Override
    public boolean shouldAddMouseOverText() {
        return false;
    }

}
