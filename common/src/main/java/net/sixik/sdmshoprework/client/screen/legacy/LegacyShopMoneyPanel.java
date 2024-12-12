package net.sixik.sdmshoprework.client.screen.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopMoneyPanel;
import net.sixik.v2.render.TextRenderHelper;

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

        int w = TextRenderHelper.getTextWidth(textTitle.getString());

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
        w = TextRenderHelper.getTextWidth(textMoney);
        w1 = this.width - w;
        w2 = w1 / 2;

        this.moneyCountField.setX(w2);
        this.moneyCountField.setText(textMoney);
        this.moneyCountField.setColor(SDMShopClient.getTheme().getMoneyTextColor());
        this.moneyCountField.setY(this.height - Theme.DEFAULT.getFontHeight() - 2);
    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().drawHollow(graphics, x, y, w, h);
    }
}
