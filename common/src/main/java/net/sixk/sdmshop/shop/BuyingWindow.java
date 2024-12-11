package net.sixk.sdmshop.shop;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdmuilibrary.client.utils.TextHelper;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.client.BuyShopTovarC2S;
import net.sixk.sdmshop.shop.network.client.SellShopTovarC2S;

import java.util.Iterator;



public class BuyingWindow extends BaseScreen {

    Tovar tovar;
    AbstractCurrency currency;
    TextField title;
    TextField cost;
    TextField moneyTxt;
    TextField moneyNum;
    TextField mayBuyTxt;
    TextField mayBuyNum;
    TextField limitTxt;
    TextField limit;
    TextField receiptTxt;
    TextField receipt;
    TextBox countTxt;
    SimpleTextButton confirm;
    SimpleTextButton cancel;
    long count;
    public int countItems = 0;

    public BuyingWindow(Tovar tovar){

        this.tovar = tovar;
        for (AbstractCurrency w1 : PlayerMoneyData.CLIENT.CLIENT_MONET.currencies) {
            if(tovar.currency.equals(w1.getID())) currency = w1;
        }


    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_0.draw(graphics, x, y, w, h);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, false);

    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 3, 26, 26);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 3, 27, 27, NordColors.POLAR_NIGHT_4, false);
        GuiHelper.drawHollowRect(graphics, x + 4, y + 4, 25, 25, NordColors.POLAR_NIGHT_3, false);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 32, y + 3, w - 34, 12);
        GuiHelper.drawHollowRect(graphics, x + 32, y + 3, w - 34, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 32, y + 17, w - 34, 12);
        GuiHelper.drawHollowRect(graphics, x + 32, y + 17, w - 34, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 33, w / 2, 12);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 33, w  / 2, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 47, w / 2, 12);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 47, w / 2, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 5 + w / 2, y + 33, w / 2 - 7, 12);
        GuiHelper.drawHollowRect(graphics, x + 5 +  w / 2, y + 33, w  / 2 - 7, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 5 + w / 2, y + 47, w / 2 - 7, 12);
        GuiHelper.drawHollowRect(graphics, x + 5 + w / 2, y + 47, w  / 2 - 7, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 61, w - 5 , 12);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 61, w  - 5, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 75, w - 5 , 12);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 75, w  - 5, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 89, w - 5 , 12);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 89, w  - 5, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 103, w / 2, 12);
        GuiHelper.drawHollowRect(graphics, x + 3, y + 103, w / 2, 12, NordColors.POLAR_NIGHT_4, true);

        NordColors.POLAR_NIGHT_1.draw(graphics, x + 5 + w / 2, y + 103, w / 2 - 7, 12);
        GuiHelper.drawHollowRect(graphics, x + 5 +  w / 2, y + 103, w  / 2 - 7, 12, NordColors.POLAR_NIGHT_4, true);

        ItemIcon.getItemIcon(tovar.item).draw(graphics, x + 6, y + 7, 20, 20);
    }




    @Override
    public void addWidgets() {

        add(title = new TextField(this).setText(tovar.item.getDisplayName().getString().replace("[","").replace("]", "")));
        add(cost = new TextField(this));
        add(moneyTxt = new TextField(this));
        add(moneyNum = new TextField(this));
        add(mayBuyTxt = new TextField(this));
        add(mayBuyNum = new TextField(this));
        add(limitTxt = new TextField(this).setText(Component.translatable("sdm_shop.buying_window.limit")));
        add(limit = new TextField(this).setText(String.valueOf(tovar.limit)));

        add(countTxt = new TextBox(this){

            @Override
            public void onTextChanged() {
                if(!getText().isEmpty()) {
                    if (getText().length() > 9) return;
                    for (int w = 0; w < getText().length(); w++) {
                        if (!Character.isDigit(getText().charAt(w))) {
                            return;
                        }
                    }

                    if(Integer.parseInt(getText())  > tovar.limit && tovar.limit != -1){
                        setText(String.valueOf(tovar.limit));
                    }

                    count = Long.parseLong(getText());
                    if (tovar.toSell){
                        if (Integer.valueOf(countTxt.getText()) > countItems / tovar.item.getCount()) countTxt.setText(String.valueOf(countItems / tovar.item.getCount()));
                    } else {
                        if(Integer.valueOf(countTxt.getText()) > (int) (currency.moneys / tovar.cost)) countTxt.setText(String.valueOf((int) (currency.moneys / tovar.cost)));
                    }
                    if((int) (currency.moneys / tovar.cost) != 0  && !tovar.toSell)spawnButton();
                    if(tovar.toSell)spawnButton();
                } else {
                    receipt.setText("");
                    count = 0;
                    Iterator<Widget> w = widgets.iterator();
                    while (w.hasNext()){
                        Widget w1 = w.next();
                        if(w1.equals(confirm)) {
                            w.remove();
                            break;
                        }
                    }

                }
                receipt.setText(count * tovar.cost > 0 && !getText().isEmpty()?String.valueOf(count * tovar.cost) : "");
            }

            @Override
            public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

            }

        });
        countTxt.ghostText = Component.translatable("sdm_shop.buying_window.ghost_text").getString();
        add(receiptTxt = new TextField(this));
        add(receipt = new TextField(this));
        add(cancel = new SimpleTextButton(this, Component.translatable("sdm_shop.cancel"), Icon.empty()) {

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, 20);
                GuiHelper.drawHollowRect(graphics, x, y, w, 20, NordColors.POLAR_NIGHT_4, true);

            }

            @Override
            public void onClicked(MouseButton mouseButton) {

                closeGui();

            }

        });

    }

    public void spawnButton(){

            Iterator<Widget> w = widgets.iterator();
            while (w.hasNext()) {
                Widget w1 = w.next();
                if (w1.equals(confirm)) {
                    w.remove();
                    break;
                }
            }

        add(confirm = new SimpleTextButton(this, Component.translatable("sdm_shop.buying_window.confirm"), Icon.empty()){
            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, 20);
                GuiHelper.drawHollowRect(graphics, x, y, w, 20, NordColors.POLAR_NIGHT_4, true);
            }

            @Override
            public void onClicked(MouseButton mouseButton) {

                if (!tovar.toSell)NetworkManager.sendToServer(new BuyShopTovarC2S(TovarList.CLIENT.tovarList.indexOf(tovar),Integer.valueOf(countTxt.getText())));
                    else NetworkManager.sendToServer(new SellShopTovarC2S(TovarList.CLIENT.tovarList.indexOf(tovar),Integer.valueOf(countTxt.getText())));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                   e.printStackTrace();
                }
                closeGui();
            }

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }
        });
        confirm.setPosAndSize(3,height - 22, width/2 - 5, 20);

    }

    @Override
    public void alignWidgets(){
        String titleText = ((TextFieldMixin)this.title).getRawText().getString();
        float titleScale = TextHelper.getTextRenderSize(titleText, width - 34, 1f,50).y;
        float titleChange = (Theme.DEFAULT.getStringWidth(titleText) - Theme.DEFAULT.getStringWidth(titleText) * titleScale) / 2;

        title.setScale(titleScale);
        title.setPos((width - 34 )/ 2 -Theme.DEFAULT.getStringWidth(titleText) / 2 + 32,5);
        if(titleScale < 0.99)title.setPos((int) ((width - 34 )/ 2 -Theme.DEFAULT.getStringWidth(titleText) / 2 + titleChange + 33) ,5);
        title.resize(Theme.DEFAULT);

        cost.setPos( (width - 34 )/ 2 - Theme.DEFAULT.getStringWidth(currency.specialSymbol + " " + tovar.cost) / 2 + 32, 19);
        cost.setText(currency.specialSymbol + " " + tovar.cost);

        if(!tovar.toSell){

            moneyTxt.setText(Component.translatable("sdm_shop.buying_window.money"));
            moneyNum.setText(String.valueOf(currency.moneys));

            mayBuyTxt.setText(Component.translatable("sdm_shop.buying_window.may_buy"));
            mayBuyNum.setText(String.valueOf((int) (currency.moneys / tovar.cost)));
            receiptTxt.setText(Component.translatable("sdm_shop.buying_window.receipt_1"));
            moneyTxt.setPos(7, 35);
            mayBuyTxt.setPos(7, 50);
        }else {
            countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).is(tovar.item.getItem()) && ItemStack.isSameItemSameComponents(inventory.getItem(i),tovar.item)) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            moneyTxt.setText(Component.translatable("sdm_shop.buying_window.count_in_inventory"));
            moneyTxt.setScale(0.8f);
            moneyNum.setText(String.valueOf(countItems));


            mayBuyTxt.setText(Component.translatable("sdm_shop.buying_window.may_sell"));
            mayBuyTxt.setScale(0.5f);
            mayBuyNum.setText(String.valueOf(countItems / tovar.item.getCount()));
            receiptTxt.setText(Component.translatable("sdm_shop.buying_window.receipt_2"));
            moneyTxt.setPos(7, 37);
            mayBuyTxt.setPos(7, 51);
        }

        moneyNum.setPos(width / 2 + 7, 36);

        mayBuyNum.setPos(width / 2 + 7, 50);

        limitTxt.setPos(width / 2 - Theme.DEFAULT.getStringWidth(((TextFieldMixin)limitTxt).getRawText().getString()) / 2, 63);

        if(tovar.limit == -1) limit.setText(Component.translatable("sdm_shop.buying_window.unlimited"));
        limit.setPos(width / 2 - Theme.DEFAULT.getStringWidth(((TextFieldMixin)limit).getRawText().getString()) / 2, 78);

        countTxt.setPos(3, 89);
        countTxt.setSize(width  - 5, 12);

        receiptTxt.setPos(7, 106);

        receipt.setPos(width / 2 + 7, 106);


        cancel.setPosAndSize(width/2 + 3,height - 22, width/2 - 5, 20);

    }



}
