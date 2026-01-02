package net.sixik.sdmshop.client.screen_new.components.creator;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.client.screen_new.components.creator.category.ShopCreatorCategoryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.custom.CustomEntryConfig;
import net.sixik.sdmshop.client.screen_new.components.creator.data.SelectedCreatorEnum;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryTypesPanel;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import java.util.function.Supplier;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ShopCreatorComponentModalPanel extends ModalPanel {

    public static ShopCreatorComponentData Data;

    protected boolean center;

    protected Button entryCreator;
    protected Button categoryCreator;

    protected Button invalidateCacheButton;
    protected Button createButton;
    protected Button cancelButton;

    protected ShopCreatorEntryPanel entryContentPanel;
    protected PanelScrollBar entryContentPanelScroll;
    protected ShopCreatorCategoryPanel categoryContentPanel;
    protected PanelScrollBar categoryContentPanelScroll;

    protected ShopTab shopTab = new ShopTab(SDMShopClient.CurrentShop);
    protected ShopEntry shopEntry = new ShopEntry(SDMShopClient.CurrentShop, shopTab.getId());

    protected ShopCreatorComponentModalPanel(Panel panel) {
        super(panel);
        Data = ShopCreatorComponentData.Data;
    }

    @Override
    public void addWidgets() {
        add(entryCreator = new Button(this, Component.literal("Entry")) {

            @Override
            public void onClicked(MouseButton button) {
                Data.SelectedCreator = SelectedCreatorEnum.Entry;
                refreshWidgets();
            }

            @Override
            public boolean isSelected() {
                return Data.SelectedCreator == SelectedCreatorEnum.Entry;
            }
        });
        add(categoryCreator = new Button(this, Component.literal("Category")) {

            @Override
            public void onClicked(MouseButton button) {
                Data.SelectedCreator = SelectedCreatorEnum.Category;
                refreshWidgets();
            }

            @Override
            public boolean isSelected() {
                return Data.SelectedCreator == SelectedCreatorEnum.Category;
            }
        });

        add(createButton = new Button(this, Component.literal("Create")) {
            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public void onClicked(MouseButton button) {
                if(Data.SelectedCreator == SelectedCreatorEnum.Entry)
                    onCreateEntry();
                else onCreateCategory();
            }
        });

        add(cancelButton = new Button(this, Component.literal("Cancel")) {
            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public void onClicked(MouseButton button) {
                getGui().popModalPanel();
            }
        });

        if(Data.SelectedCreator == SelectedCreatorEnum.Entry) {
            add(entryContentPanel = new ShopCreatorEntryPanel(this));
            add(entryContentPanelScroll = new PanelScrollBar(this, entryContentPanel) {
                @Override
                public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    SDMShopClient.someColor.draw(graphics, x, y, w, h   );
                }

                @Override
                public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
                }
            });
        } else {
            add(categoryContentPanel = new ShopCreatorCategoryPanel(this));
            add(categoryContentPanelScroll = new PanelScrollBar(this, categoryContentPanel) {
                @Override
                public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    SDMShopClient.someColor.draw(graphics, x, y, w, h   );
                }

                @Override
                public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
                }
            });
        }


    }

    @Override
    public void alignWidgets() {
        final int space = 6;
        final int typeButtonsZone = this.width - space * 2;

        entryCreator.setWidth(typeButtonsZone / 2 - 3);
        categoryCreator.setWidth(entryCreator.width);

        entryCreator.setPos(space, 2);
        categoryCreator.setPos(space + typeButtonsZone - categoryCreator.width, 2);
        entryCreator.setHeight(20);
        categoryCreator.setHeight(20);

        final int fontH = Theme.DEFAULT.getFontHeight();
        final int upperSize = categoryCreator.posY + categoryCreator.height + fontH / 2;

        if (Data.SelectedCreator == SelectedCreatorEnum.Entry) {
            entryContentPanel.width = this.width;
            entryContentPanel.posY = upperSize;
            entryContentPanel.height = this.height - 24 * 2;

            entryContentPanel.clearWidgets();
            entryContentPanel.addWidgets();
            entryContentPanel.alignWidgets();

            entryContentPanelScroll.setPosAndSize(
                    entryContentPanel.getPosX() + entryContentPanel.getWidth() - 2,
                    entryContentPanel.getPosY(),
                    2,
                    entryContentPanel.getHeight()
            );
        } else {
            categoryContentPanel.width = this.width;
            categoryContentPanel.posY = upperSize;
            categoryContentPanel.height = this.height - 24 * 2;

            categoryContentPanel.clearWidgets();
            categoryContentPanel.addWidgets();
            categoryContentPanel.alignWidgets();

            categoryContentPanelScroll.setPosAndSize(
                    categoryContentPanel.getPosX() + categoryContentPanel.getWidth() - 2,
                    categoryContentPanel.getPosY(),
                    2,
                    categoryContentPanel.getHeight()
            );
        }

        cancelButton.setWidth(entryCreator.width);
        cancelButton.posX = entryCreator.posX;
        cancelButton.posY = this.height - (cancelButton.height + 4);


        createButton.setWidth(categoryCreator.width);
        createButton.posX = categoryCreator.posX;
        createButton.posY = cancelButton.posY;

    }

    public void onSelectEntryType() {
        clearWidgets();
        addWidgets();
        alignWidgets();
    }

    @Override
    public void onClosed() {

        if (MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalClose(this);
        super.onClosed();
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

        ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

        ShopRenderingWrapper.endBatch();
    }

    public void onCreateEntry() {
        final ShopEntry entry = ShopCreatorEntryPanel.shopEntry;
        if(entry == null) return;
        ShopUtilsClient.addEntry(entry.getOwnerShop(), entry);
        getGui().popModalPanel();
    }

    public void onCreateCategory() {
        final ShopTab tab = ShopCreatorCategoryPanel.shopTab;
        if(tab == null) return;
        ShopUtilsClient.addTab(tab.getOwnerShop(), tab);
        getGui().popModalPanel();
    }

    public static ShopCreatorComponentModalPanel openCentered(
            Panel panel
    ) {
        final var modal = openDefault(panel);
        modal.center = true;

        final int sw = panel.getWidth();
        final int sh = panel.getHeight();

        final int w = modal.getWidth();
        final int h = modal.getHeight();

        modal.setPos((sw - w) / 2, (sh - h) / 2);
        return modal;
    }

    public static ShopCreatorComponentModalPanel openDefault(
            Panel panel
    ) {
        final BaseScreen gui = panel.getGui();
        final ShopCreatorComponentModalPanel modal =
                new ShopCreatorComponentModalPanel(panel);
        modal.setWidth(gui.width / 2);
        modal.setHeight(gui.height);
        gui.pushModalPanel(modal);

        if (MainShopScreen.Instance != null)
            MainShopScreen.Instance.onModalOpen(modal);

        return modal;
    }

    public static abstract class Button extends SimpleTextButton {

        public Button(Panel panel, Component txt) {
            super(panel, txt, Icon.empty());
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }

        public abstract boolean isSelected();

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if (isMouseOver)
                ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_4_INT);
            else if (isSelected())
                ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_3_INT);
            else ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.pose(), x, y, w, h, 5, INPUT_BOX_INT);
        }
    }
}
