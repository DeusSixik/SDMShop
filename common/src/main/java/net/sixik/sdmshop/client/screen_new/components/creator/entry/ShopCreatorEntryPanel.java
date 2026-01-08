package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public class ShopCreatorEntryPanel extends Panel {

    protected final ShopCreatorComponentModalPanel modalPanel;

    private SDMEditConfigScreen configScreen;

    protected ShopCreatorEntryTypesPanel entryTypesPanel;
    protected SimpleTextButton editButton;
    protected ShopCreatorEntrySelectedCategory entrySelectedCategoryPanel;

    public static ShopEntry shopEntry;

    public ShopCreatorEntryPanel(ShopCreatorComponentModalPanel panel) {
        super(panel);
        this.modalPanel = panel;
        shopEntry = new ShopEntry(SDMShopClient.CurrentShop);

    }

    @Override
    public void addWidgets() {
        add(entryTypesPanel = new ShopCreatorEntryTypesPanel(this));
        add(entrySelectedCategoryPanel = new ShopCreatorEntrySelectedCategory(this));
        add(editButton = new SimpleTextButton(this, Component.literal("Edit Entry"), Icons.SETTINGS) {
            @Override
            public void onClicked(MouseButton button) {
                if(shopEntry.getEntryType().getClass() != ShopCreatorComponentModalPanel.Data.Entry.selectedType.getClass())
                    shopEntry.setEntryType(ShopCreatorComponentModalPanel.Data.Entry.selectedType.copy());

                final ShopEntry entry = new ShopEntry(shopEntry.getOwnerShop());
                entry.deserialize(shopEntry.serialize());

                ConfigGroup group = new SDMConfigGroup("sdm", accept -> {

                    if(accept) {
                        shopEntry.deserialize(entry.serialize());
                    }

                    configScreen.closeGui();
                }).setNameKey("sidebar_button.sdm.shop");

                ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
                entry.getConfig(g);
                configScreen = new SDMEditConfigScreen(group);
                configScreen.openGui();
            }

            @Override
            public boolean shouldDraw() {
                return ShopCreatorEntrySelectedCategory.isTabSelected();
            }
        });

    }

    @Override
    public void alignWidgets() {
        entryTypesPanel.setWidth(this.width);
        entryTypesPanel.clearWidgets();
        entryTypesPanel.addWidgets();
        entryTypesPanel.alignWidgets();
        alignWidgetsWithoutEntryTypes();
    }

    public void alignWidgetsWithoutEntryTypes() {
        entrySelectedCategoryPanel.posX = 4;
        entrySelectedCategoryPanel.width = this.width - 8;
        entrySelectedCategoryPanel.posY = entryTypesPanel.posY + entryTypesPanel.height + 2;

        entrySelectedCategoryPanel.clearWidgets();
        entrySelectedCategoryPanel.addWidgets();
        entrySelectedCategoryPanel.alignWidgets();

        editButton.posX = 4;
        editButton.width = entrySelectedCategoryPanel.width;
        editButton.height = 20;
        editButton.posY = entrySelectedCategoryPanel.posY + entrySelectedCategoryPanel.height + 2;
    }
}
