package net.sixik.sdmshop.client.screen.base.widgets;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.ShopEntryType;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopEntryTypeCreator;
import net.sixik.sdmshop.utils.ShopInputHelper;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractShopEntryButton extends SimpleTextButton {

    protected ShopEntry shopEntry;
    protected boolean edit;

    protected boolean favorite = false;

    public AbstractShopEntryButton(Panel panel, ShopEntry entry) {
        this(panel, entry, false);
    }

    public AbstractShopEntryButton(Panel panel, ShopEntry entry, boolean isEdit) {
        super(panel, Component.empty(), getIconFromEntry(entry));
        this.shopEntry = entry;
        this.edit = isEdit;

        if(edit) {
            icon = Icons.ADD;
            title = Component.literal("Create");
        } else {
            favorite = ShopUtilsClient.isFavorite(shopEntry);
        }

    }

    @Override
    public void addMouseOverText(TooltipList list) {
        if(shopEntry == null) return;

        shopEntry.addTooltipToList(list);
        shopEntry.getEntryType().addEntryTooltip(list, shopEntry);
        shopEntry.getEntrySellerType().addEntryTooltip(list, shopEntry);
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        AbstractShopScreen screen = getShopScreen();
        BaseShop shop = screen.currentShop;
        boolean isClientEdit = ShopUtils.isEditModeClient();

        if(mouseButton.isLeft()) {
            
            if(isEdit()) {
                if(SDMShopClient.userData.getCreator().isEmpty())
                    screen.openCreateEntryScreen();
                else
                    screen.openContextMenu(ShopEntryTypeCreator.createContext(screen));

                return;
            } else if(ShopUtils.isEditModeClient()) {

                if (ShopInputHelper.isControl()) {

                    if (screen.selectedEntryId == null) {
                        screen.selectedEntryId = shopEntry.getId();
                        return;
                    }
                    else if (Objects.equals(screen.selectedEntryId, shopEntry.getId())) {
                        screen.selectedEntryId = null;
                        return;
                    } else {

                        MoveType type = ShopInputHelper.isMoveInsert() ? MoveType.Insert : ShopInputHelper.isMoveSwap() ? MoveType.Swap : null;

                        if(type != null) {
                            ShopUtilsClient.swapShopEntries(shop, screen.selectedEntryId, shopEntry.getId(), type);
                            screen.selectedEntryId = null;
                        }
                        return;
                    }
                } else if (ShopInputHelper.isShift()) {

                    if(shopEntry.getEntryType().getProperty().sellType.isBoth()) {
                        ShopUtilsClient.changeEntry(shop, shopEntry, entry -> {
                            entry.changeType(shopEntry.getType().isSell() ? ShopEntryType.Buy : ShopEntryType.Sell);
                        });

                        return;
                    }
                }
            }

            screen.openBuyScreen(this);
        }

        if(mouseButton.isRight()) {

            List<ContextMenuItem> contextMenu = new ArrayList<>();

            if(isClientEdit && !isEdit()) {

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.EDIT_KEY), Icons.SETTINGS, (button) -> {
                    ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                        if (accept)
                            ShopUtilsClient.syncEntry(shop, shopEntry);
                        screen.openGui();
                    }).setNameKey("sidebar_button.sdm.shop");

                    ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
                    shopEntry.getConfig(g);
                    new SDMEditConfigScreen(group).openGui();
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.DUPLICATE_KEY), Icons.ADD, (b) -> {
                    ShopUtilsClient.addEntry(shop, shopEntry.copy());
                 }));

                TooltipList d1List = new TooltipList();
                d1List.add(Component.literal("Copy " + shopEntry.getId()));
                ContextMenuItem cont = new ContextMenuItem(Component.translatable(SDMShopConstants.COPY_ID_KEY), Icons.INFO, (b) -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(shopEntry.getId().toString());
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("Copy Shop Entry " + shopEntry.getId()));
                });
                cont.addMouseOverText(d1List);
                contextMenu.add(cont);

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.DELETE_KEY), Icons.REMOVE, (b) -> {
                    ShopUtilsClient.removeEntry(shop, shopEntry);
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.RESET_LIMITER_KEY), Icons.BOOK_RED, (b) -> {
                    new SendResetLimiterC2S(shopEntry.getId(), ShopObjectTypes.SHOP_ENTRY).sendToServer();
                }));

                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_UP_KEY), Icons.UP, (b) -> {
                    ShopUtilsClient.moveShopEntry(shop, shopEntry.getId(), MoveType.Up);
                }));
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_DOWN_KEY), Icons.DOWN, (b) -> {
                    ShopUtilsClient.moveShopEntry(shop, shopEntry.getId(), MoveType.Down);
                }));

            }

            if(!isEdit()) {
                if (isFavorite()) {
                    contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.UN_FAVORITE_KEY), ShopUtilsClient.FAVORITE_ICON.withColor(Color4I.GRAY), (b) -> {
                        ShopUtilsClient.removeFavorite((shopEntry));
                        favorite = false;
                        screen.onRefresh();
                    }));
                } else {
                    contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.FAVORITE_KEY), ShopUtilsClient.FAVORITE_ICON, (b) -> {
                        ShopUtilsClient.addFavorite(shopEntry);
                        favorite = true;
                        screen.onRefresh();
                    }));
                }
            }


            if(!contextMenu.isEmpty()) {
                screen.openContextMenu(contextMenu);
                return;
            }
        }

    }

    public boolean isEdit() {
        return edit;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public ShopEntry getShopEntry() {
        return shopEntry;
    }

    public boolean isSelected(){
        return getShopScreen().selectedEntryId != null && shopEntry != null && Objects.equals(getShopScreen().selectedEntryId, shopEntry.getId());
    }

    public void drawSelected(GuiGraphics graphics, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.WHITE, false);
    }

    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }

    public void drawFavorite(GuiGraphics graphics, int x, int y, int w, int h) {
        if(isFavorite()) {
            int size = w >= 20 ? 8 : 4;
            ShopUtilsClient.FAVORITE_ICON.draw(graphics, x + w - size, y, size, size);
        }
    }

    public static Icon getIconFromEntry(ShopEntry entry) {
        return getIconFromEntry(entry, ShopUtilsClient.getTick());
    }

    public static Icon getIconFromEntry(ShopEntry entry, int tick) {
       if(entry == null) return Icon.empty();

       Icon i1 = null;

       if(entry.getRenderComponent().getIcon().isEmpty() || (entry.getRenderComponent().getIcon() instanceof ItemIcon itemIcon && itemIcon.isEmpty())) {

           if(entry.getEntryType() instanceof CustomIcon customIcon)
               i1 = customIcon.getCustomIcon(entry, tick);

       }

       if(i1 == null)
           i1 = entry.getRenderComponent().getIcon();

       return i1;
    }
}
