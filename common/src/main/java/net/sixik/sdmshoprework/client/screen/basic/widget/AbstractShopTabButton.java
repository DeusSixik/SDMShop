package net.sixik.sdmshoprework.client.screen.basic.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.common.utils.ListHelper;
import net.sixik.sdmshoprework.network.server.SendChangeShopTabC2S;
import net.sixik.sdmshoprework.network.server.SendChangesShopC2S;
import net.sixik.sdmshoprework.network.server.create.SendCreateShopTabC2S;
import net.sixik.sdmshoprework.network.server.edit.SendEditShopTabC2S;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractShopTabButton extends SimpleTextButton {

    public ShopTab shopTab;
    public boolean isEdit = false;

    public AbstractShopTabButton(Panel panel, ShopTab shopTab) {
        super(panel, shopTab != null ? shopTab.title : Component.empty(), shopTab != null ? shopTab.getIcon() : Color4I.EMPTY);
        this.shopTab = shopTab;
    }

    public AbstractShopTabButton setEdit() {
        isEdit = true;

        this.title = Component.literal("Create");
        icon = Icons.ADD;
        return this;
    }

    @Override
    public void addMouseOverText(TooltipList list) {

        list.add(getTitle());

        if(shopTab != null && !shopTab.descriptionList.isEmpty()) {
            list.add(Component.empty());

            for (String s : shopTab.descriptionList) {
                list.add(Component.translatable(s));
            }
        }
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        try {
            if (mouseButton.isLeft()) {
                if (isEdit) {
                    new SendCreateShopTabC2S(new ShopTab(ShopBase.CLIENT).serializeNBT()).sendToServer();
                    getShopScreen().refreshWidgets();
                } else {
                    getShopScreen().setSelectedTab(shopTab);
                    getShopScreen().addEntriesButtons();
                }
            } else if (mouseButton.isRight() && SDMShopR.isEditMode() && !isEdit) {
                List<ContextMenuItem> contextMenu = new ArrayList<>();

                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.edit"), Icons.SETTINGS, () -> {
                    ConfigGroup group = new ConfigGroup("sdmr").setNameKey("sidebar_button.sdmr.shop");

                    group.savedCallback = b -> {

                        if (b) {
                            new SendEditShopTabC2S(shopTab.shopTabUUID, shopTab.serializeNBT()).sendToServer();
//                        new SendChangeShopTabC2S(shopTab.shopTabUUID, shopTab.serializeNBT()).sendToServer();
                            getShopScreen().refreshWidgets();
                        }

                        openGui();
                    };

                    ConfigGroup g = group.getGroup("shop").getGroup("tab");
                    shopTab.getConfig(g);
                    new EditConfigScreen(group).openGui();
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.delete"), Icons.REMOVE, () -> {
                    if (getShopScreen().selectedTab != null) {
                        if (getShopScreen().selectedTab.shopTabUUID.equals(shopTab.shopTabUUID)) {
                            getShopScreen().selectedTab = null;
                        }
                    }

                    ShopBase.CLIENT.getShopTabs().remove(shopTab);
                    new SendChangesShopC2S(ShopBase.CLIENT.serializeNBT()).sendToServer();
                    getShopScreen().tabsPanel.refreshWidgets();
                }));


                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.up"), Icons.UP, () -> {
                    moveNew(true);
                    getShopScreen().refreshWidgets();
                }));
                contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.down"), Icons.DOWN, () -> {
                    moveNew(false);
                    getShopScreen().refreshWidgets();
                }));


                getShopScreen().openContextMenu(contextMenu);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void moveNew(boolean isUp){
        try {
            int index = shopTab.getIndex();

            if(isUp) {
                ListHelper.moveUp(ShopBase.CLIENT.getShopTabs(), index);
            }
            else {
                ListHelper.moveDown(ShopBase.CLIENT.getShopTabs(), index);
            }
            new SendChangesShopC2S(ShopBase.CLIENT.serializeNBT()).sendToServer();
            getShopScreen().refreshWidgets();
        } catch (Exception e){
            SDMShopRework.LOGGER.error(e.toString());
        }

    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        super.drawBackground(graphics, theme, x, y, w, h);
    }

    public void drawSelected(PoseStack graphics, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopClient.getTheme().getColorSelectTab(), false);
    }

    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }

}
