package net.sixk.sdmshop.shop.Tab;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.shop.network.client.UpdateTabDataC2S;

import static dev.ftb.mods.ftblibrary.ui.misc.NordColors.*;

public class AddTabPanel extends BaseScreen {

    public TextField title;
    public TextField iconTxt;
    public SimpleButton addIcon;
    public SimpleTextButton apply;
    public SimpleTextButton cancel;
    public TextBox name = new TextBox(this);
    public ItemStack item;
    public Tab tab;
    public static Icon icon;

    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth());
        setHeight(getScreen().getGuiScaledHeight());

        return true;
    }

    public AddTabPanel() {
        icon = null;
    }

    public AddTabPanel(Tab tab) {
        this.tab = tab;
        icon = ItemIcon.getItemIcon(tab.item);

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        POLAR_NIGHT_0.draw(graphics,w / 2 - 75 ,h / 2 - 37,150,75);
        GuiHelper.drawHollowRect(graphics,w / 2 - 76,h / 2 - 38,152,77, POLAR_NIGHT_4,true);

        POLAR_NIGHT_1.draw(graphics,w / 2 - 70 ,h / 2 - 12,24,24);
        GuiHelper.drawHollowRect(graphics,w / 2 - 70,h / 2 - 12,24,24, POLAR_NIGHT_4,true);
        if(icon != null) icon.draw(graphics,w / 2 - 68,h/2 - 10,20,20);

    }



    @Override
    public void addWidgets() {

        add(title = new TextField(this));
        add(iconTxt = new TextField(this));
        add(name);
        add(addIcon = new SimpleButton(this, Component.translatable("sdm.shop.addTab.selectTab"), icon == null ? Icons.ADD : Icon.empty(), ((simpleButton, mouseButton) -> {

            ItemStackConfig item = new ItemStackConfig(false, false);
            AddTabPanel gui = (AddTabPanel) getGui();

            new SelectItemStackScreen(item, set -> {

                if (set) {
                    icon = ItemIcon.getItemIcon(item.getValue());
                    this.item = item.getValue();
                    gui.refreshWidgets();
                    gui.openGui();
                }


            }).openGui();
        })));



        add(apply = new SimpleTextButton(this, Component.translatable("sdm_shop.apply"), Icon.empty()) {

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                if(ConfigFile.CLIENT.style){
                    RenderHelper.drawRoundedRect(graphics,x,y,w,h,6, RGBA.create(0,0,0, 255/2));
                }else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                    GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_4, true);
                }
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                if(name.getText().isEmpty() || item == null) return;
                if(tab == null) {
                    TovarTab.CLIENT.tabList.add(new Tab(name.getText(),item));
                } else {
                    TovarTab.CLIENT.tabList.set(TovarTab.CLIENT.tabList.indexOf(tab),new Tab(name.getText(),item));
                }
                NetworkManager.sendToServer(new UpdateTabDataC2S(TovarTab.CLIENT.serializeNBT(Minecraft.getInstance().level.registryAccess())));

                getGui().closeGui();
            }
        });

        add(cancel = new SimpleTextButton(this, Component.translatable("sdm_shop.cancel"), Icon.empty()) {
            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                if(ConfigFile.CLIENT.style){
                    RenderHelper.drawRoundedRect(graphics,x,y,w,h,6, RGBA.create(0,0,0, 255/2));
                }else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                    GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_4, true);
                }
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                getGui().closeGui();
            }
        });
    }

    @Override
    public void alignWidgets() {
        addIcon.setPosAndSize(getWidth() / 2 - 70 ,getHeight() / 2 - 12, 24,24);
        apply.setPosAndSize(getWidth() / 2 - 38, getHeight() / 2 + 24,37,12);

        cancel.setPosAndSize(getWidth() / 2 + 2, getHeight() / 2 + 24,37,12);

        title.setText(Component.translatable("sdm_shop.tab_panel.new_tab"));
        iconTxt.setText(Component.translatable("sdm.shop.addTab.Icon"));
        iconTxt.setPos(getWidth() /2 - 67,getHeight() / 2 - 21);
        if(tab != null) title.setText(Component.translatable("sdm_shop.tab_panel.new_tab_name"));
        title.setPos(getWidth() / 2 - title.getWidth() / 2 + 2 ,getHeight() / 2 - 35);

        name.setPosAndSize(getWidth() / 2 - 30,getHeight() / 2 - 5,100,10);

    }
}
