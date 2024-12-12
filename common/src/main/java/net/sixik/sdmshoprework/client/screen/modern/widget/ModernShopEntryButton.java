package net.sixik.sdmshoprework.client.screen.modern.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sixik.sdmshoprework.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshoprework.client.screen.modern.buyer.ModernBuyerScreen;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.GLRenderHelper;
import net.sixik.v2.render.TextRenderHelper;
import net.sixik.v2.utils.math.Vector2;
import net.sixik.v2.utils.math.Vector2f;

public class ModernShopEntryButton extends AbstractShopEntryButton {

    public ModernShopEntryButton(Panel panel, AbstractShopEntry entry) {
        super(panel, entry);
    }


    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        if(isSelected()) {
            RGBA.create(255, 255, 255, 255 / 3).drawRoundFill(graphics, x, y, w, h, 6);
        } else
            RGBA.create(0, 0, 0, 255 / 3).drawRoundFill(graphics, x, y, w, h, 6);
    }

    @Override
    public void draw(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();
        int s = h >= 16 ? 16 : 8;
        this.drawBackground(graphics, theme, x, y, w, h);

        int size = 0;





        if(entry != null) {
            size = 16;
            this.drawIcon(graphics, theme, x + size / 2, y + 2, w - size, h - size);

            RGBA.create(0,0,0,255 / 3).drawRoundFill(graphics, x + 2,y + w + 2, w - 4,8, 2);

            Vector2 pos = new Vector2(0,0);
            int centeredX = 0;
            Vector2f textSize = new Vector2f(0,0);
            int textWidth = 0;

            Component component = entry.isSell ?
                    Component.translatable("sdm.shop.entry.sell") :
                    Component.translatable("sdm.shop.entry.buy");


            textSize = TextRenderHelper.getTextRenderSize(component.getString(), w, 0.7f, 50);
            textWidth = (int) TextRenderHelper.getTextWidth(component.getString(), textSize.y);
            centeredX = x + 2 + (w - 4 - textWidth) / 2;
            pos = new Vector2(centeredX, y + h - Minecraft.getInstance().font.lineHeight - 1);


            GLRenderHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0);
            theme.drawString(graphics,
                    component,
                    pos.x, pos.y
            );
            GLRenderHelper.popTransform(graphics);

            String textMoney = SDMShopRework.moneyString(entry.entryPrice);


            textSize = TextRenderHelper.getTextRenderSize(textMoney, w - 4, 0.7f, 50);

            textWidth = (int) TextRenderHelper.getTextWidth(textMoney, textSize.y);

            centeredX = x + 2 + (w - 4 - textWidth) / 2;

            pos = new Vector2(centeredX, y + w + 2 + 1);
            GLRenderHelper.pushScissor(graphics, pos.x, pos.y, w - 4, h - 4);

            GLRenderHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0);
            theme.drawString(graphics, textMoney, pos.x, pos.y, theme.getContentColor(this.getWidgetType()), 2);
            GLRenderHelper.popTransform(graphics);
            GLRenderHelper.popScissor(graphics);
        } else {
            size = this.height / 2;
            this.drawIcon(graphics, theme, x + size / 2, y + size / 2, w - size, h - size);
        }
    }

    @Override
    public void openBuyScreen() {
        new ModernBuyerScreen((ModernShopScreen) getShopScreen(), this).openGui();
    }
}
