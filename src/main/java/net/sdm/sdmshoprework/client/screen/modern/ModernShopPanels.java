package net.sdm.sdmshoprework.client.screen.modern;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.sdm.sdmeconomy.api.CurrencyHelper;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopPanel;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopMoneyPanel;
import net.sdm.sdmshoprework.client.screen.basic.widget.AbstractShopEntrySearch;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.TextHelper;
import net.sixik.sdmuilibrary.client.utils.math.Vector2f;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;

public class ModernShopPanels {

    public static class TopEntriesPanel extends AbstractShopPanel {

        public AbstractShopEntrySearch textBox;
        public TopEntriesPanel(Panel panel) {
            super(panel);
        }

        @Override
        public void addWidgets() {
            add(this.textBox = new AbstractShopEntrySearch(this) {

                @Override
                public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, x, y, w, h, 8);
                }
            });
            textBox.setText(getShopScreen().searchField);
        }

        @Override
        public void alignWidgets() {
            setProperty();
        }

        public void setProperty(){

            int h = this.height / 4;

            this.textBox.ghostText = Component.translatable("sdm.shop.modern.ui.search.ghost_text").getString();
            this.textBox.setPos(h / 2 + 6, h / 2);
            this.textBox.setSize(this.width / 2 - h, this.height - h);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RenderHelper.drawRoundedRectUp(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
        }
    }

    public static class TopPanel extends AbstractShopPanel {

        public TextField categoryField;

        public TopPanel(Panel panel) {
            super(panel);
        }

        @Override
        public void addWidgets() {
            add(this.categoryField = new TextField(this));
            setProperty();
        }

        @Override
        public void alignWidgets() {
            setProperty();
        }

        public void setProperty(){


            Vector2f size = TextHelper.getTextRenderSize(Component.translatable("sdm.shop.modern.ui.tab_categories").getString(), (int) this.width, 1.2f, 50);
            int d2 = Minecraft.getInstance().font.lineHeight;

            int w1 = (int) (this.width - size.x);
            int w2 = w1 / 2;
            int h1 = this.height / 2;
            h1 = h1 - d2 / 2;

            this.categoryField.setPos(w2,h1);
            this.categoryField.setSize(this.width, this.height);
            this.categoryField.setMaxWidth(this.width);

            this.categoryField.setText(Component.translatable("sdm.shop.modern.ui.tab_categories"));
            this.categoryField.setScale(size.y);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RenderHelper.drawRoundedRectUp(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
        }
    }

    public static class BottomPanel extends AbstractShopMoneyPanel {

        public BottomPanel(Panel panel) {
            super(panel);
        }

        @Override
        public void addWidgets() {
            add(this.moneyTitleField = new TextField(this));
            add(this.moneyCountField = new TextField(this));
            setProperty();
        }

        @Override
        public void alignWidgets() {
            setProperty();
        }

        public void setProperty(){
            Component textTitle = Component.translatable("sdm.shop.ui.money");

            int w = TextHelper.getTextWidth(textTitle.getString());

            int w1 = this.width - w;
            int w2 = w1 / 2;


            this.moneyTitleField.addFlags(32);
            this.moneyTitleField.setSize(this.width - 1, this.height);
            this.moneyTitleField.setMaxWidth(this.width - 2);
            this.moneyTitleField.setText(textTitle);
            this.moneyTitleField.setX(w2);
            this.moneyTitleField.setY(2);
            this.moneyTitleField.setScale(1.2f);


            String textMoney = SDMShopRework.moneyString(CurrencyHelper.Basic.getMoney(Minecraft.getInstance().player));
            w = TextHelper.getTextWidth(textMoney);
            w1 = this.width - w;
            w2 = w1 / 2;

            this.moneyCountField.setX(w2);
            this.moneyCountField.setText(textMoney);
            this.moneyCountField.setColor(SDMShopClient.getTheme().getMoneyTextColor());
            this.moneyCountField.setY(this.height - Theme.DEFAULT.getFontHeight() - 2);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RenderHelper.drawRoundedRectDown(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
        }
    }
}
