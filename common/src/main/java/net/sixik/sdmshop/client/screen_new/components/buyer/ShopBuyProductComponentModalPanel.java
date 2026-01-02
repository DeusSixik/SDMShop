package net.sixik.sdmshop.client.screen_new.components.buyer;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.api.ShopApi;
import net.sixik.sdmshop.client.screen_new.MainShopEntryButton;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.old_api.ShopEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmshop.utils.rendering.widgets.IconTooltipWidget;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopBuyProductComponentModalPanel extends ModalPanel {

    public static ShopBuyProductComponentModalPanel openCentered(final Panel panel, final ShopEntry shopEntry) {
        final var modal = openDefault(panel, shopEntry);
        modal.center = true;

        final int sw = panel.getWidth();
        final int sh = panel.getHeight();

        final int w = modal.getWidth();
        final int h = modal.getHeight();

        modal.setPos((sw - w) / 2, (sh - h) / 2);
        return modal;
    }

    public static ShopBuyProductComponentModalPanel openDefault(final Panel panel, final ShopEntry shopEntry) {
        final BaseScreen gui = panel.getGui();

        final ShopBuyProductComponentModalPanel modal =
                new ShopBuyProductComponentModalPanel(panel, shopEntry);

        modal.setSize(gui.width / 2, (int) (gui.height / 1.5));

        gui.pushModalPanel(modal);

        if (MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalOpen(modal);

        return modal;
    }

    protected static final int fontH = Theme.DEFAULT.getFontHeight();
    protected static final int fontHD2 = fontH / 2;

    protected final int remaining;
    protected final String remainingTxt;
    protected final int remainingTxtL;
    protected final int totalLimit;
    protected final ShopEntryType shopEntryType;

    protected final ShopEntry shopEntry;
    protected final String moneyText;
    protected final int textL;
    protected final Icon icon;
    protected boolean center;

    protected int leftPanelW;
    protected int rightPanelW;
    protected int space = 4;

    protected Component title;
    protected int titleL;

    protected int iconSize;
    protected int iconSize3;

    protected Component priceBy;
    protected int priceByL;

    protected final int maxOfferSize;
    protected final String maxOfferSizeTxt;
    protected final int maxOfferSizeTxtL;

    protected IconTooltipWidget iconTooltipWidget;
    protected TextBox inputCountBox;
    protected Button minusButton;
    protected Button plusButton;
    protected Button maxButton;
    protected Button cancelButton;
    protected Button acceptButton;

    protected int currentPlayerOffer = 0;

    protected boolean leftPanelWidgetsSet = false;
    protected boolean rightPanelWidgetsSet = false;

    protected ShopBuyProductComponentModalPanel(
            final Panel panel,
            final ShopEntry shopEntry
    ) {
        super(panel);
        this.shopEntry = shopEntry;

        this.shopEntryType = shopEntry.getType();
        this.title = shopEntry.getTitle();
        this.titleL = Theme.DEFAULT.getStringWidth(title);
        this.moneyText = this.shopEntry.getEntrySellerType().moneyToString(this.shopEntry);
        this.textL = Theme.DEFAULT.getStringWidth(moneyText);
        this.icon = ShopRenderUtils.getIconFromEntry(this.shopEntry);

        this.priceBy = Component.translatable("sdm.shop.gui.buyer.text.price_per", shopEntry.getCount());
        this.priceByL = Theme.DEFAULT.getStringWidth(this.priceBy);

        this.remaining = shopEntry.getObjectLimitLeft(Minecraft.getInstance().player);
        this.totalLimit = shopEntry.getObjectLimit();
        this.remainingTxt = String.valueOf(remaining);
        this.remainingTxtL = Theme.DEFAULT.getStringWidth(remainingTxt);
        this.maxOfferSize = getMaxEntryOfferSize(remaining);
        this.maxOfferSizeTxt = String.valueOf(maxOfferSize);
        this.maxOfferSizeTxtL = Theme.DEFAULT.getStringWidth(maxOfferSizeTxt);
    }

    @Override
    public void addWidgets() {
        leftPanelWidgetsSet = false;
        rightPanelWidgetsSet = false;
        add(iconTooltipWidget = new IconTooltipWidget(this, icon, s -> shopEntry.getEntryType().addEntryTooltip(s ,shopEntry)));

        add(inputCountBox = new TextBox(this) {
            @Override
            public boolean isValid(String txt) {
                return ShopUtils.isDigitsInRange(txt, 0, maxOfferSize);
            }

            @Override
            public void onTextChanged() {
                final String txt = getText();
                if (txt.isEmpty()) return;
                currentPlayerOffer = Integer.parseInt(txt);
            }
        });
        inputCountBox.setFilter(ShopUtils.ONLY_DIGITS);
        inputCountBox.setText(String.valueOf(currentPlayerOffer));

        add(minusButton = new SimpleTextButton(this, Component.literal("-"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                setUserCount(Math.max(0, currentPlayerOffer - 1));
            }
        });
        add(plusButton = new SimpleTextButton(this, Component.literal("+"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                setUserCount(Math.min(maxOfferSize, currentPlayerOffer + 1));
            }
        });
        add(maxButton = new SimpleTextButton(this, Component.literal("MAX"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                setUserCount(maxOfferSize);
            }
        });

        add(cancelButton = new SimpleTextButton(this, Component.literal("Cancel"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                getGui().popModalPanel();
            }
        });

        add(acceptButton = new SimpleTextButton(this, Component.literal("Accept"), Icon.empty()) {

            @Override
            public boolean shouldDraw() {
                return currentPlayerOffer > 0;
            }

            @Override
            public void onClicked(MouseButton button) {
                ShopApi.sendBuyEntry(shopEntry, currentPlayerOffer);
                getGui().popModalPanel();
            }
        });
    }

    public void setUserCount(final int value) {
        inputCountBox.setText(String.valueOf(value));
    }

    @Override
    public void setWidth(int v) {
        super.setWidth(v);

        rightPanelW = this.width / 3 - space;
        leftPanelW = this.width / 2 + rightPanelW / 2;

        final int shift = leftPanelW / 7;
        leftPanelW -= shift;
        rightPanelW += shift;

        this.iconSize = v / 8;
        this.iconSize3 = iconSize / 3;
    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void onClosed() {

        if (MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalClose(this);

        super.onClosed();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        drawDefaultPanel(graphics, x, y, leftPanelW, h);
        drawDefaultPanel(graphics, x + w - rightPanelW - space, y, rightPanelW, h);
    }

    protected void drawDefaultPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);
        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_FILL_INT, BORDER_INT);
        ShopRenderingWrapper.endBatch();
    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        drawLeftPanel(graphics, theme, x, y, leftPanelW, h);
        drawRightPanel(graphics, theme, x + leftPanelW + space, y, rightPanelW, h);
    }

    public void drawRightPanel(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        int posY = y + 8;

        final Component ymTxt = Component.translatable("sdm.shop.gui.buyer.text.player_money");
        final int ymTxtL = theme.getStringWidth(ymTxt);
        theme.drawString(graphics, ymTxt, x + (w - ymTxtL) / 2, posY, Color4I.rgb(0xA8BFDC), 0);

        posY += fontH + 2;
        final double playerMoney = shopEntry.getEntrySellerType().getMoney(Minecraft.getInstance().player, shopEntry);
        shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, posY, w, h, playerMoney);

        posY += fontH + fontHD2;
        final Component ysTxt = Component.translatable(shopEntryType.isBuy() ? "sdm.shop.gui.buyer.text.player_spend" : "sdm.shop.gui.buyer.text.player_receive");
        final int ysTxtL = theme.getStringWidth(ysTxt);
        theme.drawString(graphics, ysTxt, x + (w - ysTxtL) / 2, posY, Color4I.rgb(0xA8BFDC), 0);
        posY += fontH + 2;

        shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, posY, w, h, shopEntry.getPrice() * currentPlayerOffer);

        posY += fontH + fontHD2;
        final Component mlTxt = Component.translatable("sdm.shop.gui.buyer.text.player_money_left");
        final int mlTxtL = theme.getStringWidth(mlTxt);
        theme.drawString(graphics, mlTxt, x + (w - mlTxtL) / 2, posY, Color4I.rgb(0xA8BFDC), 0);
        posY += fontH + 2;
        shopEntry.getEntrySellerType().drawCentered(
                graphics,
                theme,
                x,
                posY,
                w,
                h,
                shopEntryType.isSell() ? playerMoney + (currentPlayerOffer * shopEntry.getPrice()) : playerMoney - (currentPlayerOffer * shopEntry.getPrice())
        );

        if (!rightPanelWidgetsSet) {
            final int pad = 4;
            final int gap = 4;

            final int buttonH = fontH + fontHD2;

            final int buttonW = Math.max(20, (rightPanelW - pad * 2 - gap) / 2);

            cancelButton.setSize(buttonW, buttonH);
            acceptButton.setSize(buttonW, buttonH);

            final int rightPanelStartX = this.width - rightPanelW;
            final int buttonsY = h - pad - buttonH;

            cancelButton.setPos(rightPanelStartX + pad, buttonsY);
            acceptButton.setPos(rightPanelStartX + pad + buttonW, buttonsY);

            rightPanelWidgetsSet = true;
        }
    }


    public void drawLeftPanel(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        int posY = y + iconSize3;

        if(!leftPanelWidgetsSet) {
            iconTooltipWidget.setSize(iconSize, iconSize);
            iconTooltipWidget.setPos((w - iconSize) / 2, iconSize3);
        }
//        if (!icon.isEmpty())
//            icon.draw(graphics, x + (w - iconSize) / 2, posY, iconSize, iconSize);

        posY += iconSize + fontHD2;
        shopEntry.getEntryType().drawTitleCentered(shopEntry, graphics, theme, x, posY, w, h);

        posY += fontH * 2;
        theme.drawString(graphics, priceBy, x + 2, posY, Color4I.rgb(0xA8BFDC), 0);

        if (shopEntry.getPrice() > 0) {
            int size = shopEntry.getEntrySellerType().getRenderSize(graphics, theme, x, posY, w, h, shopEntry.getPrice());
            shopEntry.getEntrySellerType().draw(graphics, theme, x + (w - size) - 4, posY, w, h, shopEntry.getPrice());
        } else {
            theme.drawString(graphics, MainShopEntryButton.FREE_COMPONENT, x + (w - MainShopEntryButton.FREE_COMPONENT_L) - 2, posY);
        }

        if (remaining != Integer.MAX_VALUE && totalLimit > 0) {
            posY += fontH + fontHD2;
            theme.drawString(graphics, Component.translatable("sdm.shop.gui.buyer.text.available_items"), x + 2, posY, Color4I.rgb(0xA8BFDC), 0);
            theme.drawString(graphics, remainingTxt, x + (w - remainingTxtL) - 2, posY);
        }

        posY += fontH + fontHD2;
        theme.drawString(graphics, Component.translatable("sdm.shop.gui.buyer.text.max_buy"), x + 2, posY, Color4I.rgb(0xA8BFDC), 0);
        theme.drawString(graphics, maxOfferSizeTxt, x + (w - maxOfferSizeTxtL) - 2, posY);

        posY += fontH + fontHD2;
        if (!leftPanelWidgetsSet) {
            final int pad = 4;
            final int gap = 4;

            final int bY = posY - y;
            final int bH = fontH + fontHD2;

            // Y/Height одинаковые
            minusButton.setY(bY);
            minusButton.setHeight(bH);

            plusButton.setY(bY);
            plusButton.setHeight(bH);

            maxButton.setY(bY);
            maxButton.setHeight(bH);

            inputCountBox.setY(bY);
            inputCountBox.setHeight(bH);

            // ВАЖНО: ширины кнопок НЕ меняем
            final int mw = minusButton.getWidth();
            final int pw = plusButton.getWidth();
            final int xw = maxButton.getWidth();

            // X: минус слева, макс справа, плюс перед максом, инпут между минусом и плюсом
            final int minusX = pad;

            final int maxX = w - pad - xw;
            final int plusX = maxX - gap - pw;

            final int inputX = minusX + mw + gap;
            final int inputW = Math.max(0, plusX - gap - inputX);

            minusButton.setX(minusX);
            inputCountBox.setX(inputX);
            inputCountBox.setWidth(inputW);

            plusButton.setX(plusX);
            maxButton.setX(maxX);

            leftPanelWidgetsSet = true;
        }
    }

    public int getMaxEntryOfferSize(int size) {
        return ShopUtils.getMaxEntryOfferSize(shopEntry, Minecraft.getInstance().player, size != Integer.MAX_VALUE ? size : -1);
    }
}
