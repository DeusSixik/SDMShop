package net.sixik.sdmshop.client.screen.base.create_entry;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopUtilsClient;

import java.util.ArrayList;
import java.util.List;

public class AbstractCreateEntryButton extends SimpleTextButton {

    public final AbstractEntryType shopEntryType;

    public AbstractCreateEntryButton(Panel panel, AbstractEntryType entryType) {
        super(panel, Component.empty(), entryType.getCreativeIcon());
        this.shopEntryType = entryType;
    }

    public boolean isActive() {
        return Platform.isModLoaded(shopEntryType.getModId());
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        if(!isActive()) return;
        AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen) getGui();
        AbstractShopScreen shopScreen = screen.shopScreen;
        BaseShop shop = shopScreen.currentShop;

        if(mouseButton.isLeft()) {
            ShopEntry entry = new ShopEntry(shop, shopScreen.selectedTab);
            entry.setEntryType(shopEntryType.copy());
            ShopUtilsClient.addEntry(shop, entry);
            screen.closeGui();
        }

        if(mouseButton.isRight()) {
            List<ContextMenuItem> contextMenu = new ArrayList<>();
            if (!SDMShopClient.userData.getCreator().contains(shopEntryType.getId())) {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.FAVORITE_KEY), Icons.ADD, (b) -> {
                    SDMShopClient.userData.getCreator().add(shopEntryType.getId());
                    SDMShopClient.userData.save();
                }));
            } else {
                contextMenu.add(new ContextMenuItem(Component.translatable(SDMShopConstants.UN_FAVORITE_KEY), Icons.REMOVE, (b) -> {
                    SDMShopClient.userData.getCreator().remove(shopEntryType.getId());
                    SDMShopClient.userData.save();
                }));
            }
            screen.openContextMenu(contextMenu);
        }
    }
}
