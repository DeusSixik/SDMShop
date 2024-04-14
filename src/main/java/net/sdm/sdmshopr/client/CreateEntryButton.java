package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.network.CreateShopEntry;
import net.sdm.sdmshopr.network.EditShopEntry;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.entry.TypeCreator;
import net.sdm.sdmshopr.shop.entry.type.CommandEntryType;
import net.sdm.sdmshopr.shop.entry.type.IEntryType;
import net.sdm.sdmshopr.shop.entry.type.ItemEntryType;

import java.util.ArrayList;
import java.util.List;

public class CreateEntryButton extends SimpleTextButton {

    public CreateEntryButton(Panel panel) {
        super(panel, Component.empty(), Icons.ADD);
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        MainShopScreen screen = (MainShopScreen) getGui();

        if(mouseButton.isLeft() && SDMShopR.isEditModeClient()){
            List<ContextMenuItem> contextMenu = TypeCreator.createContext(screen);



            screen.openContextMenu(contextMenu);
        }
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        Font font = Minecraft.getInstance().font;
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);


        theme.drawString(graphics,
                "Create",
                (int) (x + ((this.width / 2) - (font.getSplitter().stringWidth("Create") / 2))),
                y + (this.height - font.lineHeight)
        );
    }
}
