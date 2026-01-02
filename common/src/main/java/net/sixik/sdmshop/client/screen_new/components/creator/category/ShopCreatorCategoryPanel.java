package net.sixik.sdmshop.client.screen_new.components.creator.category;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public class ShopCreatorCategoryPanel extends Panel {

    protected final ShopCreatorComponentModalPanel modalPanel;
    protected TextBox categoryNameBox;
    protected SimpleTextButton editCategoryButton;

    private SDMEditConfigScreen configScreen;

    public static ShopTab shopTab;

    public ShopCreatorCategoryPanel(ShopCreatorComponentModalPanel panel) {
        super(panel);
        this.modalPanel = panel;
        shopTab = new ShopTab(SDMShopClient.CurrentShop);
    }

    @Override
    public void addWidgets() {
        add(categoryNameBox = new TextBox(this) {
            @Override
            public void onTextChanged() {
                ShopCreatorComponentModalPanel.Data.Category.name = getText();
            }
        });
        categoryNameBox.ghostText = "Enter Name...";

        add(editCategoryButton = new SimpleTextButton(this, Component.literal("Edit Category"), Icons.SETTINGS) {
            @Override
            public void onClicked(MouseButton button) {
                shopTab.title = Component.literal(ShopCreatorComponentModalPanel.Data.Category.name);

                ConfigGroup group = new SDMConfigGroup("sdm", accept -> {

                    if(accept) {
                        ShopCreatorComponentModalPanel.Data.Category.name = shopTab.title.getString();
                        categoryNameBox.setText(ShopCreatorComponentModalPanel.Data.Category.name);
                    }

                    configScreen.closeGui();
                }).setNameKey("sidebar_button.sdm.shop");

                ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
                shopTab.getConfig(g);
                configScreen = new SDMEditConfigScreen(group);
                configScreen.openGui();
            }
        });
    }

    @Override
    public void alignWidgets() {
        categoryNameBox.setText(ShopCreatorComponentModalPanel.Data.Category.name);
        categoryNameBox.setHeight(20);
        categoryNameBox.setWidth(this.width - 16);
        categoryNameBox.setX(8);
        categoryNameBox.setY(2);

        editCategoryButton.setX(8);
        editCategoryButton.posY = categoryNameBox.posY + categoryNameBox.height + 2;
        editCategoryButton.setHeight(20);
    }
}
