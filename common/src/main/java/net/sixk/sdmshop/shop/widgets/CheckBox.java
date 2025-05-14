package net.sixk.sdmshop.shop.widgets;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.data.config.ConfigFile;

public class CheckBox extends SimpleTextButton {

    private boolean check = false;

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(ConfigFile.CLIENT.style) {
            RenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(0, 0, 0, 255 / 2));
        }
        else{
            NordColors.POLAR_NIGHT_0.draw(graphics, x, y, w, h);
            GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.rgb(94, 106, 130), false);
        }
        if (check) {
            Icons.ACCEPT.draw(graphics, x +1 , y + 1, w - 2, h - 2);
        } else {
            Icons.CANCEL.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        }

    }

    public CheckBox(Panel panel) {

        super(panel, Component.empty(), Icon.empty());

    }

    @Override
    public void onClicked(MouseButton mouseButton) {

        if(mouseButton.isLeft()) check = !check;
    }

    public boolean CheckIs(){

        return check;

    }

    public void setCheck(boolean check){

        this.check = check;

    }

}
