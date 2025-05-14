package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.math.Vector2f;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.TextRenderHelper;

public class ModernShopTitelPanel extends Panel {

    public TextField titel = new TextField(this);

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectUp(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }

    public ModernShopTitelPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(titel);
        setProperty();
    }

    @Override
    public void alignWidgets() {
        setProperty();
    }

    public void setProperty(){


        Vector2f size = TextRenderHelper.getTextRenderSize(Component.translatable("sdm.shop.modern.ui.tab_categories").getString(), (int) this.width, 1.2f, 50);
        int d2 = Minecraft.getInstance().font.lineHeight;

        int w1 = (int) (this.width - size.x);
        int w2 = w1 / 2;
        int h1 = this.height / 2;
        h1 = h1 - d2 / 2;

        titel.setPos(w2,h1);
        titel.setSize(this.width, this.height);
        titel.setMaxWidth(this.width);

        titel.setText(Component.translatable("sdm.shop.modern.ui.tab_categories"));
        titel.setScale(size.y);
    }
}
