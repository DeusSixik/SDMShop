package net.sixk.sdmshop.shop.Tab;


import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.ShopPage;
import net.sixk.sdmshop.shop.network.client.UpdateTabDataC2S;

public class TabRender extends Panel {

    public String name;
    public TextField tabName;
    public SimpleTextButton b;
    public SimpleButton edit;
    public SimpleButton del;

    public TabRender(Panel panel, String name) {

        super(panel);
        this.name = name;


    }


    @Override
    public void addWidgets() {

        add(b = new SimpleTextButton(this, Component.empty(), Icon.empty()) {

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                Color4I.rgb(39,50,73).draw(graphics,x, y , w, h);
                GuiHelper.drawHollowRect(graphics, x , y , w, h, Color4I.rgb(94,106,130), false);

            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                ShopPage.tab = name;
                getGui().refreshWidgets();
            }



        });

        add(tabName = new TextField(this));
        if(SDMShop.isEditMode()) {
            add(edit = new SimpleButton(this, Component.literal("edit"), Icons.SETTINGS, (simpleButton, mouseButton) -> {
                new TabPanel(name).openGui();
            }));
            edit.setPosAndSize(56,3,6,6);
            add(del = new SimpleButton(this, Component.literal("delete"), Icons.REMOVE, (simpleButton, mouseButton) -> {
                TovarTab.CLIENT.tabList.remove(TovarTab.CLIENT.tabList.indexOf(name));
                NetworkManager.sendToServer(new UpdateTabDataC2S(TovarTab.CLIENT.serialize().asNBT()));
                getGui().refreshWidgets();
            }));
            del.setPosAndSize(56,11,6,6);
        }
    }

    @Override
    public void alignWidgets() {
        tabName.addFlags(4);
        tabName.setPos(4,6);
        tabName.setSize(this.width,this.height);
        tabName.setMaxWidth(115);
        tabName.setText(name);
        b.setSize(64,19);
        b.setPos(0,0);
        if(Theme.DEFAULT.getStringWidth(((TextFieldMixin)tabName).getRawText()) > 60) {
            tabName.setScale(0.5f);
            tabName.setPos(4,7);
        }

        tabName.resize(Theme.DEFAULT);


    }
}
