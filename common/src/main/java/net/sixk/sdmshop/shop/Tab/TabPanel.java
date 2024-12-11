package net.sixk.sdmshop.shop.Tab;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixk.sdmshop.shop.network.client.UpdateTabDataC2S;

import static dev.ftb.mods.ftblibrary.ui.misc.NordColors.*;

public class TabPanel extends BaseScreen {

    public TextField title;
    public SimpleTextButton apply;
    public SimpleTextButton cancel;
    public TextBox name;
    public String titelTxt;

    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth());
        setHeight(getScreen().getGuiScaledHeight());

        return true;
    }

    public TabPanel() {


    }

    public TabPanel(String name) {

        titelTxt = name;

    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        POLAR_NIGHT_0.draw(graphics,w / 2 - 75 ,h / 2 - 37,150,75);
        GuiHelper.drawHollowRect(graphics,w / 2 - 76,h / 2 - 38,152,77, POLAR_NIGHT_4,true);

    }



    @Override
    public void addWidgets() {

        add(title = new TextField(this));
        add(name = new TextBox(this));

        add(apply = new SimpleTextButton(this, Component.translatable("sdm_shop.apply"), Icon.empty()) {

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                GuiHelper.drawHollowRect(graphics,x,y ,w,h, POLAR_NIGHT_4,true);
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                if(name.getText().isEmpty()) return;
                if(titelTxt == null) {
                    TovarTab.CLIENT.tabList.add(name.getText());
                } else {
                    TovarTab.CLIENT.tabList.set(TovarTab.CLIENT.tabList.indexOf(titelTxt),name.getText());
                }
                NetworkManager.sendToServer(new UpdateTabDataC2S(TovarTab.CLIENT.serialize().asNBT()));
                getGui().closeGui();
            }
        });

        add(cancel = new SimpleTextButton(this, Component.translatable("sdm_shop.cancel"), Icon.empty()) {
            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_1.draw(graphics, x, y, w, h);
                GuiHelper.drawHollowRect(graphics,x,y ,w,h, POLAR_NIGHT_4,true);
            }

            @Override
            public void onClicked(MouseButton mouseButton) {
                getGui().closeGui();
            }
        });
    }

    @Override
    public void alignWidgets() {

        apply.setPosAndSize(getWidth() / 2 - 38, getHeight() / 2 + 24,37,12);

        cancel.setPosAndSize(getWidth() / 2 + 2, getHeight() / 2 + 24,37,12);

        title.setText(Component.translatable("sdm_shop.tab_panel.new_tab"));
        if(titelTxt != null) title.setText(Component.translatable("sdm_shop.tab_panel.new_tab_name"));
        title.setPos(getWidth() / 2 - title.getWidth() / 2 + 2 ,getHeight() / 2 - 35);

        name.setPosAndSize(getWidth() / 2 - 50,getHeight() / 2 - 5,100,10);

    }
}
