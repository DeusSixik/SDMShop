package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.client.buyer.BuyerScreen;
import net.sdm.sdmshopr.network.EditShopEntry;
import net.sdm.sdmshopr.network.MoveShopEntry;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class EntryButton extends SimpleTextButton {

    public ShopEntry<?> entry;
    public EntryButton(Panel panel, ShopEntry<?> entry) {
        super(panel, Component.empty(), entry.type.getIcon());
        this.entry = entry;
    }

    @Override
    public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {return;}

    @Override
    public void onClicked(MouseButton mouseButton) {
        playClickSound();
        MainShopScreen screen = (MainShopScreen) getGui();

        if(mouseButton.isLeft()){
            new BuyerScreen(entry).openGui();
        }

        if(mouseButton.isRight() && SDMShopR.isEditModeClient()){
            List<ContextMenuItem> contextMenu = new ArrayList<>();

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.edit"), Icons.SETTINGS, () -> {
                ConfigGroup group = new ConfigGroup("sdmr", b -> {
                    openGui();

                    if(b){
                        new EditShopEntry(entry, false).sendToServer();
                    }
                }).setNameKey("sidebar_button.sdmr.shop");


                ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
                entry.getConfig(g);
                new EditConfigScreen(group).openGui();
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.delete"), Icons.REMOVE, () -> {
                new EditShopEntry(entry, true).sendToServer();
                entry.tab.shopEntryList.remove(entry);
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.up"), Icons.UP, () -> {
                moveNew(screen,true);
            }));
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.down"), Icons.DOWN, () -> {
                moveNew(screen, false);
            }));

            screen.openContextMenu(contextMenu);
        }
    }

    public void moveNew(MainShopScreen screen, boolean isUp){
        int entryId = entry.getIndex();
        ShopTab d1 = entry.tab;
        int newIndex = entryId;
        if(isUp) {
            newIndex -= 1;
        } else{
            newIndex += 1;
        }
        if (entryId < 0 || entryId >= d1.shopEntryList.size() || newIndex < 0 || newIndex >= d1.shopEntryList.size()) {
            SDMShopR.LOGGER.error("[MOVE] Index a broken !");
            return;
        }
        ShopEntry<?> f1 = d1.shopEntryList.get(entryId);
        ShopEntry<?> f2 = d1.shopEntryList.get(newIndex);
        d1.shopEntryList.set(newIndex, f1);
        d1.shopEntryList.set(entryId, f2);
        new MoveShopEntry(d1.getIndex(), entryId, isUp).sendToServer();
        screen.refreshWidgets();

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        Font font = Minecraft.getInstance().font;
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);

        GuiHelper.drawHollowRect(graphics, x, y, 18, 20, SDMShopRClient.shopTheme.getReact(), false);

        icon.draw(graphics, x + 1,y + 1, 16,16);
        theme.drawString(graphics, I18n.get("sdm.shop.entry.render.count", entry.count), x + 18, y + 6);
        theme.drawString(graphics, SDMShopR.moneyString(entry.price), x + 2, y + (this.height - font.lineHeight * 2 - 1));

        SDMShopRClient.shopTheme.getReact().draw(graphics, x, y + (this.height - font.lineHeight - 2), this.width, 1);

        theme.drawString(graphics,
                entry.isSell ? Component.translatable("sdm.shop.entry.sell") : Component.translatable("sdm.shop.entry.buy"),
                x + ((this.width / 2) - (int) (entry.isSell ? font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.sell")) / 2 : font.getSplitter().stringWidth(I18n.get("sdm.shop.entry.buy")) / 2)),
                y + (this.height - font.lineHeight)
        );


    }

    @Override
    public void addMouseOverText(TooltipList list) {
        if(!entry.tittle.isEmpty()){
            list.add(Component.translatable(entry.tittle));
            list.add(Component.empty());
        } else {
            if(!entry.type.getIconNBT().isEmpty()){

                ItemStack itemStack = NBTUtils.getItemStack(entry.type.getIconNBT(), "item");
                List<Component> list1 = new ArrayList<>();
                GuiHelper.addStackTooltip(itemStack, list1);
                list1.forEach(list::add);

                list.add(Component.empty());
            }
        }

        list.add(entry.isSell ? Component.translatable("sdm.shop.entry.sell") : Component.translatable("sdm.shop.entry.buy"));
        list.add(Component.empty());
        list.add(Component.translatable("sdm.shop.entry.tooltips.price", entry.price));
        list.add(Component.translatable("sdm.shop.entry.tooltips.sellcount", entry.count));
    }
}
