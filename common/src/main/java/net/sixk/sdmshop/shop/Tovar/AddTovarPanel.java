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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.CheckBox;
import net.sixk.sdmshop.shop.ShopPage;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarCommand;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarItem;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarXP;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class AddTovarPanel extends BaseScreen {

    private Component name;
    private Panel base;
    private TextField itemName;
    private TextField itemCostTxt;
    private TextField itemLimTxt;
    private TextField currency;
    private TextField currencyNum;
    private TextField error;
    private TextField tab;
    private TextField tovarTab;
    private TextField checkBoxTxt;
    private TextField countXpLvlTxt;
    private TextField xpORlvl;
    private TextField byTagTxt;
    private TextField tagTxt;
    private TextField commandTxt;
    private TextField tagSelected;
    private TextBox countXpLvl;
    private TextBox itemCost;
    private TextBox itemLim;
    private TextBox command;
    private SimpleButton addItem;
    private SimpleButton select;
    private SimpleButton selectTab;
    private SimpleButton selectTag;
    private SimpleTextButton apply;
    private SimpleTextButton cancel;
    private Panel tabPanel;
    private Panel currencyPanel;
    private Panel tagPanel;
    private CheckBox isSell;
    private CheckBox isLvl;
    private CheckBox byTag;
    private Tovar tovar;
    private Icon icon;
    private ItemStack itemStack;
    private String save1;
    private String save2;
    private String currencyName;
    private String tabName ;
    private TagKey tag ;
    private String id;
    private boolean save3;
    private boolean save4;
    private boolean save6;
    private int save5 = 1;
    private int shift = 0;
    private boolean editMod = false;



    public AddTovarPanel(@Nullable String tab, String id){

        tabName = tab;
        this.id = id;
        this.name = Component.empty();

    }

    public AddTovarPanel(Tovar tovar){

        this.tovar = tovar;
        icon = tovar.abstractTovar.getIcon();
        this.name = Component.nullToEmpty(tovar.abstractTovar.getTitel());

        id = tovar.abstractTovar.getID();
        save1 = String.valueOf(tovar.limit);
        if(tovar.limit == -1) save1 = "";
        save2 = String.valueOf(tovar.cost);
        currencyName = tovar.currency;
        tabName = tovar.tab;
        editMod = true;
        save3 = tovar.toSell;
        if(id.equals("ItemType")) {
            itemStack = (ItemStack) tovar.abstractTovar.getItemStack();
            save6 = tovar.abstractTovar.getisXPLVL();
            tag = tovar.abstractTovar.getTag();
        }
        if (id.equals("XPType")) {
            save4 = tovar.abstractTovar.getisXPLVL();
            save5 = (int) tovar.abstractTovar.getItemStack();
        }
    }


    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth());
        setHeight(getScreen().getGuiScaledHeight());

        return true;
    }


    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }

    @Override
    public void addWidgets() {
        add(base = new Panel(this) {

            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                NordColors.POLAR_NIGHT_0.draw(graphics, x  , y , w  ,h );
                GuiHelper.drawHollowRect(graphics, x  , y, w, h, NordColors.POLAR_NIGHT_4, false);

            }

            @Override
            public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                NordColors.POLAR_NIGHT_1.draw(graphics, x + 3, y + 3, 26, 26);
                GuiHelper.drawHollowRect(graphics, x + 3, y + 3, 27, 27, NordColors.POLAR_NIGHT_4, false);
                GuiHelper.drawHollowRect(graphics, x + 4, y + 4, 25, 25, NordColors.POLAR_NIGHT_3, false);
                if(icon != null)
                    drawIcon(graphics, x + 7, y + 7, w, h);



            }

            public void drawIcon(GuiGraphics graphics, int x, int y, int w, int h) {
                icon.draw(graphics, x, y, 20, 20);
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
                add(isSell = new CheckBox(this){
                    @Override
                    public void onClicked(MouseButton mouseButton) {
                        if(id.equals("CommandType")) return;
                        super.onClicked(mouseButton);
                    }
                });
                itemLim.setText(save1);
                itemCost.setText(save2);
                isSell.setCheck(save3);

                if(id.equals("XPType")) {

                    widgets.remove(addItem);

                    icon = ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE);
                    name = Component.translatable("sdm_shop.tovar_panel.name_xp");
                    shift = 28;

                    add(countXpLvlTxt = new TextField(this).setText(Component.translatable("sdm_shop.tovar_panel.count_xp_lvl")));
                    countXpLvlTxt.setPos(+ 2, 62 + shift);

                    add(xpORlvl = new TextField(this).setText(Component.translatable("sdm_shop.tovar_panel.bool_xp_or_lvl")));
                    xpORlvl.setPosAndSize(69 + 8, 63 + shift ,69, 12);
                    xpORlvl.setScale(0.8f);

                    add(countXpLvl = new TextBox(this));
                    countXpLvl.setPosAndSize(2, 70 + shift ,69, 12);

                    add(isLvl = new CheckBox(this));
                    isLvl.setPosAndSize(86,71 + shift,10,10);

                    isLvl.setCheck(save4);
                    countXpLvl.setText(String.valueOf(save5));

                }

                if(id.equals("CommandType")){

                    if(itemStack == null)
                        icon = ItemIcon.getItemIcon(Items.BARRIER);

                    name = Component.translatable("sdm_shop.tovar_panel.name_command");
                    shift = 28;

                    add(commandTxt = new TextField(base).setText(Component.translatable(  "sdm_shop.addtovar.command")));
                    commandTxt.setPos(+ 2, 62 + shift);

                    add(command = new TextBox(base));
                    command.setPosAndSize(+ 2, 72 + shift,69, 12);
                }

                add(addItem = new SimpleButton(this, Component.translatable("sdm_shop.addtovar.additem"), icon == null ? Icons.ADD : Icon.empty(), ((simpleButton, mouseButton) -> {

                    ItemStackConfig item = new ItemStackConfig(false, false);
                    AddTovarPanel gui = (AddTovarPanel) getGui();

                    new SelectItemStackScreen(item, set -> {

                        if (set) {
                            icon = ItemIcon.getItemIcon(item.getValue());
                            itemStack = item.getValue();
                            if(id.equals("ItemType")) name = item.getValue().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL).get(0);
                            gui.refreshWidgets();
                            gui.openGui();
                        }


                    }).openGui();
                })));

                addItem.setPos(9, 8);


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

                        if(itemStack == null && id.equals("ItemType")){
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

                        if (isSell.CheckIs() && byTag.CheckIs() && tag == null){
                            error.setText(Component.translatable("sdm_shop.addtovar.error_7"));
                            error.setScale(0.6f);
                            return;
                        }

                        if(id.equals("CommandType"))
                            if (command.getText().isEmpty()){
                            error.setText(Component.translatable("sdm_shop.addtovar.error_8"));
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
                            Tovar newTovar = null;
                            switch (id){
                                case "ItemType" :
                                    newTovar =  new Tovar( tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs()).setAbstract(new TovarItem(itemStack,byTag.CheckIs(),tag));
                                    break;
                                case "XPType" :
                                    newTovar =  new Tovar( tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs()).setAbstract(new TovarXP(Integer.valueOf(countXpLvl.getText()),isLvl.CheckIs()));
                                    break;
                                case "CommandType" :
                                    newTovar = new Tovar(tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs()).setAbstract(new TovarCommand(command.getText()));
                                    break;
                            };

                            TovarList.CLIENT.tovarList.set(TovarList.CLIENT.tovarList.indexOf(tovar), newTovar);

                        } else
                        {

                            Tovar newTovar = null;
                            switch (id){
                                case "ItemType" :
                                    newTovar =  new Tovar(tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs()).setAbstract(new TovarItem(itemStack,byTag.CheckIs(),tag));
                                    break;
                                case "XPType" :
                                    newTovar =  new Tovar(tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs()).setAbstract(new TovarXP(Integer.valueOf(countXpLvl.getText()),isLvl.CheckIs()));
                                    break;
                                case "CommandType" :
                                    newTovar = new Tovar(tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs()).setAbstract(new TovarCommand(command.getText()));
                                    System.out.println(command.getText());
                                    break;
                            };

                            TovarList.CLIENT.tovarList.add(newTovar);
                        }
                        NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarList.CLIENT.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                        new ShopPage().openGui();
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
                    save6 = byTag.CheckIs();

                    if (id.equals("XPType")) {
                        save4 = isLvl.CheckIs();
                        save5 = Integer.parseInt(countXpLvl.getText());
                    }

                    if(tagPanel != null) widgets.remove(tagPanel);
                    if(tabPanel != null) widgets.remove(tabPanel);
                    if(currencyPanel != null) widgets.remove(currencyPanel);

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

                    currencyPanel.setPosAndSize(15, 77,40,68);

                    currencyPanel.addWidgets();
                    currencyPanel.alignWidgets();

                })));

                add(selectTab = new SimpleButton(this, Component.empty(),Icons.DOWN,((simpleButton, mouseButton) -> {

                    save1 = itemLim.getText();
                    save2 = itemCost.getText();
                    save3 = isSell.CheckIs();
                    save6 = byTag.CheckIs();
                    if (id.equals("XPType")) {
                        save4 = isLvl.CheckIs();
                        save5 = Integer.parseInt(countXpLvl.getText());
                    }

                    if(tagPanel != null) widgets.remove(tagPanel);
                    if(currencyPanel != null) widgets.remove(currencyPanel);
                    if(tabPanel != null) widgets.remove(tabPanel);

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

                    tabPanel.setPosAndSize(15, 49,100,100);

                    tabPanel.addWidgets();
                    tabPanel.alignWidgets();

                })));


                add(byTagTxt = new TextField(base){

                    @Override
                    public boolean isEnabled() {
                        return isSell.CheckIs() && id.equals("ItemType");
                    }

                    @Override
                    public boolean shouldDraw() {
                        return isSell.CheckIs() && id.equals("ItemType");
                    }

                }.setText(Component.translatable("sdm_shop.addtovar.byTag_txt")));
                byTagTxt.setPos(2, 111 + shift);

                add(byTag = new CheckBox(base){


                    @Override
                    public boolean isEnabled() {
                        return isSell.CheckIs() && id.equals("ItemType");
                    }

                    @Override
                    public boolean shouldDraw() {
                        return isSell.CheckIs() && id.equals("ItemType");
                    }


                });
                byTag.setCheck(save6);
                byTag.setPosAndSize( byTagTxt.width / 2 - 3, 121 + shift, 10, 10);



                add(tagTxt = new TextField(base){
                    @Override
                    public boolean isEnabled() {
                        return isSell.CheckIs() && byTag.CheckIs();
                    }

                    @Override
                    public boolean shouldDraw() {
                        return isSell.CheckIs() && byTag.CheckIs();
                    }
                }.setText(Component.translatable("sdm_shop.addtovar.Tag_txt")));
                tagTxt.setPos(2, 133 + shift);

                add(selectTag = new SimpleButton(base, Component.empty(), Icons.DOWN, ((simpleButton, mouseButton1) -> {
                    if(itemStack == null) return;
                    save1 = itemLim.getText();
                    save2 = itemCost.getText();
                    save3 = isSell.CheckIs();
                    save6 = byTag.CheckIs();

                    if (tagPanel != null) widgets.remove(tagPanel);
                    if (tabPanel != null) widgets.remove(tabPanel);
                    if (currencyPanel != null) widgets.remove(currencyPanel);

                    add(tagPanel = new Panel(base) {

                        SimpleTextButton setTag;
                        static List<SimpleTextButton> tagList = new ArrayList<>();

                        @Override
                        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                            NordColors.POLAR_NIGHT_0.draw(graphics, x, y, 40, 70);
                            GuiHelper.drawHollowRect(graphics, x, y, 40, 70, NordColors.POLAR_NIGHT_4, false);

                        }

                        @Override
                        public void addWidgets() {

                            tagList.clear();

                            for (TagKey w : itemStack.getTags().toList()) {

                                String i = w.location().toString();

                                setTag = new SimpleTextButton(this, Component.literal(i), Icon.empty()) {
                                    @Override
                                    public void onClicked(MouseButton mouseButton) {

                                        tag = w;
                                        AddTovarPanel gui = (AddTovarPanel) getGui();
                                        gui.refreshWidgets();

                                    }

                                };

                                add(setTag);
                                tagList.add(setTag);

                            }

                            for (int n = 0; n < tagList.size(); n++) {

                                SimpleTextButton w = tagList.get(n);
                                w.setSize(36, 9);
                                w.setPos(2, 1 + 10 * n);
                            }


                        }

                        @Override
                        public void alignWidgets() {

                        }
                    });

                    tagPanel.setPosAndSize(15, 77, 40, 68);

                    tagPanel.addWidgets();
                    tagPanel.alignWidgets();

                })){
                    @Override
                    public boolean isEnabled() {
                        return isSell.CheckIs() && byTag.CheckIs();
                    }

                    @Override
                    public boolean shouldDraw() {
                        return isSell.CheckIs() && byTag.CheckIs();
                    }
                });
                selectTag.setPosAndSize(2, 143 + shift, 12, 12);

                add(tagSelected = new TextField(base){
                    @Override
                    public boolean isEnabled() {
                        return isSell.CheckIs() && byTag.CheckIs();
                    }

                    @Override
                    public boolean shouldDraw() {
                        return isSell.CheckIs() && byTag.CheckIs();
                    }
                });
                tagSelected.setPos(17, 143 + shift);



            }

            @Override
            public void alignWidgets() {
                itemName.setPos( 33, 5);
                itemName.setMaxWidth(width - 33);
                itemName.setText(name);
                itemName.resize(Theme.DEFAULT);

                tab.setPos(3, 34);
                tab.setText(Component.translatable("sdm_shop.addtovar.tab"));

                if(Theme.DEFAULT.getStringWidth(((TextFieldMixin)tab).getRawText()) > getWidth() / 5) tab.setScale(0.7f);
                selectTab.setPosAndSize( 2, 44,12,12);
                tovarTab.setPos(15, 46);

                currency.setPos( 2, 62 );
                currency.setText(Component.translatable("sdm_shop.addtovar.currency"));

                select.setPos(2, 72 );
                select.setSize(12,12);

                currencyNum.setPos( 15, 74 );
                currencyNum.setSize(30,12);

                itemCostTxt.setPos( currency.getWidth() + 10,62 );
                itemCostTxt.setText(Component.translatable("sdm_shop.addtovar.cost"));

                itemCost.setPosAndSize( currency.getWidth() + 10, 72 , 50,12);

                itemLimTxt.setPos( 2, 89 + shift);
                itemLimTxt.setText(Component.translatable("sdm_shop.addtovar.limit"));

                itemLim.setPos( 2, 96 + shift);
                itemLim.setSize(69, 12);

                checkBoxTxt.setPos(itemLim.width + 8, 89 + shift);
                checkBoxTxt .setText(Component.translatable("sdm_shop.addtovar.tosell"));

                if(Component.translatable("sdm_shop.addtovar.tosell").getString().equals("Is sell ?")) {
                    itemLimTxt.setScale(0.8f);
                    checkBoxTxt.setScale(0.8f);
                } else {
                    itemLimTxt.setScale(0.5f);
                    checkBoxTxt.setScale(0.5f);
                }

                isSell.setPosAndSize(86 ,97 + shift, 10,10);

                apply.setPosAndSize(8 , base.getHeight() - 15,45,12);

                cancel.setPosAndSize(60 , base.getHeight() - 15,45, 12);

                error.setPosAndSize(69 + 5,35,15,30);
                error.setColor(Color4I.RED);
                error.setMaxWidth(70);

                String i = Component.translatable("sdm_shop.currency." + currencyName).getString();
                if(i.equals("sdm_shop.currency." + currencyName)) i = currencyName;
                //currencyTxt.setText( i + " ");

                if(currencyName != null) currencyNum.setText(i);
                if(tabName != null) tovarTab.setText(tabName);
                tagSelected.setMaxWidth(width - 10);
                if(tag != null) tagSelected.setText(tag.location().toString());
                itemName.resize(Theme.DEFAULT);
            }
        });

        base.setPosAndSize(width / 2 - 57, height / 2 - 92 , 114,184);
    }



    @Override
    public void alignWidgets() {



    }
}
