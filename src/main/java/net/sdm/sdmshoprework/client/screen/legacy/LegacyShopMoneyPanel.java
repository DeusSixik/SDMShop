package net.sdm.sdmshoprework.client.screen.legacy;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmeconomy.api.CurrencyHelper;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopMoneyPanel;
import net.sixik.sdmuilibrary.client.utils.TextHelper;

public class LegacyShopMoneyPanel extends AbstractShopMoneyPanel {


    public LegacyShopMoneyPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(this.moneyTitleField = new TextField(this));
        add(this.moneyCountField = new TextField(this));
    }

    @Override
    public void alignWidgets() {
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
        SDMShopClient.getTheme().drawHollow(graphics, x, y, w, h);
    }
}
