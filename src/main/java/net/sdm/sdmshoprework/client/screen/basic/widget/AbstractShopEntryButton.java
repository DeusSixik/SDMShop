package net.sdm.sdmshoprework.client.screen.basic.widget;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.api.shop.AbstractShopTab;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sdm.sdmshoprework.client.screen.legacy.buyer.LegacyBuyerScreen;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.common.shop.type.ShopItemEntryType;
import net.sdm.sdmshoprework.common.utils.ListHelper;
import net.sdm.sdmshoprework.common.utils.TypeCreator;
import net.sdm.sdmshoprework.network.server.SendChangesShopEntriesC2S;
import net.sdm.sdmshoprework.network.server.edit.SendEditShopEntryC2S;
import net.sdm.sdmshoprework.network.server.edit.SendEditShopTabC2S;
import net.sdm.sdmshoprework.network.server.move.SendMoveShopEntryC2S;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractShopEntryButton extends SimpleTextButton {


    public AbstractShopEntry entry;
    public boolean isEdit = false;

    public AbstractShopEntryButton(Panel panel, AbstractShopEntry entry) {
        super(
                panel,
                Component.empty(),
                entry == null ? Icon.empty() : entry.getEntryType().getIcon()
        );
        this.entry = entry;
    }

    @Override
    public void addMouseOverText(TooltipList list) {

        if(entry != null && entry.getEntryType() != null) {

            if(entry.getEntryType() instanceof ShopItemEntryType entryType) {
                List<Component> list1 = new ArrayList<>();
                GuiHelper.addStackTooltip(entryType.itemStack, list1);
                list1.forEach(list::add);
            }

            if(!entry.descriptionList.isEmpty()) {
                list.add(Component.empty());
                entry.descriptionList.stream().map(Component::translatable).forEach(list::add);
            }
        }

    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        try {
            if(mouseButton.isLeft()) {

                    if (isEdit) {
                        List<ContextMenuItem> contextMenu = TypeCreator.createContext(getShopScreen());
                        getShopScreen().openContextMenu(contextMenu);
                        return;
                    }

                    if (Screen.hasControlDown() && SDMShopR.isEditMode()) {
                        if (getShopScreen().selectedEntryID == null) {
                            getShopScreen().selectedEntryID = entry.entryUUID;
                        } else if (Objects.equals(getShopScreen().selectedEntryID, entry.entryUUID)) {
                            getShopScreen().selectedEntryID = null;
                        } else {
                            ListHelper.swap(entry.getShopTab().getTabEntry(), entry.getShopTab().getShopEntry(getShopScreen().selectedEntryID).getIndex(), entry.getIndex());
                            new SendMoveShopEntryC2S(entry.getShopTab().shopTabUUID, entry.getShopTab().getShopEntry(getShopScreen().selectedEntryID).getIndex(), entry.getIndex()).sendToServer();
                            getShopScreen().selectedEntryID = null;
                            getShopScreen().refreshWidgets();
                        }
                    } else if (Screen.hasShiftDown() && SDMShopR.isEditMode()) {
                        if (entry.getEntryType().getSellType() == AbstractShopEntryType.SellType.BOTH) {
                            entry.isSell = !entry.isSell;
                            new SendChangesShopEntriesC2S(getShopScreen().selectedTab.shopTabUUID, ShopBase.CLIENT.getShopTab(getShopScreen().selectedTab.shopTabUUID).serializeNBT()).sendToServer();
                            getShopScreen().refreshWidgets();
                        }
                    } else {
                        new LegacyBuyerScreen(entry).openGui();
                        getShopScreen().refreshWidgets();
                    }
            }


            if(mouseButton.isRight() && SDMShopR.isEditMode() && !isEdit) {

                List<ContextMenuItem> contextMenu = new ArrayList<>();

                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.edit"), Icons.SETTINGS, (button) -> {
                    ConfigGroup group = new ConfigGroup("sdmr", b -> {
                        openGui();

                        if (b) {
                            new SendEditShopEntryC2S(entry.getShopTab().shopTabUUID, entry.entryUUID, entry.serializeNBT()).sendToServer();
                            getShopScreen().refreshWidgets();
    //                        new SendEditShopEntryC2S(entry.getShopTab().shopTabUUID, entry.entryUUID, entry.serializeNBT()).sendToServer();
                        }
                    }).setNameKey("sidebar_button.sdmr.shop");


                    ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
                    entry.getConfig(g);
                    new EditConfigScreen(group).openGui();
                    getShopScreen().refreshWidgets();
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.delete"), Icons.REMOVE, (b) -> {
                    entry.getShopTab().getTabEntry().remove(entry);
                    new SendChangesShopEntriesC2S(getShopScreen().selectedTab.shopTabUUID, ShopBase.CLIENT.getShopTab(getShopScreen().selectedTab.shopTabUUID).serializeNBT()).sendToServer();
                    getShopScreen().refreshWidgets();
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.up"), Icons.UP, (b) -> {
                    moneEntry(true);
                    getShopScreen().refreshWidgets();
                }));
                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.down"), Icons.DOWN, (b) -> {
                    moneEntry(false);
                    getShopScreen().refreshWidgets();
                }));

                getShopScreen().openContextMenu(contextMenu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCreateSceen(){

    }

    public AbstractShopEntryButton setEdit() {
        isEdit = true;

        icon = Icons.ADD;

        return this;
    }

    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }

    public void moneEntry(boolean isUp){
        int entryId = entry.getIndex();

        AbstractShopTab d1 = entry.getShopTab();
        if(isUp) {
            ListHelper.moveUp(d1.getTabEntry(), entryId);
        }
        else {
            ListHelper.moveDown(d1.getTabEntry(), entryId);
        }
        new SendChangesShopEntriesC2S(getShopScreen().selectedTab.shopTabUUID, ShopBase.CLIENT.getShopTab(getShopScreen().selectedTab.shopTabUUID).serializeNBT()).sendToServer();

    }

    public boolean isSelected(){
        return getShopScreen().selectedEntryID != null && entry != null && Objects.equals(getShopScreen().selectedEntryID, entry.entryUUID);
    }

    public void drawSelected(GuiGraphics graphics, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.WHITE, false);
    }
}
