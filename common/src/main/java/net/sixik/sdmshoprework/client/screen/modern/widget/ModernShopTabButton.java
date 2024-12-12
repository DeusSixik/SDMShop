package net.sixik.sdmshoprework.client.screen.modern.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractShopTabButton;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.GLRenderHelper;
import net.sixik.v2.utils.math.Vector2;

import java.util.Objects;

public class ModernShopTabButton extends AbstractShopTabButton {
    

    public ModernShopTabButton(Panel panel, ShopTab shopTab) {
        super(panel, shopTab);
    }

    @Override
    public void draw(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        this.drawBackground(graphics, theme, x, y, w, h);
        int s = h >= 16 ? 16 : 8;
        int off = (h - s) / 2;
        int textX = x;
        int textY = y + (h - theme.getFontHeight() + 1) / 2;

        if (this.hasIcon()) {
            this.drawIcon(graphics, theme, x + 1, y + 1, h - 2, h - 2);
            textX += 1 + h - 2;
        }

        int d = off / 2;
        int j = h / 8;
        int k = 1 + h - 2 - 8;

        if(shopTab != null) {

            if (getShopScreen().selectedTab != null && Objects.equals(getShopScreen().selectedTab.shopTabUUID, shopTab.shopTabUUID)) {
                RGBA.create(255,255,255, 255 / 3).drawRoundFill(graphics, textX + 4, y + 2, this.width - k * 3 - 2, h - 4, 2);
            } else
                RGBA.create(0,0,0, 255 / 3).drawRoundFill(graphics, textX + 4, y + 2, this.width - k * 3 - 2, h - 4, 2);

        } else
            RGBA.create(0,0,0, 255 / 3).drawRoundFill(graphics, textX + 4, y + 2, this.width - k * 3 - 2, h - 4, 2);


        Vector2 pos = new Vector2(textX + 6, textY + 1);
        GLRenderHelper.pushScissor(graphics, pos.x,pos.y, this.width - k * 3 - 5, h - 4);

        GLRenderHelper.pushTransform(graphics, pos, new Vector2(1,1), 0.7f, 0);
        theme.drawString(graphics, title, pos.x, pos.y, theme.getContentColor(this.getWidgetType()), 2);
        GLRenderHelper.popTransform(graphics);
        GLRenderHelper.popScissor(graphics);
    }


    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {

    }
}
