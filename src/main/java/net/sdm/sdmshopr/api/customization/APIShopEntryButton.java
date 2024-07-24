package net.sdm.sdmshopr.api.customization;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.client.EntryPanel;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.client.buyer.BuyerScreen;
import net.sdm.sdmshopr.network.mainshop.EditShopEntry;
import net.sdm.sdmshopr.network.mainshop.MoveShopEntry;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;
import net.sdm.sdmshopr.utils.ListHelper;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;


public class APIShopEntryButton extends SimpleTextButton {

    public int sizeButton = 48;
    public EntryPanel entryPanel;
    public ShopEntry<?> shopEntry;

    public APIShopEntryButton(EntryPanel entryPanel, ShopEntry<?> shopEntry, Component title, Icon icon) {
        super(entryPanel, title, icon);
        this.entryPanel = entryPanel;
        this.shopEntry = shopEntry;
        setSize(sizeButton,sizeButton);
    }

    public void setSizeButton(int sizeButton) {
        this.sizeButton = sizeButton;
    }

    public int getSizeButton() {
        return sizeButton;
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        if(!shopEntry.tittle.isEmpty()){
            list.add(Component.translatable(shopEntry.tittle));
            list.add(Component.empty());
        } else {
            if(!shopEntry.type.getIconNBT().isEmpty()){

                ItemStack itemStack = NBTUtils.getItemStack(shopEntry.type.getIconNBT(), "item");
                List<Component> list1 = new ArrayList<>();
                GuiHelper.addStackTooltip(itemStack, list1);
                list1.forEach(list::add);

                list.add(Component.empty());
            }
        }

        list.add(shopEntry.isSell ? Component.translatable("sdm.shop.entry.sell") : Component.translatable("sdm.shop.entry.buy"));
        list.add(Component.empty());
        list.add(Component.translatable("sdm.shop.entry.tooltips.price", shopEntry.price));
        list.add(Component.translatable("sdm.shop.entry.tooltips.sellcount", shopEntry.count));
        if(shopEntry.isHaveLimit()) {
            list.add(Component.empty());
            list.add(Component.translatable("sdm.shop.entry.tooltips.entryleft").append(": ").append(shopEntry.getLeftEntry() + "/" + shopEntry.getLimitOnEntry()));
        }
    }


    @Override
    public void onClicked(MouseButton mouseButton) {
        playClickSound();
        MainShopScreen screen = (MainShopScreen) getGui();

        if(mouseButton.isLeft()){
            new BuyerScreen(shopEntry).openGui();
        }

        if(mouseButton.isRight() && SDMShopR.isEditModeClient()){
            List<ContextMenuItem> contextMenu = new ArrayList<>();

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.edit"), Icons.SETTINGS, (button) -> {
                ConfigGroup group = new ConfigGroup("sdmr", b -> {
                    openGui();

                    if(b){
                        new EditShopEntry(shopEntry, false).sendToServer();
                    }
                }).setNameKey("sidebar_button.sdmr.shop");


                ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
                shopEntry.getConfig(g);
                new EditConfigScreen(group).openGui();
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.delete"), Icons.REMOVE, (b) -> {
                new EditShopEntry(shopEntry, true).sendToServer();
                shopEntry.tab.shopEntryList.remove(shopEntry);
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.up"), Icons.UP, (b) -> {
                moveNew(screen,true);
            }));
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.down"), Icons.DOWN, (b) -> {
                moveNew(screen, false);
            }));

            screen.openContextMenu(contextMenu);
        }
    }

    public void moveNew(MainShopScreen screen, boolean isUp){
        int entryId = shopEntry.getIndex();

        ShopTab d1 = shopEntry.tab;
        if(isUp) {
            ListHelper.moveUp(d1.shopEntryList, entryId);
        }
        else {
            ListHelper.moveDown(d1.shopEntryList, entryId);
        }
        new MoveShopEntry(d1.getIndex(), entryId, isUp).sendToServer();
        screen.refreshWidgets();

    }
    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }
}
