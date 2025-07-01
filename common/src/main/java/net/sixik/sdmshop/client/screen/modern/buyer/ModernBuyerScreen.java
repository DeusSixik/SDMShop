package net.sixik.sdmshop.client.screen.modern.buyer;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.render.BuyerRenderVariable;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerBuyButton;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerCancelButton;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerScreen;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernBuyerScreen extends AbstractBuyerScreen {

    protected int offerSize = -1;
    protected int limitValue;
    protected ShopLimiterData limitData;
    protected TextBox textBox;
    protected int sizeIcon;
    protected Component entryType;

    public ModernBuyerScreen(AbstractShopScreen shopScreen, AbstractShopEntryButton shopEntry) {
        super(shopScreen, shopEntry);


        entryType = Component.empty();
        updateLimitData();
    }

    @Override
    public void addWidgets() {}

    @Override
    public void alignWidgets() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getWidgets().clear();

        add(this.textBox = new TextBox(this) {
            @Override
            public boolean isValid(String txt) {
                return parse(null, txt, 1, offerSize);
            }

            @Override
            public void onTextChanged() {
                if(!getText().isEmpty())
                    count = Integer.parseInt(getText());
            }

            @Override
            public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics,x,y,w,h, 4);
            }
        });
        add(this.cancelButton = new CancelButton(this));
        add(this.buyButton = new BuyButton(this));


        int bsize = this.width / 2 - 10;
        this.sizeIcon = width >= 16 ? 16 : 8;


        this.cancelButton.setPosAndSize(8, this.height - 24, bsize, 16);
        this.buyButton.setPosAndSize(this.width - bsize - 8, this.height - 24, bsize, 16);
        this.textBox.setText(count > 0 ? String.valueOf(count) : "");
        this.textBox.ghostText = shopEntry.getType().isSell() ?
                Component.translatable("sdm.shop.modern.ui.buyer.entry.input.ghost.sell").getString() :
                Component.translatable("sdm.shop.modern.ui.buyer.entry.input.ghost.buy").getString();
        this.textBox.setPos(5, 5 + sizeIcon * 2 + 2 + (lineHeight + 1 + 2) * 2);
        this.textBox.setSize(this.width - 10, lineHeight + 1);

        updateButtons();
    }

    protected void updateButtons(){

        updateLimitData();

        entryType = shopEntry.getType().isSell() ? Component.translatable("sdm.shop.modern.ui.buyer.entry.sell")
                : Component.translatable("sdm.shop.modern.ui.buyer.entry.buy");
    }

    protected void updateLimitData() {
        this.limitData = getShopLimit();
        this.limitValue = limitData.value();
        this.offerSize = getMaxEntryOfferSize(limitValue >= 0 ? limitValue : -1);
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, x, y, w, h, 10);

        Vector2 pos = new Vector2(x + 5, y + 5);

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, sizeIcon * 2, sizeIcon * 2, 8);

        AbstractShopEntryButton.getIconFromEntry(shopEntry).draw(graphics,pos.x + sizeIcon / 2,pos.y + sizeIcon / 2,sizeIcon,sizeIcon);

        pos.setX(pos.x + sizeIcon * 2 + 2);

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, this.width - 10 - 2 - sizeIcon * 2, lineHeight + 1, 4);


        BuyerRenderVariable variable = new BuyerRenderVariable(pos, sizeIcon);

        shopEntry.getEntryType().drawTitle(shopEntry, graphics, theme, x,y,w,h,variable, this);

        int d = shopEntry.getEntrySellerType().getRenderWight(graphics, theme, x,y,w,h, shopEntry.getCount(),this, 0);;
        int w1 = (this.width - 10 - 2 - sizeIcon * 2) - d;
        int w2 = w1 / 2;

        pos.setY(pos.y + sizeIcon);
        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, this.width - 10 - 2 - sizeIcon * 2, lineHeight + 1, 4);


        shopEntry.getEntrySellerType().draw(graphics, theme, pos.x + w2, pos.y + 1, w, 16, shopEntry.getPrice(), this, 0);

        pos.setPosition(x + 5, y + 5 + sizeIcon * 2 + 2);
        Vector2 size = new Vector2(this.width - 10, this.height - (5 + sizeIcon * 2 + 2 + 24 + 2));

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1, 4);

        GLHelper.pushScissor(graphics, pos.x,pos.y, size.x / 2 - 2, lineHeight + 1);
        theme.drawString(graphics, Component.translatable("sdm.shop.modern.ui.player_money"), pos.x + 2, pos.y + 1, Color4I.WHITE, 2);
        GLHelper.popScissor(graphics);

        RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, pos.x + size.x / 2,pos.y, size.x / 2, lineHeight + 1, 4);

        String textMoney;

        GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, lineHeight + 1);
        shopEntry.getEntrySellerType().draw(graphics, theme, pos.x + size.x / 2 + 2, pos.y + 1, w, 16, shopEntry.getEntrySellerType().getMoney(Minecraft.getInstance().player, shopEntry),this, -2);

        GLHelper.popScissor(graphics);

        pos.setPosition(pos.x, pos.y + lineHeight + 1 + 2);
        ShopRenderUtils.drawLabel(graphics, theme, pos, size, entryType.getString(), String.valueOf(offerSize));

        if(limitValue >= 0){
            pos.setPosition(pos.x, pos.y + (lineHeight + 1 + 2) * 2);
            ShopRenderUtils.drawLabel(graphics, theme, pos, size, Component.translatable("sdm.shop.modern.ui.buyer.entry.limit").getString(), String.valueOf(limitValue));

            pos.setPosition(pos.x, pos.y + lineHeight + 1 + 2);
            textMoney = shopEntry.getType().isSell() ? Component.translatable("sdm.shop.modern.ui.buyer.entry.output.sell").getString() : Component.translatable("sdm.shop.modern.ui.buyer.entry.output.buy").getString();
        }
        else {
            pos.setPosition(pos.x, pos.y + (lineHeight + 1 + 2) * 2);
            textMoney = shopEntry.getType().isSell() ? Component.translatable("sdm.shop.modern.ui.buyer.entry.output.sell").getString() : Component.translatable("sdm.shop.modern.ui.buyer.entry.output.buy").getString();
        }

        ShopRenderUtils.drawLabel(graphics, theme, pos, size,
        (graphics1, theme1, x1, y1, w3, h1) -> {
            theme.drawString(graphics1, textMoney, x1 + 2, y1 + 1, Color4I.WHITE, 2);
        },
        (graphics1, theme1, x1, y1, w3, h1) -> {
            shopEntry.getEntrySellerType().draw(graphics1, theme1, x1, y1, w3, 16, shopEntry.getPrice() * count, this, 0);
        }
        );

    }


    protected static class CancelButton extends AbstractBuyerCancelButton {
        public CancelButton(ModernBuyerScreen modernBuyerScreen) {
            super(modernBuyerScreen);
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }


        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0,0,0,255 / 2).drawRoundFill(graphics, x,y,w,h, 6);
        }
    }

    protected static class BuyButton extends AbstractBuyerBuyButton {
        public BuyButton(ModernBuyerScreen modernBuyerScreen) {
            super(modernBuyerScreen);
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0,0,0,255 / 2).drawRoundFill(graphics, x,y,w,h, 6);
        }
    }
}
