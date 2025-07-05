package net.sixk.sdmshop.shop.Tovar.AddTovar;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.data.config.ConfigFile;

public class SelectTypeTovarPanel extends Panel {

    protected SimpleButton addItem;
    protected SimpleButton addXp;
    protected SimpleButton addCommand;


    public SelectTypeTovarPanel(Panel panel) {
        super(panel);

    }


    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_0.draw(graphics, x, y, w ,h);
        GuiHelper.drawHollowRect(graphics, x, y, w,h, NordColors.POLAR_NIGHT_4, false);

    }



    @Override
    public void addWidgets() {

        // Унифицируй это. Хотя ты и сам знаешь

        add(addItem = new SimpleButton(this, Component.literal("Item"), ItemIcon.getItemIcon(Items.CRAFTING_TABLE), (simpleButton, mouseButton) -> {
            AddProperties.id = "ItemType";
            AddProperties.shift = 0;
            AddProperties.icon = Icons.ADD;
            AddProperties.name = Component.empty();
            AddTovarPanel gui = (AddTovarPanel) getGui();
            gui.refreshWidgets();
        }){
            @Override
            public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                super.drawIcon(graphics, theme, x + 3, y + 3, w - 6, h - 6);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                if(ConfigFile.CLIENT.style) {
                    RenderHelper.drawRoundedRect(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 255 / 2));
                }else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, 26, 26);
                    GuiHelper.drawHollowRect(graphics, x, y, 27, 27, NordColors.POLAR_NIGHT_4, false);
                    GuiHelper.drawHollowRect(graphics, x + 1, y + 1, 25, 25, NordColors.POLAR_NIGHT_3, false);
                }

            }
        });

        add(addXp = new SimpleButton(this, Component.literal("XP/LEVEL"), ItemIcon.getItemIcon(Items.EXPERIENCE_BOTTLE), (simpleButton, mouseButton) -> {
            AddProperties.id = "XPType";
            AddProperties.tag = null;
            AddTovarPanel gui = (AddTovarPanel) getGui();
            gui.refreshWidgets();
        }){
            @Override
            public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                super.drawIcon(graphics, theme, x + 3 , y + 3, w - 6, h - 6);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                if(ConfigFile.CLIENT.style) {
                    RenderHelper.drawRoundedRect(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 255 / 2));
                }else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, 26, 26);
                    GuiHelper.drawHollowRect(graphics, x, y, 27, 27, NordColors.POLAR_NIGHT_4, false);
                    GuiHelper.drawHollowRect(graphics, x + 1, y + 1, 25, 25, NordColors.POLAR_NIGHT_3, false);
                }

            }
        });

        add(addCommand = new SimpleButton(this, Component.literal("Command"), ItemIcon.getItemIcon(Items.COMMAND_BLOCK), (simpleButton, mouseButton) -> {
            AddProperties.id = "CommandType";
            AddProperties.tag = null;
            AddTovarPanel gui = (AddTovarPanel) getGui();
            gui.refreshWidgets();
        }){
            @Override
            public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                super.drawIcon(graphics, theme, x + 3 , y + 3, w - 6, h - 6);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                if(ConfigFile.CLIENT.style) {
                    RenderHelper.drawRoundedRect(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 255 / 2));
                }else {
                    NordColors.POLAR_NIGHT_1.draw(graphics, x, y, 26, 26);
                    GuiHelper.drawHollowRect(graphics, x, y, 27, 27, NordColors.POLAR_NIGHT_4, false);
                    GuiHelper.drawHollowRect(graphics, x + 1, y + 1, 25, 25, NordColors.POLAR_NIGHT_3, false);
                }

            }
        });

    }

    @Override
    public void alignWidgets(){

            addItem.setPosAndSize(3, 3,27,27);
            addXp.setPosAndSize(3, 33,27,27);
            addCommand.setPosAndSize(3, 63,27,27);

    }
}
