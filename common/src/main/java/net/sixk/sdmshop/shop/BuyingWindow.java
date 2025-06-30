package net.sixk.sdmshop.shop;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmuilibrary.client.utils.renders.TextRenderHelper;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.client.BuyShopTovarC2S;
import net.sixk.sdmshop.shop.network.client.SellShopTovarC2S;

import java.util.Iterator;
import java.util.UUID;


public class BuyingWindow extends BaseScreen {

    public AbstractTovar tovar;
    public CurrencyPlayerData.PlayerCurrency currency;
    public TextField title;
    public TextField cost;
    public TextField moneyTxt;
    public TextField moneyNum;
    public TextField mayBuyTxt;
    public TextField mayBuyNum;
    public TextField limitTxt;
    public TextField limit;
    public TextField receiptTxt;
    public TextField receipt;
    public TextBox countTxt;
    public SimpleTextButton confirm;
    public SimpleTextButton cancel;
    public int stackCount;
    public String id;
    public ItemStack item;
    public long count;
    public int countItems = 0;

    public BuyingWindow(UUID uuid) {
        this.analysisTovar(uuid);
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

        tovar.abstractTovar.getIcon().draw(graphics, x + 6, y + 7, 20, 20);
    }




    @Override
    public void addWidgets() {

        add(title = new TextField(this).setText(tovar.abstractTovar.getTitel()));
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
                        if (Integer.valueOf(countTxt.getText()) > countItems / stackCount) countTxt.setText(String.valueOf(countItems / stackCount));
                    } else {
                        if(Integer.valueOf(countTxt.getText()) > (int) (currency.balance / tovar.cost)) countTxt.setText(String.valueOf((int) (currency.balance / tovar.cost)));
                    }
                    if((int) (currency.balance / tovar.cost) != 0  && !tovar.toSell)spawnButton();
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

    public void analysisTovar(UUID uuid) {
        Iterator var2 = TovarList.CLIENT.tovarList.iterator();

        while(var2.hasNext()) {
            AbstractTovar t = (AbstractTovar) var2.next();
            if (t.uuid == uuid) {
                this.tovar = t;
            }
        }

        var2 = EconomyAPI.getPlayerCurrencyClientData().currencies.iterator();

        while(var2.hasNext()) {
            CurrencyPlayerData.PlayerCurrency w1 = (CurrencyPlayerData.PlayerCurrency)var2.next();
            if (this.tovar.currency.equals(w1.currency.getName())) {
                this.currency = w1;
                break;
            }
        }

        id = tovar.getID();
        switch (this.id) {
            case "ItemType":
                ItemStack item = (ItemStack)tovar.abstractTovar.getItemStack();
                this.stackCount = item.getCount();
                this.item = item;
                break;
            case "XPType":
                this.stackCount = (Integer)tovar.abstractTovar.getItemStack();
        }

    }

    @Override
    public void alignWidgets(){
        String titleText = ((TextFieldMixin)this.title).getRawText().getString();
        float titleScale = TextRenderHelper.getTextRenderSize(titleText, width - 34, 1.0F, 50).y;
        float titleChange = ((float)Theme.DEFAULT.getStringWidth(titleText) - (float)Theme.DEFAULT.getStringWidth(titleText) * titleScale) / 2.0F;
            this.title.setScale(titleScale);
            this.title.setPos((width - 34) / 2 - Theme.DEFAULT.getStringWidth(titleText) / 2 + 32, 5);
            if ((double)titleScale < 0.99) {
            this.title.setPos((int)((float)((width - 34) / 2 - Theme.DEFAULT.getStringWidth(titleText) / 2) + titleChange + 33.0F), 5);
        }

            this.title.resize(Theme.DEFAULT);
            this.cost.setPos((width - 34) / 2 - Theme.DEFAULT.getStringWidth(currency.currency.symbol.value + " " + tovar.cost) / 2 + 32, 19);
            this.cost.setText(currency.currency.symbol.value + " " + tovar.cost);
            if (!tovar.toSell) {
                moneyTxt.setText(Component.translatable("sdm_shop.buying_window.money"));
                moneyNum.setText(String.valueOf(currency.balance));
                mayBuyTxt.setText(Component.translatable("sdm_shop.buying_window.may_buy"));
                mayBuyNum.setText(String.valueOf((int)(currency.balance / (double)tovar.cost)));
                receiptTxt.setText(Component.translatable("sdm_shop.buying_window.receipt_1"));
                moneyTxt.setPos(7, 35);
                mayBuyTxt.setPos(7, 50);
            } else {
                countItems = 0;
                if (id.equals("ItemType")) {
                    Inventory inventory = Minecraft.getInstance().player.getInventory();
                    int i;
                    if (tovar.abstractTovar.getisXPLVL()) {
                        for(i = 0; i < inventory.getContainerSize(); ++i) {
                            if (inventory.getItem(i) != null && inventory.getItem(i).is(tovar.abstractTovar.getTag())) {
                                countItems += inventory.getItem(i).getCount();
                            }
                        }
                    } else {
                        for(i = 0; i < inventory.getContainerSize(); ++i) {
                            if (inventory.getItem(i).is(item.getItem()) && ItemStack.isSameItemSameComponents(inventory.getItem(i),item)) {
                                this.countItems += inventory.getItem(i).getCount();
                            }
                        }
                    }
                } else if (tovar.abstractTovar.getisXPLVL()) {
                    countItems = Minecraft.getInstance().player.experienceLevel;
                } else {
                    countItems = Minecraft.getInstance().player.totalExperience;
                }

                moneyTxt.setText(Component.translatable("sdm_shop.buying_window.count_in_inventory"));
                moneyTxt.setScale(0.8F);
                moneyNum.setText(String.valueOf(this.countItems));
                mayBuyTxt.setText(Component.translatable("sdm_shop.buying_window.may_sell"));
                mayBuyTxt.setScale(0.5F);
                mayBuyNum.setText(String.valueOf(this.countItems / this.stackCount));
                receiptTxt.setText(Component.translatable("sdm_shop.buying_window.receipt_2"));
                moneyTxt.setPos(7, 37);
                mayBuyTxt.setPos(7, 51);
            }

        moneyNum.setPos(width / 2 + 7, 36);
        mayBuyNum.setPos(width / 2 + 7, 50);
        limitTxt.setPos(width / 2 - Theme.DEFAULT.getStringWidth(((TextFieldMixin)limitTxt).getRawText().getString()) / 2, 63);
        if (tovar.limit == -1L) {
            limit.setText(Component.translatable("sdm_shop.buying_window.unlimited"));
        }

        limit.setPos(width / 2 - Theme.DEFAULT.getStringWidth(((TextFieldMixin)limit).getRawText().getString()) / 2, 78);
        countTxt.setPos(3, 89);
        countTxt.setSize(width - 5, 12);
        receiptTxt.setPos(7, 106);
        receipt.setPos(width / 2 + 7, 106);
        cancel.setPosAndSize(width / 2 + 3, height - 22, width / 2 - 5, 20);
    }
}
