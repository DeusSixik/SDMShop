package net.sdm.sdmshopr.api.customization;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.client.EntryPanel;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.client.buyer.BuyerScreen;
import net.sdm.sdmshopr.network.mainshop.EditShopEntry;
import net.sdm.sdmshopr.network.mainshop.MoveShopEntry;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;
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
            list.add(new TranslatableComponent(shopEntry.tittle));
            list.add(TextComponent.EMPTY);
        } else {
            if(!shopEntry.type.getIconNBT().isEmpty()){

                ItemStack itemStack = NBTUtils.getItemStack(shopEntry.type.getIconNBT(), "item");
                List<Component> list1 = new ArrayList<>();
                GuiHelper.addStackTooltip(itemStack, list1);
                list1.forEach(list::add);

                list.add(TextComponent.EMPTY);
            }
        }

        list.add(shopEntry.isSell ? new TranslatableComponent("sdm.shop.entry.sell") : new TranslatableComponent("sdm.shop.entry.buy"));
        list.add(TextComponent.EMPTY);
        list.add(new TranslatableComponent("sdm.shop.entry.tooltips.price", shopEntry.price));
        list.add(new TranslatableComponent("sdm.shop.entry.tooltips.sellcount", shopEntry.count));
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

            contextMenu.add(new ContextMenuItem(new TranslatableComponent("sdm.shop.entry.context.edit"), Icons.SETTINGS, () -> {
                ConfigGroup group = new ConfigGroup("sdmr").setNameKey("sidebar_button.sdmr.shop");

                group.savedCallback = s -> {
                    openGui();
                  if(s){
                      new EditShopEntry(shopEntry, false).sendToServer();
                  }
                };

                ConfigGroup g = group.getGroup("shop").getGroup("entry");
                shopEntry.getConfig(g);
                new EditConfigScreen(group).openGui();
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(new TranslatableComponent("sdm.shop.entry.context.delete"), Icons.REMOVE, () -> {
                new EditShopEntry(shopEntry, true).sendToServer();
                shopEntry.tab.shopEntryList.remove(shopEntry);
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(new TranslatableComponent("sdm.shop.entry.context.move.up"), Icons.UP, () -> {
                moveNew(screen,true);
            }));
            contextMenu.add(new ContextMenuItem(new TranslatableComponent("sdm.shop.entry.context.move.down"), Icons.DOWN, () -> {
                moveNew(screen, false);
            }));

            screen.openContextMenu(contextMenu);
        }
    }

    public void moveNew(MainShopScreen screen, boolean isUp){
        int entryId = shopEntry.getIndex();
        ShopTab d1 = shopEntry.tab;
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
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }
}
