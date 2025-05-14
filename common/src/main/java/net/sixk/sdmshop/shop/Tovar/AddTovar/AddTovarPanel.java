package net.sixk.sdmshop.shop.Tovar.AddTovar;

import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import org.jetbrains.annotations.Nullable;



public class AddTovarPanel extends BaseScreen {


    public static AddProperties base;
    protected Panel typePanel  = new SelectTypeTovarPanel(this);

    public AddTovarPanel(@Nullable String tab){

       base = new AddProperties(this,tab);
       refreshWidgets();

    }

    public AddTovarPanel(Tovar tovar){

        base = new AddProperties(this, tovar);
        refreshWidgets();
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
        add(typePanel);
        add(base);
    }



    @Override
    public void alignWidgets() {

        typePanel.setPosAndSize(width / 2 - 91,height / 2 - 92,33,93);
        base.setPosAndSize(width / 2 - 57, height / 2 - 92 , 114,184);

    }
}
