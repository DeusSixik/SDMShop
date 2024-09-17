package net.sdm.sdmshoprework.client.screen.basic.createEntry;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopScreen;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractCreateEntryScreen extends BaseScreen {

    public boolean showNotLoadedContent = false;

    public static final int sizeButton = 50;

    public AbstractCreateEntryPanel entriesPanel;
    public PanelScrollBar entriesScrollPanel;

    public AbstractShowOnlyLoadedButton shopOnlyLoadedButton;
    public AbstractBackButton backToShopButton;

    public AbstractShopScreen shopScreen;
    public AbstractCreateEntryScreen(AbstractShopScreen shopScreen){
        this.shopScreen = shopScreen;
    }

    @Override
    public ContextMenu openContextMenu(@NotNull List<ContextMenuItem> menu) {
        ContextMenu contextMenu = new ContextMenu(this, menu){
            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                NordColors.POLAR_NIGHT_3.draw(graphics, x + 1, y + 1, w - 2, h - 2);
                GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.BLACK, false);
            }
        };
        this.openContextMenu(contextMenu);
        return contextMenu;
    }

    public void setProperties() {}

    public void addEntriesButtons() {}

    public static class AbstractBackButton extends SimpleTextButton {

        public AbstractBackButton(Panel panel) {
            super(panel, Component.translatable("sdm.shop.entry.creator.back"), Icons.BACK);
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
            if(mouseButton.isLeft()){
                getGui().closeGui();
            }
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.getTheme().draw(graphics, x, y, w, h);
        }
    }

    public static class AbstractShowOnlyLoadedButton extends SimpleTextButton {

        public AbstractShowOnlyLoadedButton(Panel panel) {
            super(panel, Component.translatable("sdm.shop.entry.creator.info"), Icons.BOOK);
        }

        @Override
        public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen) getGui();
            if(screen.showNotLoadedContent){
                icon = Icons.CHECK;
            } else {
                icon = Icons.CLOSE;
            }

            super.drawIcon(graphics, theme, x, y, w, h);
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
            if(mouseButton.isLeft()){
                AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen) getGui();
                screen.showNotLoadedContent = !screen.showNotLoadedContent;
                screen.refreshWidgets();
                screen.addEntriesButtons();
            }
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.getTheme().draw(graphics, x, y, w, h);
        }
    }
}
