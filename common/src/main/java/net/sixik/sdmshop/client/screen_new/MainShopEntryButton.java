package net.sixik.sdmshop.client.screen_new;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.ArrayList;
import java.util.List;

public class MainShopEntryButton extends SimpleTextButton {

    protected static final int fontH = Theme.DEFAULT.getFontHeight();

    protected final MainShopEntryPanel entryPanel;
    protected final ShopEntry shopEntry;

    public String moneyText;
    public int textL;
    public Icon icon;
    public Component component;
    private int componentL;

    private String countString;
    private String limitString;
    private int limitStingL;
    private boolean hasLimit;
    private int limitColor;
    private boolean hasCount;
    private int countL;

    public MainShopEntryButton(MainShopEntryPanel panel, ShopEntry shopEntry) {
        super(panel, shopEntry.getTitle(), Icon.empty());
        this.entryPanel = panel;
        this.shopEntry = shopEntry;
        onInit();
    }

    public void onInit() {
        moneyText = shopEntry.getEntrySellerType().moneyToString(shopEntry);
        textL = Theme.DEFAULT.getStringWidth(moneyText);
        icon = ShopRenderUtils.getIconFromEntry(shopEntry);

        component = shopEntry.getType().isSell() ?
                Component.translatable("sdm.shop.entry.sell") :
                Component.translatable("sdm.shop.entry.buy");
        hasCount = shopEntry.getCount() > 1;

        this.countString = String.valueOf(shopEntry.getCount());
        this.countL = Theme.DEFAULT.getStringWidth(this.countString);

        int remaining = shopEntry.getObjectLimitLeft(Minecraft.getInstance().player);
        int totalLimit = shopEntry.getObjectLimit();

        /*
            Checking: if it returns MAX_VALUE, then the limit is inactive
         */
        if (remaining != Integer.MAX_VALUE && totalLimit > 0) {
            this.hasLimit = true;

            /*
                We form a string: "Remaining / Total"
             */
            this.limitString = remaining + "/" + totalLimit;
            this.limitStingL = Theme.DEFAULT.getStringWidth(limitString);
            
            if (remaining <= 0) {
                this.limitColor = 0xFFFF5555; // Red (It's over)
                this.component = Component.translatable("sdm.shop.entry.sold");
            } else if (remaining == 1) {
                this.limitColor = 0xFFFFAA00; // Golden (The Last one)
            } else {
                this.limitColor = 0xFFAAAAAA; // Grey (Normal)
            }
        } else {
            this.hasLimit = false;
        }

        componentL = Theme.DEFAULT.getStringWidth(component);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        if(shopEntry == null) return;

        shopEntry.addTooltipToList(list);
        shopEntry.getEntryType().addEntryTooltip(list, shopEntry);
        shopEntry.getEntrySellerType().addEntryTooltip(list, shopEntry);
    }

    public boolean isEdit() {
        return false;
    }

    @Override
    public void onClicked(MouseButton mouseButton) {

        MainShopScreen screen = (MainShopScreen) entryPanel.screen;
        BaseShop shop = screen.getShop();
        boolean isClientEdit = ShopUtils.isEditModeClient();

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

//            if(!isEdit()) {
//                if (isFavorite()) {
//                    contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.UN_FAVORITE_KEY), ShopUtilsClient.FAVORITE_ICON.withColor(Color4I.GRAY), (b) -> {
//                        ShopUtilsClient.removeFavorite((shopEntry));
//                        favorite = false;
//                        screen.onRefresh();
//                    }));
//                } else {
//                    contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.FAVORITE_KEY), ShopUtilsClient.FAVORITE_ICON, (b) -> {
//                        ShopUtilsClient.addFavorite(shopEntry);
//                        favorite = true;
//                        screen.onRefresh();
//                    }));
//                }
//            }


            if(!contextMenu.isEmpty()) {
                screen.openContextMenu(contextMenu);
                return;
            }
        }
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(!MainShopScreen.Instance.shouldRenderWidgets) return;
        drawAfterBatch(graphics, theme, x, y, w, h);
    }

    @Override
    public boolean checkMouseOver(int mouseX, int mouseY) {
        if(!MainShopScreen.Instance.shouldRenderWidgets)
            return false;

        return super.checkMouseOver(mouseX, mouseY);
    }

    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.FOREGROUND;
    }

    public void drawBatch(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(isMouseOver) ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_3_INT);
        else ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
    }

    public void drawAfterBatch(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        final int iconSize = w / 3;
        final int iconSize3 = iconSize / 3;

        /*
            Icon rendering
         */
        if(!icon.isEmpty())
            icon.draw(graphics, x + (this.width - iconSize) / 2, y + iconSize3, iconSize, iconSize);

        /*
            Drawing the quantity of a product (stack)
         */
        if(hasCount) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);

            theme.drawString(graphics, countString, x + (w - countL) / 2 + iconSize / 2, y + iconSize);

            graphics.pose().popPose();
        }

        /*
            Drawing the limit
         */
        if (hasLimit) {
            drawLimitBadge(graphics, theme, x, y, w);
        }

        final int endElementY = y + (h - (fontH + 2));

        /*
            Rendering "Buy/Sell" text
         */
        theme.drawString(graphics, component, x + (w - componentL) / 2, endElementY - (fontH + 2));

        /*
            Rendering the Price
         */
        shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, endElementY ,w, h, shopEntry.getPrice());
    }

    private void drawLimitBadge(GuiGraphics graphics, Theme theme, int x, int y, int w) {
        final PoseStack pose = graphics.pose();

        pose.pushPose();

        /*
            We scale the text because the space is limited (0.75x)
         */
        final float scale = 0.75f;
        pose.scale(scale, scale, 1f);

        /*
            Coordinates with scaling
            We want the top-right corner: (x + w - padding) / scale
         */
        final int destX = (int) ((x + w - 4) / scale - limitStingL);
        final int destY = (int) ((y + 4) / scale);
        theme.drawString(graphics, limitString, destX, destY, limitColor);

        pose.popPose();
    }
}
