package net.sixk.sdmshop.shop.Tab;


import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.network.client.UpdateTabDataC2S;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;

import java.util.Iterator;

public class TabRender extends Panel {

    public Tab tab;
    Panel panel;
    public TextField tabName;
    public SimpleTextButton b;
    public SimpleButton edit;
    public SimpleButton del;

    public TabRender(Panel panel, Tab tab) {

        super(panel);
        this.panel = panel;
        this.tab = tab;

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

                if(ConfigFile.CLIENT.style) {
                    ShapesRenderHelper.drawRoundedRect(graphics, x, y, w, h,8, RGBA.create(0,0,0,100));
                    ItemIcon.getItemIcon(tab.item).draw(graphics,x+width - 16, y + 1, 14,18);
                } else {
                    Color4I.rgb(39,50,73).draw(graphics,x, y , w, h);
                    GuiHelper.drawHollowRect(graphics, x , y , w, h, Color4I.rgb(94,106,130), false);
                    ItemIcon.getItemIcon(tab.item).draw(graphics,x+width - 18, y + 1, 16,18);
                }
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                TabPanel.selectedTab = tab.name;
                getGui().refreshWidgets();
            }



        });

        b.setSize(width,19);

        add(tabName = new TextField(this));
        if(SDMShop.isEditMode()) {
            add(edit = new SimpleButton(this, Component.literal("edit"), Icons.SETTINGS, (simpleButton, mouseButton) -> {
                new AddTabPanel(tab).openGui();
            }));
            edit.setPosAndSize(62,3,6,6);
            add(del = new SimpleButton(this, Component.literal("delete"), Icons.REMOVE, (simpleButton, mouseButton) -> {
                Iterator<Tovar> iterator = TovarList.CLIENT.tovarList.iterator();
                while (iterator.hasNext()){
                    Tovar t = iterator.next();
                    if(t.tab.equals(tab.name))  iterator.remove();
                }

                TovarTab.CLIENT.tabList.remove(TovarTab.CLIENT.tabList.indexOf(tab));
                NetworkManager.sendToServer(new UpdateTabDataC2S(TovarTab.CLIENT.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarTab.CLIENT.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                getGui().refreshWidgets();
            }));
            del.setPosAndSize(62,11,6,6);
        }

    }

    @Override
    public void alignWidgets() {
        tabName.addFlags(4);
        tabName.setPos(4,6);
        tabName.setSize(width,height);
        tabName.setMaxWidth(130);
        tabName.setText(tab.name);

        b.setPos(0,0);
        if(Theme.DEFAULT.getStringWidth(((TextFieldMixin)tabName).getRawText()) > 100) {
            tabName.setScale(0.5f);
            tabName.setPos(4,7);
        }

        tabName.resize(Theme.DEFAULT);


    }
}
