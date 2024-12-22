package net.sixk.sdmshop.shop;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.sixk.sdmshop.shop.Tovar.AddTovarPanel;

public class SelectTypeTovarPanel extends BaseScreen {

    private SimpleButton addItem;
    private SimpleButton addXp;
    private SimpleButton addCommand;
    private String tab;

    public SelectTypeTovarPanel(String tab){

        this.tab = tab;

    }

    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth());
        setHeight(getScreen().getGuiScaledHeight());

        return true;
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_0.draw(graphics, x  + (w / 4) , y + h / 4  , w /2,h/2);
        GuiHelper.drawHollowRect(graphics, x  + (w / 4) , y + h / 4  , w /2,h/2, NordColors.POLAR_NIGHT_4, false);

    }



    @Override
    public void addWidgets() {

        add(addItem = new SimpleButton(this, Component.literal("Item"), ItemIcon.getItemIcon(Items.CRAFTING_TABLE), (simpleButton, mouseButton) -> {
            new AddTovarPanel(tab, "ItemType").openGui();
        }){
            @Override
            public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                super.drawIcon(graphics, theme, x + 3, y + 3, w - 6, h - 6);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

                NordColors.POLAR_NIGHT_1.draw(graphics, x , y , 26 , 26);
                GuiHelper.drawHollowRect(graphics, x, y, 27 , 27 , NordColors.POLAR_NIGHT_4, false);
                GuiHelper.drawHollowRect(graphics, x + 1 , y + 1, 25, 25, NordColors.POLAR_NIGHT_3, false);

            }
        });

        add(addXp = new SimpleButton(this, Component.literal("XP/LEVEL"), ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE), (simpleButton, mouseButton) -> {
            new AddTovarPanel(tab, "XPType").openGui();
        }){
            @Override
            public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                super.drawIcon(graphics, theme, x + 3 , y + 3, w - 6, h - 6);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x , int y, int w, int h) {

                NordColors.POLAR_NIGHT_1.draw(graphics, x , y , 26 , 26);
                GuiHelper.drawHollowRect(graphics, x , y, 27 , 27 , NordColors.POLAR_NIGHT_4, false);
                GuiHelper.drawHollowRect(graphics, x + 1 , y + 1, 25, 25, NordColors.POLAR_NIGHT_3, false);

            }
        });

        add(addCommand = new SimpleButton(this, Component.literal("Command"), ItemIcon.getItemIcon(Items.BARRIER), (simpleButton, mouseButton) -> {
            new AddTovarPanel(tab, "CommandType").openGui();
        }){
            @Override
            public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                super.drawIcon(graphics, theme, x + 3 , y + 3, w - 6, h - 6);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x , int y, int w, int h) {

                NordColors.POLAR_NIGHT_1.draw(graphics, x , y , 26 , 26);
                GuiHelper.drawHollowRect(graphics, x , y, 27 , 27 , NordColors.POLAR_NIGHT_4, false);
                GuiHelper.drawHollowRect(graphics, x + 1 , y + 1, 25, 25, NordColors.POLAR_NIGHT_3, false);

            }
        });

    }

    @Override
    public void alignWidgets(){

            addItem.setPosAndSize(posX  + (width / 4) + 3 , posY + height / 4 + 3,27,27);
            addXp.setPosAndSize(posX  + (width / 4) + 3 + 30 , posY + height / 4 + 3,27,27);
            addCommand.setPosAndSize(posX  + (width / 4) + 3 + 60 , posY + height / 4 + 3,27,27);

    }
}
