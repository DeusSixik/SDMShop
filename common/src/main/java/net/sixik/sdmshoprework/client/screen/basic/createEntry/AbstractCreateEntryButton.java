package net.sixik.sdmshoprework.client.screen.basic.createEntry;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.network.server.SendChangesShopEntriesC2S;
import net.sixik.sdmshoprework.network.server.create.SendCreateShopEntryC2S;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreateEntryButton extends SimpleTextButton {

    public AbstractShopEntryType shopEntryType;

    public AbstractCreateEntryButton(Panel panel, AbstractShopEntryType entryType) {
        super(panel, Component.empty(), entryType.getCreativeIcon());
        this.shopEntryType = entryType;
    }

    public boolean isActive() {
        return Platform.isModLoaded(shopEntryType.getModId());
    }

    @Override
    public void onClicked(MouseButton mouseButton) {

        if(isActive()) {

            AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen) getGui();

            if (mouseButton.isLeft()) {
                ShopEntry create = new ShopEntry(screen.shopScreen.selectedTab);
                create.setEntryType(shopEntryType.copy());
                screen.shopScreen.selectedTab.getTabEntry().add(create);

                new SendCreateShopEntryC2S(screen.shopScreen.selectedTab.shopTabUUID, create.serializeNBT()).sendToServer();
                screen.closeGui();
                screen.shopScreen.addEntriesButtons();
            }

            //LEGACY CODE !
            if (mouseButton.isRight()) {
                List<ContextMenuItem> contextMenu = new ArrayList<>();
                if (!SDMShopClient.creator.favoriteCreator.contains(shopEntryType.getId())) {
                    contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.creator.addfavorite"), Icons.ADD, (b) -> {
                        SDMShopClient.creator.favoriteCreator.add(shopEntryType.getId());
                        SNBT.write(SDMShopPaths.getFileClient(), SDMShopClient.creator.serializeNBT());
                    }));
                } else {
                    contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.creator.removefavorite"), Icons.REMOVE, (b) -> {
                        SDMShopClient.creator.favoriteCreator.remove(shopEntryType.getId());
                        SNBT.write(SDMShopPaths.getFileClient(), SDMShopClient.creator.serializeNBT());
                    }));
                }
                screen.openContextMenu(contextMenu);
            }
        }
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }
}
