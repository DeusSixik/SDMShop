package net.sixik.sdmshop.client.screen.base.widgets;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
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
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbstractShopTabButton extends SimpleTextButton {

    protected ShopTab shopTab;
    protected boolean edit;

    public AbstractShopTabButton(Panel panel, ShopTab shopTab) {
        this(panel, shopTab, false);
    }

    public AbstractShopTabButton(Panel panel, ShopTab shopTab, boolean edit) {
        super(panel, shopTab != null ? shopTab.title : Component.empty(), shopTab != null ? shopTab.getRenderComponent().getIcon() : Icon.empty());
        this.edit = edit;
        this.shopTab = shopTab;


        if(edit) {
            icon = Icons.ADD;
            title = Component.literal("Create");
        }
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        if(shopTab == null) return;

        if(shopTab.title != null && !shopTab.title.getString().isEmpty())
            list.add(shopTab.title);

        shopTab.addTooltipToList(list);
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        AbstractShopScreen screen = getShopScreen();
        BaseShop shop = screen.currentShop;
        boolean isClientEdit = ShopUtils.isEditModeClient();

        if(mouseButton.isLeft()) {
            if(isEdit()) ShopUtilsClient.addTab(shop, new ShopTab(shop));
            else         screen.selectTab(shopTab.getId());

            return;
        }

        if(mouseButton.isRight()) {

            if(isClientEdit && !isEdit()) {
                List<ContextMenuItem> contextMenu = new ArrayList<>();

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.EDIT_KEY), Icons.SETTINGS, (button) -> {
                    ConfigGroup group = new SDMConfigGroup("sdm", b -> {
                        if (b) ShopUtilsClient.syncTab(shop, shopTab);
                        screen.openGui();
                    }).setNameKey("sidebar_button.sdm.shop");


                    ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
                    shopTab.getConfig(g);
                    new SDMEditConfigScreen(group).openGui();
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.DELETE_KEY), Icons.REMOVE, (b) -> {
                    if (screen.selectedTab != null) {
                        if (Objects.equals(screen.selectedTab, shopTab.getId())) {
                            screen.selectedTab = null;
                        }
                    }

                    ShopUtilsClient.removeTab(shop, shopTab);
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_UP_KEY), Icons.UP, (b) -> {
                    ShopUtilsClient.moveShopTab(shop, shopTab.getId(), MoveType.Up);
                }));
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_DOWN_KEY), Icons.DOWN, (b) -> {
                    ShopUtilsClient.moveShopTab(shop, shopTab.getId(), MoveType.Down);
                }));

                screen.openContextMenu(contextMenu);
            }

        }
    }


    public boolean isEdit() {
        return edit;
    }

    public ShopTab getShopTab() {
        return shopTab;
    }

    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }


    public void drawSelected(GuiGraphics graphics, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.WHITE, false);
    }
}
