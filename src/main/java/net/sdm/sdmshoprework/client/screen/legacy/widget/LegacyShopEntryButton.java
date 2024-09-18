package net.sdm.sdmshoprework.client.screen.legacy.widget;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.sdm.sdmeconomy.api.CurrencyHelper;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sdm.sdmshoprework.client.screen.legacy.createEntry.LegacyCreateEntryScreen;
import net.sixik.sdmuilibrary.client.utils.TextHelper;

public class LegacyShopEntryButton extends AbstractShopEntryButton {

    public LegacyShopEntryButton(Panel panel, AbstractShopEntry entry) {
        super(panel, entry);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);

        if(entry != null && entry.getEntryType() != null) {
            int s = h >= 16 ? 16 : 8;

            Font font = Minecraft.getInstance().font;
            int d = y + (this.height - (font.lineHeight * 2)) - 4;

            theme.drawString(graphics,
                    entry.isSell ?
                            Component.translatable("sdm.shop.entry.sell") :
                            Component.translatable("sdm.shop.entry.buy"),

                    x + ((this.width / 2) - (int) (entry.isSell ? font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.sell")) / 2 : font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.buy")) / 2)),
                    d
            );


            String textMoney = SDMShopRework.moneyString(entry.entryPrice);
            int ww1 = TextHelper.getTextWidth(textMoney);
            int w1 = this.width - ww1;
            int w2 = w1 / 2;

            theme.drawString(graphics, textMoney, x + w2, d + font.lineHeight + 2, SDMShopClient.getTheme().getMoneyTextColor(), 0);
        }

        if(isSelected()){
            drawSelected(graphics, x, y, w, h);
        }
    }

    @Override
    public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        super.drawIcon(graphics, theme, x, y - h / 2 - 2, w, h);
    }
}
