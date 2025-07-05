package net.sixk.sdmshop.shop.Tovar.AddTovar;

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
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarCommand;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarItem;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarTypeRegister;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarXP;
import net.sixk.sdmshop.shop.modern.widgets.ModerTextBox;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;
import net.sixk.sdmshop.shop.widgets.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProperties extends Panel {

    public static Icon icon;
    public static String id;
    public AbstractTovar tovar;
    public UUID uuid;
    public static Component name;
    protected ItemStack itemStack;
    public static TagKey tag;
    public static String tabName;
    protected String currencyName;
    protected TextField itemName;
    protected TextField itemCostTxt;
    protected TextField itemLimTxt;
    protected TextField currency;
    protected TextField currencyNum;
    protected TextField error;
    protected TextField tab;
    protected TextField tovarTab;
    protected TextField checkBoxTxt;
    protected TextField countXpLvlTxt;
    protected TextField xpORlvl;
    protected TextField byTagTxt;
    protected TextField tagTxt;
    protected TextField commandTxt;
    protected TextField tagSelected;
    protected TextBox countXpLvl = new TextBox(this);
    protected TextBox itemCost = new TextBox(this);
    protected TextBox itemLim = new TextBox(this);
    protected TextBox command = new TextBox(this);
    protected SimpleButton addItem;
    protected SimpleButton select;
    protected SimpleButton selectTab;
    protected SimpleButton selectTag;
    protected SimpleTextButton apply;
    protected SimpleTextButton cancel;
    protected Panel tabPanel;
    protected Panel currencyPanel;
    protected Panel tagPanel;
    protected CheckBox isSell;
    protected CheckBox isLvl;
    protected CheckBox byTag;
    protected String save1;
    protected String save2;
    protected boolean save3;
    protected boolean save4;
    protected boolean save6;
    protected int save5 = 1;
    protected boolean editMod = false;
    public static int shift = 0;

    public AddProperties(Panel panel, String tab) {
        super(panel);
        tabName = tab;
        uuid = UUID.randomUUID();
        id = "ItemType";
        name = Component.empty();
        icon = Icons.ADD;
        shift = 0;

    }

    public AddProperties(Panel panel, AbstractTovar tovar) {
        super(panel);
        this.tovar = tovar;
        uuid = tovar.uuid;
        icon = tovar.getIcon();
        name = Component.nullToEmpty(tovar.getTitel());
        id = tovar.getID();
        save1 = String.valueOf(tovar.limit);
        if(tovar.limit == -1) save1 = "";
        save2 = String.valueOf(tovar.cost);
        currencyName = tovar.currency;
        tabName = tovar.tab;
        editMod = true;
        save3 = tovar.toSell;
        if(id.equals("ItemType")) {
            itemStack = (ItemStack) tovar.getItemStack();
            save6 = tovar.getisXPLVL();
            tag = tovar.getTag();
        }
        if (id.equals("XPType")) {
            save4 = tovar.getisXPLVL();
            save5 = (int) tovar.getItemStack();
        }
    }

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
        add(itemLim);
        add(itemCost);
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

            add(countXpLvl);
            if(ConfigFile.CLIENT.style) countXpLvl = new ModerTextBox(this);
            countXpLvl.setPosAndSize(2, 72 + shift ,69, 12);

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

            add(commandTxt = new TextField(this).setText(Component.translatable(  "sdm_shop.addtovar.command")));
            commandTxt.setPos(+ 2, 62 + shift);

            add(command);
            if(ConfigFile.CLIENT.style) command = new ModerTextBox(this);
            command.setPosAndSize(+ 2, 72 + shift,69, 12);
        }

        add(addItem = new SimpleButton(this, Component.translatable("sdm_shop.addtovar.additem"), Icon.empty(), ((simpleButton, mouseButton) -> {

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
                if(ConfigFile.CLIENT.style){
                    RenderHelper.drawRoundedRect(graphics,x,y,w,h,6, RGBA.create(0,0,0, 255/2));
                }else
                {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                    GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, true);
                }
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
                    AbstractTovar newTovar = null;
                    switch (id){
                        case "ItemType" :
                            newTovar =  new TovarItem(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs(),itemStack,byTag.CheckIs(),tag);
                            break;
                        case "XPType" :
                            newTovar =  new TovarXP(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs(),Integer.valueOf(countXpLvl.getText()),isLvl.CheckIs());
                            break;
                        case "CommandType" :
                            newTovar = new TovarCommand(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs(),command.getText());
                            break;
                    };
                    TovarTypeRegister.getType(id).ifPresent(func ->{
                        AbstractTovar w1 = func.apply(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs());
                        w1.update(type->{
                            if(type instanceof TovarItem tovarItem) {
                                tovarItem.item = itemStack;
                                tovarItem.byTag = byTag.CheckIs();
                                tovarItem.tag = tag;
                                return;
                            }
                            if (type instanceof TovarXP tovarXP){
                                tovarXP.xpCount = Integer.parseInt(countXpLvl.getText());
                                tovarXP.isXPLVL = isLvl.CheckIs();
                                return;
                            }
                            if(type instanceof TovarCommand tovarCommand){

                            }
                        });
                    });
                    TovarList.CLIENT.tovarList.set(TovarList.CLIENT.tovarList.indexOf(tovar), newTovar);

                }
                else {
                    AbstractTovar newTovar = null;
                    switch (id){
                        case "ItemType" :
                            newTovar =  new TovarItem(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs(),itemStack,byTag.CheckIs(),tag);
                            break;
                        case "XPType" :
                            newTovar =  new TovarXP(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs(),Integer.valueOf(countXpLvl.getText()),isLvl.CheckIs());
                            break;
                        case "CommandType" :
                            newTovar = new TovarCommand(uuid, icon, tabName, currencyName, Integer.valueOf(itemCost.getText()), lim, isSell.CheckIs(),command.getText());
                            break;
                    };

                    TovarList.CLIENT.tovarList.add(newTovar);
                }

                NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarList.CLIENT.serializeNBT(Minecraft.getInstance().level.registryAccess())));
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
                if(ConfigFile.CLIENT.style){
                    RenderHelper.drawRoundedRect(graphics,x,y,w,h,6, RGBA.create(0,0,0, 255/2));
                }else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                    GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, true);
                }
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

                    for (CurrencyPlayerData.PlayerCurrency w : EconomyAPI.getPlayerCurrencyClientData().currencies) {

                        String i = Component.translatable("sdm_shop.currency." + w.currency.getName()).getString();
                        if(i.equals("sdm_shop.currency." + w.currency.getName())) i = w.currency.getName();
                        test1 = new SimpleTextButton(this, Component.literal(i), Icon.empty()) {
                            @Override
                            public void onClicked(MouseButton mouseButton) {

                                currencyName = w.currency.getName();
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

            add(tabPanel = new TabPanel(this));

            tabPanel.setPosAndSize(15, 49,100,100);

            tabPanel.addWidgets();
            tabPanel.alignWidgets();

        })));


        add(byTagTxt = new TextField(this){

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

        add(byTag = new CheckBox(this){


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



        add(tagTxt = new TextField(this){
            @Override
            public boolean isEnabled() {
                return isSell.CheckIs() && byTag.CheckIs() && id.equals("ItemType");
            }

            @Override
            public boolean shouldDraw() {
                return isSell.CheckIs() && byTag.CheckIs() && id.equals("ItemType");
            }
        }.setText(Component.translatable("sdm_shop.addtovar.Tag_txt")));
        tagTxt.setPos(2, 133 + shift);

        add(selectTag = new SimpleButton(this, Component.empty(), Icons.DOWN, ((simpleButton, mouseButton1) -> {
            if(itemStack == null) return;
            save1 = itemLim.getText();
            save2 = itemCost.getText();
            save3 = isSell.CheckIs();
            save6 = byTag.CheckIs();

            if (tagPanel != null) widgets.remove(tagPanel);
            if (tabPanel != null) widgets.remove(tabPanel);
            if (currencyPanel != null) widgets.remove(currencyPanel);

            add(tagPanel = new TagPanel(this, itemStack));

            tagPanel.setPosAndSize(15, 77, 40, 68);

            tagPanel.addWidgets();
            tagPanel.alignWidgets();

        })){
            @Override
            public boolean isEnabled() {
                return isSell.CheckIs() && byTag.CheckIs() && id.equals("ItemType");
            }

            @Override
            public boolean shouldDraw() {
                return isSell.CheckIs() && byTag.CheckIs() && id.equals("ItemType");
            }
        });

        selectTag.setPosAndSize(2, 143 + shift, 12, 12);

        add(tagSelected = new TextField(this){
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

        apply.setPosAndSize(8 , getHeight() - 15,45,12);

        cancel.setPosAndSize(60 , getHeight() - 15,45, 12);

        if(Component.translatable("sdm_shop.addtovar.tosell").getString().equals("Is sell ?")) {
            itemLimTxt.setScale(0.8f);
            checkBoxTxt.setScale(0.8f);
        } else {
            itemLimTxt.setScale(0.5f);
            checkBoxTxt.setScale(0.5f);
        }

        isSell.setPosAndSize(86 ,97 + shift, 10,10);

        error.setPosAndSize(69 + 5,35,15,30);
        error.setColor(Color4I.RED);
        error.setMaxWidth(70);

        String i = Component.translatable("sdm_shop.currency." + currencyName).getString();
        if(i.equals("sdm_shop.currency." + currencyName)) i = currencyName;

        if(currencyName != null) currencyNum.setText(i);
        if(tabName != null) tovarTab.setText(tabName);
        tagSelected.setMaxWidth(width - 10);
        if(tag != null) tagSelected.setText(tag.location().toString());
        itemName.resize(Theme.DEFAULT);

    }

}
