package net.sixik.sdmshop.client.screen.modern.widgets;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.math.Vector2f;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopEntryButton extends AbstractShopEntryButton {


    public ModernShopEntryButton(Panel panel, ShopEntry entry) {
        super(panel, entry);
    }

    public ModernShopEntryButton(Panel panel, ShopEntry entry, boolean isEdit) {
        super(panel, entry, isEdit);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(isSelected()) {
            RGBA.create(255, 255, 255, 255 / 3).drawRoundFill(graphics, x, y, w, h, 6);
        } else
            RGBA.create(0, 0, 0, 255 / 3).drawRoundFill(graphics, x, y, w, h, 6);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();
        this.drawBackground(graphics, theme, x, y, w, h);

        int size;

        if(shopEntry != null) {
            size = 16;



            getIconFromEntry(shopEntry).draw(graphics, x + size / 2, y + 2, w - size, h - size);

            RGBA.create(0,0,0,255 / 3).drawRoundFill(graphics, x + 2,y + w + 2, w - 4,8, 2);

            Vector2 pos;
            int centeredX;
            Vector2f textSize;
            int textWidth;

            Component component = shopEntry.getType().isSell() ?
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



            String textMoney = shopEntry.getEntrySellerType().moneyToString(shopEntry);
            textSize = TextHelper.getTextRenderSize(textMoney, w - 4, 0.7f, 50);


            textWidth = shopEntry.getEntrySellerType().getRenderWight(graphics, theme, pos.x, pos.y, w, 16, shopEntry.getPrice(), this, 0);

            centeredX = ShopRenderUtils.centerTextX(w - 4, textWidth, x, textSize.y);

            pos = new Vector2(centeredX, y + w + 2 + 1);

            GLHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0);
            shopEntry.getEntrySellerType().draw(graphics, theme, pos.x, pos.y, w, 16, shopEntry.getPrice(), this, 0);

            GLHelper.popTransform(graphics);
        } else {
            size = this.height / 2;
            this.drawIcon(graphics, theme, x + size / 2, y + size / 2, w - size, h - size);
        }

        drawFavorite(graphics, x, y, w, h);
    }
}
