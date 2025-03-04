package net.sixik.sdmshoprework.client.screen.legacy.buyer;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.client.screen.basic.buyer.AbstractBuyerBuyButton;
import net.sixik.sdmshoprework.client.screen.basic.buyer.AbstractBuyerCancelButton;
import net.sixik.sdmshoprework.client.screen.basic.buyer.AbstractBuyerScreen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class LegacyBuyerScreen extends AbstractBuyerScreen {

    public TextBox inputField;
    public TextField infoField;
    public TextField infoProductField;
    public TextField costBuyField;
    public TextField outputMoneyField;

    public int cantBuy = 0;

    public LegacyBuyerScreen(AbstractShopEntry entry){
//        for (String allCurrencyKey : CurrencyHelper.getAllCurrencyKeys()) {
//            System.out.println(allCurrencyKey);
//        }


        this.shopEntry = entry;
        int bsize = this.width / 2 - 10;


        int howMane = entry.getEntryType().howMany(Minecraft.getInstance().player, entry.isSell, entry);

        this.inputField = new TextBox(this){
            @Override
            public boolean isValid(String txt) {
                return parse((Consumer)null, txt, 0, howMane);
            }

            @Override
            public void onTextChanged() {
                if(isValid(getText()) && !getText().isEmpty()){
                    String f1 = getText();
                    if(f1.startsWith("0") && f1.length() > 1){
                        StringBuilder d1 = new StringBuilder();
                        for (int i = 1; i < f1.length(); i++) {
                            d1.append(f1.charAt(i));
                        }
                        LegacyBuyerScreen.this.updateCountInfo(Integer.parseInt(d1.toString()));
                        return;
                    }
                    LegacyBuyerScreen.this.updateCountInfo(Integer.parseInt(getText()));
                }
            }
        };
        this.inputField.setPosAndSize(8, 8, this.width - 16, 16);
        this.inputField.setText("0");
        this.inputField.setCursorPosition(this.inputField.getText().length());
        this.inputField.setFocused(true);

        this.infoField = new TextField(this);
        this.infoField.setPos(8, inputField.posY + inputField.height + 2);
        this.infoField.setSize(inputField.width, 9);
        this.infoField.setText(Component.translatable("sdm.shop.buyer.info.count.money", CurrencyHelper.Basic.getMoney(Minecraft.getInstance().player)));

        this.infoProductField = new TextField(this);
        this.infoProductField.setPos(8, infoField.posY + infoField.height + 2);
        this.infoProductField.setSize(inputField.width, 9);
        this.infoProductField.setText(
                entry.isSell ? Component.translatable("sdm.shop.buyer.info.entry.sell", howMane)
                        : Component.translatable("sdm.shop.buyer.info.entry.buy", howMane)
        );

        this.costBuyField = new TextField(this);
        this.costBuyField.setPos(8, infoProductField.posY + infoProductField.height + 10);
        this.costBuyField.setSize(inputField.width, 9);


        this.costBuyField.setText(
                entry.isSell ? Component.translatable("sdm.shop.buyer.info.cost.sell", 0)
                        : Component.translatable("sdm.shop.buyer.info.cost.buy", 0)
        );

        this.outputMoneyField = new TextField(this);
        this.outputMoneyField.setPos(8, costBuyField.posY + costBuyField.height + 2);
        this.outputMoneyField.setSize(inputField.width, 9);
        this.outputMoneyField.setText(
                Component.translatable("sdm.shop.buyer.info.money.left", CurrencyHelper.Basic.getMoney(Minecraft.getInstance().player))
        );

        this.cancelButton = new CancelButton(this);
        this.cancelButton.setPosAndSize(8, this.height - 24, bsize, 16);



        this.buyButton = new BuyButton(this);
        this.buyButton.setPosAndSize(this.width - bsize - 8, this.height - 24, bsize, 16);
    }

    public boolean parse(@Nullable Consumer<Integer> callback, String string, int min, int max) {
        try {
            int v = Long.decode(string).intValue();
            if (v >= (Integer)min && v <= (Integer)max) {
                if (callback != null) {
                    callback.accept(v);
                }

                return true;
            }
        } catch (Exception var4) {
        }

        return false;
    }

    public void updateCountInfo(int count){
        if(this.costBuyField == null) return;
        long d1 = count * shopEntry.entryPrice;
        this.count = count;
        this.costBuyField.setText(
                Component.translatable("sdm.shop.buyer.info.cost.buy", d1)
        );

        int money = shopEntry.isSell ? (int) (CurrencyHelper.Basic.getMoney(Minecraft.getInstance().player) + d1) : (int) (CurrencyHelper.Basic.getMoney(Minecraft.getInstance().player) - d1);

        this.outputMoneyField.setText(
                Component.translatable("sdm.shop.buyer.info.money.left", money)
        );

        refreshWidgets();
    }

    @Override
    public boolean onInit() {
        closeContextMenu();
        return true;
    }

    @Override
    public void addWidgets() {
        add(buyButton);
        add(cancelButton);

        add(infoField);
        add(infoProductField);
        add(costBuyField);

        add(outputMoneyField);

        add(inputField);
    }


    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }

    @Override
    public boolean onClosedByKey(Key key) {
        if (super.onClosedByKey(key)) {
            if(key.is(GLFW.GLFW_KEY_E) || key.is(GLFW.GLFW_KEY_BACKSPACE)) return false;

            if (key.esc()) {
                cancelButton.onClicked(MouseButton.LEFT);
            } else {

            }

            return true;
        } else {
            return false;
        }
    }


    protected static class CancelButton extends AbstractBuyerCancelButton {

        public CancelButton(Panel panel) {
            super(panel);
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.getTheme().draw(graphics, x, y, w, h);
        }
    }

    protected static class BuyButton extends AbstractBuyerBuyButton{

        public BuyButton(Panel panel) {
            super(panel);
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.getTheme().draw(graphics, x, y, w, h);
        }
    }
}
