package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

import java.util.List;
import java.util.function.Consumer;

public class ShopContextMenuUtils {

    public static final int ShowEdit = 1 << 1;
    public static final int ShowDelete = 1 << 2;
    public static final int ShowLimiter = 1 << 3;
    public static final int ShowMove = 1 << 4;

    public static final int ShowBasic = ShowEdit | ShowDelete | ShowLimiter;
    public static final int ShowAll = ShowBasic | ShowMove;

    public static List<ContextMenuItem> getContextMenu(
            final ShopTab shopTab,
            final Consumer<ShopTab> onDelete,
            final Consumer<SDMEditConfigScreen> onEditOpen,
            final Runnable onEditClose
    ) {
        return getContextMenu(shopTab, onDelete, onEditOpen, onEditClose, ShowAll);
    }

    public static List<ContextMenuItem> getContextMenu(
            final ShopTab shopTab,
            final Consumer<ShopTab> onDelete,
            final Consumer<SDMEditConfigScreen> onEditOpen,
            final Runnable onEditClose,
            final int showBits
    ) {
        final List<ContextMenuItem> contextMenu = new ObjectArrayList<>();
        final BaseShop shop = shopTab.getOwnerShop();

        if(ShopUtils.isEditModeClient()) {
            if ((showBits & ShowEdit) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.EDIT_KEY), Icons.SETTINGS, (button) -> {
                    ConfigGroup group = new SDMConfigGroup("sdm", b -> {
                        if (b) ShopUtilsClient.syncTab(shop, shopTab);
                        onEditClose.run();
                    }).setNameKey("sidebar_button.sdm.shop");
                    ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
                    shopTab.getConfig(g);
                    final SDMEditConfigScreen edit = new SDMEditConfigScreen(group);
                    onEditOpen.accept(edit);
                    edit.openGui();
                }));
            }

            if ((showBits & ShowDelete) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.DELETE_KEY), Icons.REMOVE, (b) -> {
                    ShopUtilsClient.removeTab(shop, shopTab);
                    onDelete.accept(shopTab);
                }));
            }

            if ((showBits & ShowLimiter) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.RESET_LIMITER_KEY), Icons.BOOK_RED, (b) -> {
                    new SendResetLimiterC2S(shopTab.getId(), ShopObjectTypes.SHOP_TAB).sendToServer();
                }));
            }

            if ((showBits & ShowMove) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_UP_KEY), Icons.UP, (b) -> {
                    ShopUtilsClient.moveShopTab(shop, shopTab.getId(), MoveType.Up);
                }));
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_DOWN_KEY), Icons.DOWN, (b) -> {
                    ShopUtilsClient.moveShopTab(shop, shopTab.getId(), MoveType.Down);
                }));
            }
        }

        return contextMenu;
    }

    public static List<ContextMenuItem> getContextMenu(
            final ShopEntry shopEntry,
            final Consumer<ShopEntry> onDelete,
            final Consumer<SDMEditConfigScreen> onEditOpen,
            final Runnable onEditClose,
            final Consumer<ShopEntry> onCopy
    ) {
        return getContextMenu(shopEntry, onDelete, onEditOpen, onEditClose, onCopy, ShowAll);
    }

    public static List<ContextMenuItem> getContextMenu(
            final ShopEntry shopEntry,
            final Consumer<ShopEntry> onDelete,
            final Consumer<SDMEditConfigScreen> onEditOpen,
            final Runnable onEditClose,
            final Consumer<ShopEntry> onCopy,
            final int showBits
    ) {
        final List<ContextMenuItem> contextMenu = new ObjectArrayList<>();
        final BaseShop shop = shopEntry.getOwnerShop();

        if(ShopUtils.isEditModeClient()) {

            if((showBits & ShowEdit) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.EDIT_KEY), Icons.SETTINGS, (button) -> {
                    ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                        if (accept)
                            ShopUtilsClient.syncEntry(shop, shopEntry);
                        onEditClose.run();
                    }).setNameKey("sidebar_button.sdm.shop");

                    ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
                    shopEntry.getConfig(g);
                    final SDMEditConfigScreen edit = new SDMEditConfigScreen(group);
                    onEditOpen.accept(edit);
                    edit.openGui();
                }));
            }

            contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.DUPLICATE_KEY), Icons.ADD, (b) -> {
                final var entry = shopEntry.copy();
                ShopUtilsClient.addEntry(shop, entry);
                onCopy.accept(entry);
            }));

            TooltipList d1List = new TooltipList();
            d1List.add(Component.literal("Copy " + shopEntry.getId()));
            ContextMenuItem cont = new ContextMenuItem(Component.translatable(SDMShopConstants.COPY_ID_KEY), Icons.INFO, (b) -> {
                Minecraft.getInstance().keyboardHandler.setClipboard(shopEntry.getId().toString());
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Copy Shop Entry " + shopEntry.getId()));
            });
            cont.addMouseOverText(d1List);
            contextMenu.add(cont);

            if((showBits & ShowDelete) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.DELETE_KEY), Icons.REMOVE, (b) -> {
                    ShopUtilsClient.removeEntry(shop, shopEntry);
                    onDelete.accept(shopEntry);
                }));
            }

            if((showBits & ShowLimiter) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.RESET_LIMITER_KEY), Icons.BOOK_RED, (b) -> {
                    new SendResetLimiterC2S(shopEntry.getId(), ShopObjectTypes.SHOP_ENTRY).sendToServer();
                }));
            }

            if((showBits & ShowMove) != 0) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_UP_KEY), Icons.UP, (b) -> {
                    ShopUtilsClient.moveShopEntry(shop, shopEntry.getId(), MoveType.Up);
                }));
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.MOVE_DOWN_KEY), Icons.DOWN, (b) -> {
                    ShopUtilsClient.moveShopEntry(shop, shopEntry.getId(), MoveType.Down);
                }));
            }
        }

        return contextMenu;
    }
}
