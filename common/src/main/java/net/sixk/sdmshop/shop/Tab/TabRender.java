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
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixik.sdmuilibrary.client.utils.renders.ShapesRenderHelper;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.mixin.TextFieldMixin;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.modern.ModernAddTabPanel;
import net.sixk.sdmshop.shop.network.client.UpdateTabDataC2S;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;


public class TabRender extends Panel {

    public Tab tab;
    public TextField tabName;
    public SimpleTextButton b;
    public SimpleButton edit;
    public SimpleButton del;

    public TabRender(Panel panel, Tab tab) {
        super(panel);
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
                if (ConfigFile.CLIENT.style) {
                    ShapesRenderHelper.drawRoundedRect(graphics, x, y, width, h, 8, RGBA.create(0, 0, 0, 100));
                    ItemIcon.getItemIcon(tab.item).draw(graphics, x + width - 16, y + 1, 14, 18);
                } else {
                    Color4I.rgb(39, 50, 73).draw(graphics, x, y, width, h);
                    GuiHelper.drawHollowRect(graphics, x, y, width, h, Color4I.rgb(94, 106, 130), false);
                    ItemIcon.getItemIcon(tab.item).draw(graphics, x + width - 18, y + 1, 16, 18);
                }
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                TabPanel.selectedTab = tab.name;
                this.getGui().refreshWidgets();
            }


        });
        b.setSize(width, 19);

        add(tabName = new TextField(this));
        if (SDMShop.isEditMode()) {
            add(edit = new SimpleButton(this, Component.literal("edit"), Icons.SETTINGS, (simpleButton, mouseButton) -> {
               if(ConfigFile.CLIENT.style)
                (new ModernAddTabPanel(tab)).openGui();
               else
                (new AddTabPanel(tab)).openGui();
            }));
            edit.setPosAndSize(62, 3, 6, 6);
            add(del = new SimpleButton(this, Component.literal("delete"), Icons.REMOVE, (simpleButton, mouseButton) -> {

                TovarList.CLIENT.tovarList.removeIf(t -> t.tab.equals(tab.name));

                TovarTab.CLIENT.tabList.remove(tab);
                NetworkManager.sendToServer(new UpdateTabDataC2S(TovarTab.CLIENT.serializeNBT(Minecraft.getInstance().level.registryAccess())));
                NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarTab.CLIENT.serializeNBT(Minecraft.getInstance().level.registryAccess())));
                getGui().refreshWidgets();
            }));
            del.setPosAndSize(62, 11, 6, 6);
        }

    }

    public void alignWidgets() {
        tabName.addFlags(4);
        tabName.setPos(4, 6);
        tabName.setSize(width, height);
        tabName.setMaxWidth(130);
        tabName.setText(tab.name);
        b.setPos(0, 0);
        if (Theme.DEFAULT.getStringWidth(((TextFieldMixin) tabName).getRawText()) > 100) {
            tabName.setScale(0.5F);
            tabName.setPos(4, 7);
        }

        tabName.resize(Theme.DEFAULT);
    }
}