package net.sixik.sdmshop.client.screen.modern.panels;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopPanel;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopMoneyPanel;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntrySearch;
import net.sixik.sdmshop.client.screen.modern.wallet.OpenWalletButton;
import net.sixik.sdmshop.client.screen.modern.wallet.PlayerWallet;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2f;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopPanels {

    public static class TopEntriesPanel extends AbstractShopPanel {

        public AbstractShopEntrySearch textBox;
        public SimpleTextButton infoButton;
        public SimpleTextButton settingButton;
        public TopEntriesPanel(Panel panel) {
            super(panel);
        }


        @Override
        public void addWidgets() {
            AbstractShopScreen shopScreen = getShopScreen();


            add(this.textBox = new AbstractShopEntrySearch(this) {

                @Override
                public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    RGBA.create(0, 0, 0, 255 / 2).drawRoundFill(graphics, x, y, w, h, 8);
                }
            });
            if(ShopUtils.isEditModeClient()) {
                add(infoButton = new SimpleTextButton(this, Component.empty(), Icons.INFO) {
                    @Override
                    public void onClicked(MouseButton mouseButton) {
                        getShopScreen().openInfoScreen();
                    }

                    @Override
                    public void addMouseOverText(TooltipList list) {
                        list.add(Component.translatable("sdm.shop.modern.ui.keybinding.info"));
                        list.add(Component.empty());
                        list.add(Component.translatable("sdm.shop.modern.ui.keybinding.info.entry"));
                        list.add(Component.translatable("sdm.shop.modern.ui.keybinding.info.entry.change_buy_sell"));
                        list.add(Component.translatable("sdm.shop.modern.ui.keybinding.info.entry.move"));
                    }

                    @Override
                    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {}
                });
            }

            add(settingButton = new SimpleTextButton(this, Component.empty(), Icons.SETTINGS) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    if(mouseButton.isLeft()) {

                        ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                            if (accept)
                                SDMShopClient.userData.save();
                            shopScreen.openGui();
                        }).setNameKey("sidebar_button.sdm.shop");



                        ConfigGroup g = group.getOrCreateSubgroup("shop");
                        SDMShopClient.userData.getConfig(g);
                        new SDMEditConfigScreen(group).openGui();
                    } else if(mouseButton.isRight() && ShopUtils.isEditModeClient()) {
                        ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                            if (accept)
                                ShopUtilsClient.changeParams(shopScreen.currentShop);
                            shopScreen.openGui();
                        }).setNameKey("sidebar_button.sdm.shop");


                        ConfigGroup g = group.getOrCreateSubgroup("shop");
                        shopScreen.currentShop.getParams().getConfig(g);
                        new SDMEditConfigScreen(group).openGui();
                    }
                }

                @Override
                public void addMouseOverText(TooltipList list) {
                    list.add(Component.translatable(SDMShopConstants.SETTINGS_KEY));

                    if(ShopUtils.isEditModeClient()) {
                        list.add(Component.empty());
                        list.add(Component.translatable("sdm.shop.context.edit.settings.tooltip"));
                    }
                }

                @Override
                public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {}
            });

            textBox.setText(getShopScreen().searchField);
        }

        @Override
        public void alignWidgets() {
            setProperty();
        }

        public void setProperty(){

            int h = this.height / 4;


            this.textBox.ghostText = Component.translatable("sdm.shop.modern.ui.search.ghost_text").getString();
            this.textBox.setPos(h / 2 + 6, h / 2);
            this.textBox.setSize(this.width / 2 - h, this.height - h);
            if(ShopUtils.isEditModeClient()) {
                this.infoButton.setSize(this.height - h, this.height - h);
                this.infoButton.setPos(this.width - infoButton.width - 6, h / 2);

                this.settingButton.setSize(infoButton.width, infoButton.height);
                this.settingButton.setPos(infoButton.posX - settingButton.width - 2, infoButton.posY);
            } else {
                this.settingButton.setSize(this.height - h, this.height - h);
                this.settingButton.setPos(this.width - settingButton.width - 6, h / 2);
            }
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RenderHelper.drawRoundedRectUp(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
        }
    }

    public static class TopPanel extends AbstractShopPanel {

        public TextField categoryField;

        public TopPanel(Panel panel) {
            super(panel);
        }

        @Override
        public void addWidgets() {
            add(this.categoryField = new TextField(this));
            setProperty();
        }

        @Override
        public void alignWidgets() {
            setProperty();
        }

        public void setProperty(){


            Vector2f size = TextHelper.getTextRenderSize(Component.translatable("sdm.shop.modern.ui.tab_categories").getString(), (int) this.width, 1.2f, 50);
            int d2 = Minecraft.getInstance().font.lineHeight;

            int w1 = (int) (this.width - size.x);
            int w2 = w1 / 2;
            int h1 = this.height / 2;
            h1 = h1 - d2 / 2;

            this.categoryField.setPos(w2,h1);
            this.categoryField.setSize(this.width, this.height);
            this.categoryField.setMaxWidth(this.width);

            this.categoryField.setText(Component.translatable("sdm.shop.modern.ui.tab_categories"));
            this.categoryField.setScale(size.y);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RenderHelper.drawRoundedRectUp(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
        }
    }

    public static class BottomPanel extends AbstractShopMoneyPanel {

        public BottomPanel(Panel panel) {
            super(panel);
        }

        @Override
        public void addWidgets() {
            add(this.moneyTitleField = new TextField(this) {
                @Override
                public void addMouseOverText(TooltipList list) {
                    list.add(Component.literal("Coming soon money menu..."));
                }
            });
            add(this.moneyCountField = new TextField(this) {
                @Override
                public void addMouseOverText(TooltipList list) {
                    list.add(Component.literal("Coming soon money menu..."));
                }
            });
            add(this.openWalletButton = new OpenWalletButton(this,Component.empty(), Icon.empty(),((simpleButton, mouseButton) -> {
                (new PlayerWallet()).openGui();
            })));

            setProperty();
        }

        @Override
        public void alignWidgets() {
            setProperty();
        }

        public void setProperty(){
            Component textTitle = Component.translatable("sdm.shop.modern.ui.money");

            int w = TextHelper.getTextWidth(textTitle.getString());

            int w1 = this.width - w;
            int w2 = w1 / 2;


            this.moneyTitleField.addFlags(32);
            this.moneyTitleField.setSize(this.width - 1, this.height);
            this.moneyTitleField.setMaxWidth(this.width - 2);
            this.moneyTitleField.setText(textTitle);
            this.moneyTitleField.setX(w2);
            this.moneyTitleField.setY(2);
            this.moneyTitleField.setScale(1.2f);

            String textMoney = ShopUtils.moneyToString(Minecraft.getInstance().player, SDMCoin.getId());
            w = TextHelper.getTextWidth(textMoney);
            w1 = this.width - w;
            w2 = w1 / 2;

            //this.moneyCountField.setX(w2);
            //this.moneyCountField.setText(textMoney);
            //this.moneyCountField.setY(this.height - Theme.DEFAULT.getFontHeight() - 2);

            this.openWalletButton.setSize(this.height - moneyTitleField.height - 4,this.height - moneyTitleField.height - 4);
            this.openWalletButton.setX(this.width/2 - this.openWalletButton.width/2);
            this.openWalletButton.setY(moneyTitleField.posY + Theme.DEFAULT.getFontHeight() + 1);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RenderHelper.drawRoundedRectDown(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
        }
    }
}
