package net.sdm.sdmshopr.customization.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.api.customization.APIShopEntryButton;
import net.sdm.sdmshopr.client.EntryPanel;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

public class LegacyEntryButton extends APIShopEntryButton {

    public LegacyEntryButton(Panel entryPanel, ShopEntry<?> shopEntry) {
        super((EntryPanel) entryPanel, shopEntry, TextComponent.EMPTY, shopEntry.type.getIcon());
    }


    @Override
    public void drawIcon(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        return;
    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        super.drawBackground(graphics, theme, x, y, w, h);
        Font font = Minecraft.getInstance().font;



        for (String tag : shopEntry.TAGS) {
            if(SDMShopR.ClientModEvents.tags.containsKey(tag)){
                SDMShopR.ClientModEvents.tags.get(tag).executeClient(graphics, SDMShopRClient.shopTheme,x,y,w,h);
            }
        }

        if(!shopEntry.type.getIconNBT().isEmpty()) {
            ItemStack item = NBTUtils.getItemStack(shopEntry.type.getIconNBT(), "item");
            item.setCount(shopEntry.count);
            ItemIcon.getItemIcon(item).draw(graphics, x + (w / 2) - 12, y + 4, 24, 24);
        } else {
            icon.draw(graphics, x + (w / 2) - 12, y + 4, 24, 24);
            theme.drawString(graphics, String.valueOf(shopEntry.count), x + (w / 2) - 8 + 14, y + 14);
        }


        theme.drawString(graphics,
                shopEntry.isSell ? new TranslatableComponent("sdm.shop.entry.sell") : new TranslatableComponent("sdm.shop.entry.buy"),
                x + ((this.width / 2) - (int) (shopEntry.isSell ? font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.sell")) / 2 : font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.buy")) / 2)),
                y + (this.height - (font.lineHeight * 2))
        );

        theme.drawString(graphics, SDMShopR.moneyString(shopEntry.price), x + 2, y + (this.height - font.lineHeight));

    }
}
