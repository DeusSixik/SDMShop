package net.sixk.sdmshop.shop.Tovar;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.CheckBox;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class AddTovarPanel extends BaseScreen {

    private Component name;
    private TextField itemName;
    private TextField itemCostTxt;
    private TextField itemLimTxt;
    private TextField currency;
    private TextField currencyNum;
    private TextField error;
    private TextField tab;
    private TextField tovarTab;
    private TextField checkBoxTxt;
    private TextBox itemCost;
    private TextBox itemLim;
    private SimpleButton addItem;
    private SimpleButton select;
    private SimpleButton selectTab;
    private SimpleTextButton apply;
    private SimpleTextButton cancel;
    private CheckBox isSell;
    private Tovar tovar;
    private Icon icon;
    private ItemStack itemStack;
    private Panel tabPanel;
    private Panel currencyPanel;
    private String save1;
    private String save2;
    private boolean save3;
    private String currencyName;
    private String tabName ;
    private boolean editMod = false;



    public AddTovarPanel(@Nullable String tab){

        tabName = tab;
        this.name = Component.empty();

    }

    public AddTovarPanel(Tovar tovar){

        this.tovar = tovar;
        icon = ItemIcon.getItemIcon(tovar.item);
        this.name = tovar.item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL).get(0);
        itemStack = tovar.item;
        save1 = String.valueOf(tovar.limit);
        if(tovar.limit == -1) save1 = "";
        save2 = String.valueOf(tovar.cost);
        currencyName = tovar.currency;
        tabName = tovar.tab;
        editMod = true;
        save3 = tovar.toSell;
    }


    @Override
    public boolean onInit() {

        setWidth(341);
        setHeight(192);

        return true;
    }


    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_0.draw(graphics, x  + (w / 3) , y , w / 3,h);
        GuiHelper.drawHollowRect(graphics, x  + (w / 3), y, w / 3, h, NordColors.POLAR_NIGHT_4, false);

    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

            NordColors.POLAR_NIGHT_1.draw(graphics, x + (w / 3) + 3, y + 3, 26, 26);
            GuiHelper.drawHollowRect(graphics, x + (w / 3) + 3, y + 3, 27, 27, NordColors.POLAR_NIGHT_4, false);
            GuiHelper.drawHollowRect(graphics, x + (w / 3) + 4, y + 4, 25, 25, NordColors.POLAR_NIGHT_3, false);
            if(icon != null)
                drawIcon(graphics, x + (w / 3) + 7, y + 7, w, h);
            //currencyNum.setText(testSign);


    }

    public void drawIcon(GuiGraphics graphics, int x, int y, int w, int h) {
        this.icon.draw(graphics, x, y, 20, 20);
    }


    @Override
    public void addWidgets() {

        add(itemName = new TextField(this));
        add(itemCostTxt = new TextField(this));
        add(itemLimTxt = new TextField(this));
        add(currency = new TextField(this));
        add(error = new TextField(this));
        add(currencyNum = new TextField(this));
        add(tovarTab = new TextField(this));
        add(tab = new TextField(this));
        add(itemLim = new TextBox(this));
        add(itemCost = new TextBox(this));
        add(checkBoxTxt = new TextField(this));
        add(isSell = new CheckBox(this));
        itemLim.setText(save1);
        itemCost.setText(save2);
        isSell.setCheck(save3);



            add(addItem = new SimpleButton(this, Component.translatable("sdm_shop.addtovar.additem"), icon == null ? Icons.ADD : Icon.empty(), ((simpleButton, mouseButton) -> {

                ItemStackConfig item = new ItemStackConfig(false, false);
                AddTovarPanel gui = (AddTovarPanel) getGui();

                new SelectItemStackScreen(item, set -> {

                    if (set) {
                        this.icon = ItemIcon.getItemIcon(item.getValue());
                        itemStack = item.getValue();
                        name = item.getValue().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL).get(0);
                        gui.refreshWidgets();
                        gui.openGui();
                    }


                }).openGui();
            })));

            addItem.setPos(getWidth() / 3 + 9, 8);


        add(apply = new SimpleTextButton(this,Component.translatable("sdm_shop.apply"),Icon.empty()) {

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, true);
            }

            @Override
            public void onClicked(MouseButton mouseButton) {

                if(itemStack == null){
                    error.setText(Component.translatable("sdm_shop.addtovar.error_1"));
                    error.setScale(0.6f);
                    return;
                }

                if(tabName == null){
                    error.setText(Component.translatable("sdm_shop.addtovar.error_2"));
                    error.setScale(0.6f);
                    return;
                }

                for (int i = 0; i < itemLim.getText().length(); i++) {
                    if(!Character.isDigit(itemLim.getText().charAt(i))){
                        error.setText(Component.translatable("sdm_shop.addtovar.error_3"));
                        error.setScale(0.6f);
                        return;
                    }
                }

                for (int i = 0; i < itemCost.getText().length(); i++) {
                    if(!Character.isDigit(itemCost.getText().charAt(i))){
                        error.setText(Component.translatable("sdm_shop.addtovar.error_4"));
                        error.setScale(0.6f);
                        return;
                    }
                }

                if(currencyName == null){

                    error.setText(Component.translatable("sdm_shop.addtovar.error_6"));
                    error.setScale(0.6f);

                    return;
                }
                if (itemCost.getText().isEmpty()){
                    error.setText(Component.translatable("sdm_shop.addtovar.error_5"));
                    error.setScale(0.6f);
                    return;
                }

                long lim;

                if(itemLim.getText().isEmpty()) {

                    lim = -1;

                }
                else {

                    lim = Long.parseLong(itemLim.getText());
                }


                if(editMod) {

                    Tovar newTovar = new Tovar(itemStack, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs());
                    TovarList.CLIENT.tovarList.set(TovarList.CLIENT.tovarList.indexOf(tovar), newTovar);

                } else
                {
                    Tovar newTovar = new Tovar(itemStack, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim,isSell.CheckIs());
                    TovarList.CLIENT.tovarList.add(newTovar);
                }
                NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarList.CLIENT.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                closeGui();

            }
        });

        add(cancel = new SimpleTextButton(this,Component.translatable("sdm_shop.cancel"),Icon.empty()) {

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, true);
            }

            @Override
            public void onClicked(MouseButton mouseButton) {

                closeGui();

            }
        });

        add(select = new SimpleButton(this, Component.empty(),Icons.DOWN,((simpleButton, mouseButton) -> {

            save1 = itemLim.getText();
            save2 = itemCost.getText();
            save3 = isSell.CheckIs();

            if(tabPanel != null) tabPanel.clearWidgets();

            add(currencyPanel = new Panel(this){

                SimpleTextButton test1;
                static List<SimpleTextButton> tesBL = new ArrayList<>();

                @Override
                public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                    NordColors.POLAR_NIGHT_0.draw(graphics, x, y, 40, 70);
                    GuiHelper.drawHollowRect(graphics, x, y, 40, 70,NordColors.POLAR_NIGHT_4,false);

                }

                @Override
                public void addWidgets() {

                    tesBL.clear();

                    for (AbstractCurrency w : PlayerMoneyData.CLIENT.CLIENT_MONET.currencies) {

                        String i = Component.translatable("sdm_shop.currency." + w.getID()).getString();
                        if(i.equals("sdm_shop.currency." + w.getID())) i = w.getID();
                        test1 = new SimpleTextButton(this, Component.literal(i), Icon.empty()) {
                            @Override
                            public void onClicked(MouseButton mouseButton) {

                                currencyName = w.getID();
                                AddTovarPanel gui = (AddTovarPanel) getGui();
                                gui.refreshWidgets();

                            }

                        };

                        add(test1);
                        tesBL.add(test1);

                    }

                    for (int n = 0; n < tesBL.size(); n++) {

                        SimpleTextButton w = tesBL.get(n);
                        w.setSize(36,9);
                        w.setPos( 2,1 + 10 * n);
                    }



                }

                @Override
                public void alignWidgets() {


                }
            });

            currencyPanel.setPosAndSize(getWidth() / 3 + 25, 74,40,68);

            currencyPanel.addWidgets();
            currencyPanel.alignWidgets();

        })));

        add(selectTab = new SimpleButton(this, Component.empty(),Icons.DOWN,((simpleButton, mouseButton) -> {

            save1 = itemLim.getText();
            save2 = itemCost.getText();
            save3 = isSell.CheckIs();

            if(currencyPanel != null) currencyPanel.clearWidgets();

            add(tabPanel = new Panel(this) {

                SimpleTextButton test1;
                static List<SimpleTextButton> tesBL = new ArrayList<>();

                @Override
                public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                    NordColors.POLAR_NIGHT_0.draw(graphics, x, y, 40, 70);
                    GuiHelper.drawHollowRect(graphics, x, y, 40, 70,NordColors.POLAR_NIGHT_4,false);

                }

                @Override
                public void addWidgets() {

                    tesBL.clear();

                    for (String w : TovarTab.CLIENT.tabList) {

                        test1 = new SimpleTextButton(this, Component.literal(w), Icon.empty()) {
                            @Override
                            public void onClicked(MouseButton mouseButton) {
                                tabName = w;
                                AddTovarPanel gui = (AddTovarPanel) getGui();
                                gui.refreshWidgets();

                            }

                        };

                        add(test1);
                        tesBL.add(test1);

                    }

                    for (int n = 0; n < tesBL.size(); n++) {

                        SimpleTextButton w = tesBL.get(n);
                        w.setSize(36,9);
                        w.setPos( 2,1 + 10 * n);
                    }
                }

                @Override
                public void alignWidgets() {

                }
            });

            tabPanel.setSize(100,100);
            tabPanel.setPos(getWidth() / 3 + 25, 74);

            tabPanel.addWidgets();
            tabPanel.alignWidgets();

        })));

    }

    @Override
    public void alignWidgets() {

        itemName.setPos( getWidth() / 3 + 33, 5);
        itemName.setMaxWidth(getWidth() / 3 - 33);
        itemName.setText(name);
        itemName.resize(Theme.DEFAULT);

        tab.setPos(getWidth() / 3 + 3, 34);
        tab.setText(Component.translatable("sdm_shop.addtovar.tab"));
        if(Theme.DEFAULT.getStringWidth(((TextFieldMixin)tab).getRawText()) > getWidth() / 5) tab.setScale(0.7f);
        selectTab.setPosAndSize(getWidth() / 3 + 2, 44,12,12);
        tovarTab.setPos(getWidth() / 3 + 15, 44);

        currency.setPos( getWidth() / 3 + 2, 62);
        currency.setText(Component.translatable("sdm_shop.addtovar.currency"));

        select.setPos(getWidth() / 3 + 2, 70);
        select.setSize(12,12);

        itemCostTxt.setPos( getWidth() / 3 + currency.getWidth() + 10,62);
        itemCostTxt.setText(Component.translatable("sdm_shop.addtovar.cost"));

        currencyNum.setPos( getWidth() / 3 + 15, 74);
        currencyNum.setSize(30,12);

        itemLimTxt.setPos( getWidth() / 3 + 2, 89);
        itemLimTxt.setText(Component.translatable("sdm_shop.addtovar.limit"));

        itemLim.setPos( getWidth() / 3 + 2, 96);
        itemLim.setSize(69, 12);

        checkBoxTxt.setPos(getWidth() / 3 + itemLim.width + 8, 89);
        checkBoxTxt .setText(Component.translatable("sdm_shop.addtovar.tosell"));

        if(Component.translatable("sdm_shop.addtovar.tosell").getString().equals("Is sell ?")) {
            itemLimTxt.setScale(0.8f);
            checkBoxTxt.setScale(0.8f);
        } else {
            itemLimTxt.setScale(0.5f);
            checkBoxTxt.setScale(0.5f);
        }

        isSell.setPosAndSize(getWidth() / 3 + 86 ,97, 10,10);

        itemCost.setPosAndSize( getWidth() / 3 + + currency.getWidth() + 10, 72, 50,12);

        apply.setPos(getWidth() / 3 + (getWidth() / 3) / 2 - 47, height - 20);
        apply.setSize(45,12);

        cancel.setPosAndSize(getWidth() / 3 + (getWidth() / 3) / 2 + 5 , height - 20,45, 12);

        error.setPos(getWidth() / 3 + 69 + 5,35);
        error.setSize(15,30);
        error.setColor(Color4I.RED);
        error.setMaxWidth(70);

        String i = Component.translatable("sdm_shop.currency." + currencyName).getString();
        if(i.equals("sdm_shop.currency." + currencyName)) i = currencyName;
        //currencyTxt.setText( i + " ");

        if(currencyName != null) currencyNum.setText(i);
        if(tabName != null) tovarTab.setText(tabName);

    }
}
