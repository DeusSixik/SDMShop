package net.sdm.sdmshoprework.client.screen.modern.widget;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sdm.sdmshoprework.client.screen.modern.ModernShopScreen;
import net.sdm.sdmshoprework.client.screen.modern.buyer.ModernBuyerScreen;
import net.sixik.sdmuilibrary.client.utils.GLHelper;
import net.sixik.sdmuilibrary.client.utils.TextHelper;
import net.sixik.sdmuilibrary.client.utils.math.Vector2;
import net.sixik.sdmuilibrary.client.utils.math.Vector2f;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;

public class ModernShopEntryButton extends AbstractShopEntryButton {

    public ModernShopEntryButton(Panel panel, AbstractShopEntry entry) {
        super(panel, entry);
    }


    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0,255 / 3).drawRoundFill(graphics, x,y,w,h, 6);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
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


            textSize = TextHelper.getTextRenderSize(component.getString(), w, 0.7f, 50);
            textWidth = (int) TextHelper.getTextWidth(component.getString(), textSize.y);
            centeredX = x + 2 + (w - 4 - textWidth) / 2;
            pos = new Vector2(centeredX, y + h - Minecraft.getInstance().font.lineHeight - 1);


            GLHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0);
            theme.drawString(graphics,
                    component,
                    pos.x, pos.y
            );
            GLHelper.popTransform(graphics);

            String textMoney = SDMShopRework.moneyString(entry.entryPrice);


            textSize = TextHelper.getTextRenderSize(textMoney, w - 4, 0.7f, 50);

            textWidth = (int) TextHelper.getTextWidth(textMoney, textSize.y);

            centeredX = x + 2 + (w - 4 - textWidth) / 2;

            pos = new Vector2(centeredX, y + w + 2 + 1);
            GLHelper.pushScissor(graphics, pos.x, pos.y, w - 4, h - 4);

            GLHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0);
            theme.drawString(graphics, textMoney, pos.x, pos.y, theme.getContentColor(this.getWidgetType()), 2);
            GLHelper.popTransform(graphics);
            GLHelper.popScissor(graphics);
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
