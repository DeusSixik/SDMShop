package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.api.customization.APIShopEntryButton;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

public class EntryButton extends APIShopEntryButton {

    public ShopEntry<?> entry;

    public EntryButton(Panel panel, ShopEntry<?> entry) {
        super((EntryPanel) panel, entry, Component.empty(), entry.type.getIcon());
        this.entry = entry;
    }

    @Override
    public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {return;}

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        Font font = Minecraft.getInstance().font;
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);

        GuiHelper.drawHollowRect(graphics, x, y, 18, 20, SDMShopRClient.shopTheme.getReact(), false);

        icon.draw(graphics, x + 1,y + 1, 16,16);

        SDMShopRClient.shopTheme.getReact().draw(graphics, x, y + (this.height - font.lineHeight - 2), this.width, 1);


        for (String tag : entry.TAGS) {
            if(SDMShopR.ClientModEvents.tags.containsKey(tag)){
                SDMShopR.ClientModEvents.tags.get(tag).executeClient(graphics,SDMShopRClient.shopTheme,x,y,w,h);
            }
        }

        theme.drawString(graphics, entry.count, x + 19, y + 2);
        theme.drawString(graphics, Component.translatable("sdm.shop.entry.render.count"), x + 19, y + 11);
        theme.drawString(graphics, SDMShopR.moneyString(entry.price), x + 2, y + (this.height - font.lineHeight * 2 - 1));

        if(entry.isHaveLimit()) {
            theme.drawString(graphics,
                    !entry.isHaveMore() ? Component.translatable("sdm.shop.entry.sold") : entry.isSell ? Component.translatable("sdm.shop.entry.sell") : Component.translatable("sdm.shop.entry.buy"),
                    x + ((this.width / 2) - (int) (entry.isSell ? font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.sell")) / 2 : font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.buy")) / 2)),
                    y + (this.height - font.lineHeight)
            );
        } else {
            theme.drawString(graphics,
                    entry.isSell ? Component.translatable("sdm.shop.entry.sell") : Component.translatable("sdm.shop.entry.buy"),
                    x + ((this.width / 2) - (int) (entry.isSell ? font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.sell")) / 2 : font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.buy")) / 2)),
                    y + (this.height - font.lineHeight)
            );
        }



    }
}
