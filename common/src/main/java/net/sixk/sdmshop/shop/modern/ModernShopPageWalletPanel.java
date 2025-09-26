package net.sixk.sdmshop.shop.modern;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.math.Vector2f;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.TextRenderHelper;


public class ModernShopPageWalletPanel  extends Panel {

    public SimpleButton basket;
    public TextField titel = new TextField(this);
    public float alpha = 1;
    public boolean s = false;

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectDown(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getGameProfile(),false);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        graphics.blit(info.getSkin().texture(), x + width/2 - 8 , y + height/2 - 5, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(info.getSkin().texture(), x + width/2 - 8, y + height/2 - 5, 15, 15, 40.0f, 8, 8, 8, 64, 64);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        if (s) {
            alpha += 1f / Minecraft.getInstance().getFps();
        } else {
            alpha -= 1f / Minecraft.getInstance().getFps();
        }

        if (alpha >= 1.0F || alpha <= 0.5F) {
            s = !s;
        }
    }

    public ModernShopPageWalletPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(titel);
        setProperty();
        add(basket = new SimpleButton(this, Component.empty(), Icon.empty(),((simpleButton, mouseButton) -> {

            new ModernPlayerBasket().openGui();

        })){
            @Override
            public boolean shouldAddMouseOverText() {
                return false;
            }
        });
    }

    @Override
    public void alignWidgets() {
        basket.setPosAndSize(width/2 - 8, height/2 - 7, 15,15);
    }

    public void setProperty(){


        Vector2f size = TextRenderHelper.getTextRenderSize(Component.translatable("sdm_shop.player_basket.wallet").getString(), (int) this.width, 1.0f, 50);


        int w1 = (int) (this.width - size.x);
        int w2 = w1 / 2;

        titel.setPos(w2,3);
        titel.setSize(this.width, this.height);
        titel.setMaxWidth(this.width);

        titel.setText(Component.translatable("sdm_shop.player_basket.wallet"));
        titel.setScale(size.y);
    }
}
