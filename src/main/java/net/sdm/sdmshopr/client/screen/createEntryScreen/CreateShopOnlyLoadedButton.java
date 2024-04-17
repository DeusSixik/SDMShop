package net.sdm.sdmshopr.client.screen.createEntryScreen;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopRClient;

public class CreateShopOnlyLoadedButton extends SimpleTextButton {

    public CreateShopOnlyLoadedButton(Panel panel, Component txt) {
        super(panel, txt, Icons.BOOK);
    }

    @Override
    public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        CreateEntryScreen screen = (CreateEntryScreen) getGui();
        if(screen.showNotLoadedContent){
            icon = Icons.CHECK;
        } else {
            icon = Icons.CLOSE;
        }

        super.drawIcon(graphics, theme, x, y, w, h);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        if(mouseButton.isLeft()){
            CreateEntryScreen screen = (CreateEntryScreen) getGui();
            screen.showNotLoadedContent = !screen.showNotLoadedContent;
            screen.refreshWidgets();
            screen.updateEntry();
        }
    }
}
