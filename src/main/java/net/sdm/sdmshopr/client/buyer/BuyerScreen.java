package net.sdm.sdmshopr.client.buyer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.network.mainshop.BuyEntry;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

public class BuyerScreen extends BaseScreen {
    @Override public boolean isDefaultScrollVertical() {return false;}

    public ShopEntry<?> entry;
    public TextBox inputField;
    public TextField infoField;
    public TextField infoProductField;
    public TextField costBuyField;
    public TextField outputMoneyField;

    public BuyButton buyButton;
    public CancelButton cancelButton;
    public int cantBuy = 0;
    public int count = 0;
    public BuyerScreen(ShopEntry<?> entry){
        this.entry = entry;
        int bsize = this.width / 2 - 10;


        int howMane = entry.type.howMany(entry.isSell, entry);

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
                        BuyerScreen.this.updateCountInfo(Integer.parseInt(d1.toString()));
                        return;
                    }
                    BuyerScreen.this.updateCountInfo(Integer.parseInt(getText()));
                }
            }

            @Override
            public void writeText(String textToWrite) {
                super.writeText(textToWrite);
            }
        };
        this.inputField.setPosAndSize(8, 8, this.width - 16, 16);
        this.inputField.setText("0");
        this.inputField.setCursorPosition(this.inputField.getText().length());
        this.inputField.setFocused(true);

        this.infoField = new TextField(this);
        this.infoField.setPos(8, inputField.posY + inputField.height + 2);
        this.infoField.setSize(inputField.width, 9);
        this.infoField.setText(new TranslatableComponent("sdm.shop.buyer.info.count.money", SDMShopR.getClientMoney()));

        this.infoProductField = new TextField(this);
        this.infoProductField.setPos(8, infoField.posY + infoField.height + 2);
        this.infoProductField.setSize(inputField.width, 9);
        this.infoProductField.setText(
                entry.isSell ? new TranslatableComponent("sdm.shop.buyer.info.entry.sell", howMane)
                        : new TranslatableComponent("sdm.shop.buyer.info.entry.buy", howMane)
        );

        this.costBuyField = new TextField(this);
        this.costBuyField.setPos(8, infoProductField.posY + infoProductField.height + 10);
        this.costBuyField.setSize(inputField.width, 9);

        this.costBuyField.setText(
                entry.isSell ? new TranslatableComponent("sdm.shop.buyer.info.cost.sell", 0)
                        : new TranslatableComponent("sdm.shop.buyer.info.cost.buy", 0)
        );

        this.outputMoneyField = new TextField(this);
        this.outputMoneyField.setPos(8, costBuyField.posY + costBuyField.height + 2);
        this.outputMoneyField.setSize(inputField.width, 9);
        this.outputMoneyField.setText(
                new TranslatableComponent("sdm.shop.buyer.info.money.left", SDMShopR.getClientMoney())
        );

        this.cancelButton = new CancelButton(this);
        this.cancelButton.setPosAndSize(8, this.height - 24, bsize, 16);

        this.buyButton = new BuyButton(this);
        this.buyButton.setPosAndSize(this.width - bsize - 8, this.height - 24, bsize, 16);

    }

    @Override
    public ContextMenu openContextMenu(@NotNull List<ContextMenuItem> menu) {
        ContextMenu contextMenu = new ContextMenu(this, menu){
            @Override
            public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_3.draw(graphics, x + 1, y + 1, w - 2, h - 2);
                GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.BLACK, false);
            }
        };
        this.openContextMenu(contextMenu);
        return contextMenu;
    }

    public void updateCountInfo(int count){
        if(this.costBuyField == null) return;
        int d1 = count * entry.price;
        this.count = count;
        this.costBuyField.setText(
                new TranslatableComponent("sdm.shop.buyer.info.cost.buy", d1)
        );

        int money = entry.isSell ? (int) (SDMShopR.getClientMoney() + d1) : (int) (SDMShopR.getClientMoney() - d1);

        this.outputMoneyField.setText(
                new TranslatableComponent("sdm.shop.buyer.info.money.left", money)
        );

        refreshWidgets();
    }


    @Override
    public boolean onInit() {
//        closeContextMenu();
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
    public void alignWidgets() {
    }

    @Override
    public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(matrixStack, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(matrixStack, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
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

    protected class CancelButton extends SimpleTextButton{

        public CancelButton(Panel panel) {
            super(panel, new TranslatableComponent("sdm.shop.buyer.button.cancel"), Icon.EMPTY);
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
            if(mouseButton.isLeft()) {
                BuyerScreen screen = (BuyerScreen) getGui();
                screen.closeGui();

            }

        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }

        @Override
        public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
            SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
            GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
            GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
        }
    }

    protected class BuyButton extends SimpleTextButton{

        public BuyButton(Panel panel) {
            super(panel, new TranslatableComponent("sdm.shop.buyer.button.accept"), Icon.EMPTY);
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
            if(mouseButton.isLeft()){

                ShopEntry<?> d1 = BuyerScreen.this.entry;
                new BuyEntry(d1.tab.getIndex(), d1.getIndex(), BuyerScreen.this.count).sendToServer();
                BuyerScreen.this.closeGui();
                MainShopScreen.refreshIfOpen();
            }
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }

        @Override
        public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
            SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
            GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
            GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
        }
    }
}
