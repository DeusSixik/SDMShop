package net.sixk.sdmshop.shop;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.AddTabPanel;
import net.sixk.sdmshop.shop.Tab.TabPanel;
import net.sixk.sdmshop.shop.widgets.Search;


public class ShopPage extends BaseScreen {

    public TextBox search;
    public ShopEntriesPanel entryPanel = new ShopEntriesPanel(this);
    public TabPanel tabPanel = new TabPanel(this);
    public SimpleButton addTovarTab;
    public SimpleButton basket;
    public SimpleButton resetTab;
    public SimpleButton cancel;
    public float alpha = 1.0F;
    public boolean s = false;

    public ShopPage() {
    }

    public boolean onInit() {
        setWidth(getScreen().getGuiScaledWidth() * 4 / 5);
        setHeight(getScreen().getGuiScaledHeight() * 4 / 5);
        refreshWidgets();
        return true;
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        NordColors.POLAR_NIGHT_0.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        NordColors.POLAR_NIGHT_3.draw(graphics, x + 90, y + 18, w - 1 - 90, h - 1 - 18);
        NordColors.POLAR_NIGHT_4.draw(graphics, x + 90, y + 1, 1, h - 2);
        NordColors.POLAR_NIGHT_4.draw(graphics, x + 1, y + 18, w - 2, 1);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, false);
    }

    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getGameProfile(), false);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blit(info.getSkin().texture(), x + width - 17, y + 2, 15, 15, 8.0F, 8.0F, 8, 8, 64, 64);
        graphics.blit(info.getSkin().texture(), x + width - 17, y + 2, 15, 15, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        if (s) {
            alpha += 1f / Minecraft.getInstance().getFps();
        } else {
            alpha -= 1f / Minecraft.getInstance().getFps();
        }

        if (alpha >= 1.0F || alpha <= 0.5F) {
            s = !s;
        }

    }

    @Override
    public void addWidgets() {

        add(search = new Search(this));
        add(cancel = new SimpleButton(this, Component.translatable("sdm_shop.cancel"), Icons.CANCEL, (simpleButton, mouseButton) -> {
            closeGui();
        }));
        search.setText(Search.searchContent);
        search.ghostText = Component.translatable("sdm_shop.shop_page.ghost_text").getString();
        add(entryPanel);
        add(basket = new SimpleButton(this, Component.empty(), Icon.empty(), (simpleButton, mouseButton) -> {
            (new PlayerBasket()).openGui();
        }){
            public boolean shouldAddMouseOverText() {
                return false;
            }
        });
        add(tabPanel);
        System.out.println(SDMShop.isEditMode());
        if (SDMShop.isEditMode()) {
            add(addTovarTab = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.add_tab"), Icons.ADD, (simpleButton, mouseButton) -> {
                (new AddTabPanel()).openGui();
            }));
        }

        add(resetTab = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.reset_tabs"), Icons.REFRESH, (simpleButton, mouseButton) -> {
            EconomyAPI.getPlayerCurrencyClientData().getBalance("sdmcoin");
            TabPanel.selectedTab = "All";
            Search.searchContent = "";
            getGui().refreshWidgets();
        }));

    }


    @Override
    public void alignWidgets() {
        search.setPosAndSize(107, 3, 90, 13);
        cancel.setPosAndSize(200, 3, 14, 14);
        entryPanel.setPosAndSize(91, 19, width - 80, height - 19);
        if (SDMShop.isEditMode()) {
            addTovarTab.setPos(1, 1);
        }
        tabPanel.setPosAndSize(1, 19, 90, height - 19);
        resetTab.setPosAndSize(92, 3, 14, 14);
        basket.setPosAndSize(width - 17, 2, 15, 15);
    }

}

