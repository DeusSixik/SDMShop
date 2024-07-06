package net.sdm.sdmshopr.client.screen.createEntryScreen;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.network.mainshop.CreateShopEntry;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

import java.util.ArrayList;
import java.util.List;

public class CreateEntryButton extends SimpleTextButton {
    public TextField field;
    public final IEntryType entryType;
    public CreateEntryButton(Panel panel, IEntryType entryType) {
        super(panel, Component.empty(), entryType.getCreativeIcon());
        this.entryType = entryType;
    }


    @Override
    public void addMouseOverText(TooltipList list) {

    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        CreateEntryScreen screen = (CreateEntryScreen) getGui();
        if(mouseButton.isLeft()){
            ShopEntry<IEntryType> create = new ShopEntry<>(screen.screen.selectedTab, entryType.copy(), 1,1,false);
            screen.screen.selectedTab.shopEntryList.add(create);
            screen.refreshWidgets();
            new CreateShopEntry(create).sendToServer();
            screen.closeGui();
        }

        if(mouseButton.isRight()){
            List<ContextMenuItem> contextMenu = new ArrayList<>();
            if(!SDMShopR.ClientModEvents.creator.favoriteCreator.contains(entryType.getID())) {
                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.creator.addfavorite"), Icons.ADD, (b) -> {
                    SDMShopR.ClientModEvents.creator.favoriteCreator.add(entryType.getID());
                    SNBT.write(SDMShopR.getFileClient(), SDMShopR.ClientModEvents.creator.serializeNBT());
                }));
            } else {
                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.creator.removefavorite"), Icons.REMOVE, (b) -> {
                    if(b.mousePressed(MouseButton.LEFT)) {
                        SDMShopR.ClientModEvents.creator.favoriteCreator.remove(entryType.getID());
                        SNBT.write(SDMShopR.getFileClient(), SDMShopR.ClientModEvents.creator.serializeNBT());
                    }
                }));
            }
            screen.openContextMenu(contextMenu);
        }
    }

    @Override
    public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }
}
