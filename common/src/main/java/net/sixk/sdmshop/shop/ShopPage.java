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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TabPanel;
import net.sixk.sdmshop.shop.Tab.TabRender;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.AddTovarPanel;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.Tovar.TovarPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ShopPage extends BaseScreen {

    public TextBox search;
    public Panel entryPanel;
    public Panel tabPanel;
    public SimpleButton addTovarTab;
    public SimpleButton basket;
    public SimpleButton resetTab;
    public SimpleButton addTovar;
    public List<TabRender> tabRenderList = new ArrayList<>();
    public List<TovarPanel> tovarRenderList = new ArrayList<>();
    public static String tab = "All";
    public String searchContent = "";
    public float alpha = 1;
    public boolean s = false;
    public ShopPage() {

    }

    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);
        return true;
    }
    public GuiGraphics graphics;
    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        this.graphics = graphics;
        NordColors.POLAR_NIGHT_0.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        NordColors.POLAR_NIGHT_3.draw(graphics, x + 64, y + 18, w - 1 - 64, h - 1 - 18);
        NordColors.POLAR_NIGHT_4.draw(graphics, x + 64, y + 1, 1, h - 2);
        NordColors.POLAR_NIGHT_4.draw(graphics, x + 1, y + 18, w - 2, 1);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, NordColors.POLAR_NIGHT_4, false);


    }

    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        PlayerInfo info = new PlayerInfo(Minecraft.getInstance().getGameProfile(),false);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        //blit(ResourceLocation texture, int x, int y, int width, int height, int textureX, int textureY, int textureW, int textureH, int textureSizeX, int textureSizeY)
        graphics.blit(info.getSkin().texture(), x + width - 17 , y + 2, 15, 15, 8.0f, 8, 8, 8, 64, 64);
        graphics.blit(info.getSkin().texture(), x +150, y, 126, 126, 40.0f, 8, 8, 8, 64, 64);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        if(s) {
            alpha += 0.005f;
        }else {
            alpha -= 0.005f;
        }
        if(alpha >= 1 || alpha <= 0.5f) s = !s;
    }

    @Override
    public void addWidgets() {

       add(search = new TextBox(this){
           @Override
           public void onTextChanged() {
               searchContent = search.getText();
           }

           @Override
           public void onEnterPressed() {
               refreshWidgets();
           }

       });

        search.setText(searchContent);
        search.ghostText = Component.translatable("sdm_shop.shop_page.ghost_text").getString();

        add(entryPanel = new Panel(this){

            int w1 = 0;
            int w2 = 0;

            @Override
            public void addWidgets() {

                for (int w = 0; w< TovarList.CLIENT.tovarList.size(); w++) {

                    TovarPanel tovarRender = new TovarPanel(entryPanel, TovarList.CLIENT.tovarList.get(w));

                    if( w1 == (getGui().width - 65 ) / 30) {

                        w1 = 0;
                        w2++;

                    }

                    if((Objects.equals(tab, TovarList.CLIENT.tovarList.get(w).tab) || Objects.equals(tab, "All")) && (TovarList.CLIENT.tovarList.get(w).limit != 0 || SDMShop.isEditMode())) {
                        String i = TovarList.CLIENT.tovarList.get(w).item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL).get(0).getString();
                        if(searchContent.isEmpty() || i.contains(searchContent)){
                            add(tovarRender);
                            tovarRenderList.add(tovarRender);
                            tovarRender.setPos(2 + 30 * w1, 2 + 45 * w2);
                            //System.out.println(SDMShop.isEditMode());
                            w1++;
                        }
                    }
                }

                if( w1 == (getGui().width - 65 ) / 30 ) {

                    w1 = 0;
                    w2++;

                }

                if(SDMShop.isEditMode())
                    add(addTovar = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.add_tovar"), Icons.ADD,((simpleButton, mouseButton) -> {
                        new AddTovarPanel(tab == "All" ? null : tab).openGui();
                    })));
            }

            @Override
            public void alignWidgets() {
                for (int n = 0; n < tovarRenderList.size(); n++) {
                    tovarRenderList.get(n).setSize(30, 44);
                }

                if(SDMShop.isEditMode()) {
                    addTovar.setPos(2 + 30 * w1, 7 + 45 * w2);
                    addTovar.setSize(20, 20);
                }
            }
        });


        add(basket= new SimpleButton(this,Component.empty(),Icon.empty(),((simpleButton, mouseButton) -> {

            new PlayerBasket().openGui();

        })){
            @Override
            public boolean shouldAddMouseOverText() {
                return false;
            }
        });

        add(tabPanel = new Panel(this) {
            @Override
            public void addWidgets() {

                for (int n = 0; n< TovarTab.CLIENT.tabList.size(); n++) {
                    TabRender tabRender = new TabRender(tabPanel, TovarTab.CLIENT.tabList.get(n));
                    add(tabRender);
                    tabRenderList.add(tabRender);
                    tabRender.setPos(0, 20 * n );
                }

            }

            @Override
            public void alignWidgets() {
                for (int n = 0; n < tabRenderList.size(); n++) {
                    tabRenderList.get(n).setSize(64, 20);
                }
            }
        });


        if(SDMShop.isEditMode())
            add(addTovarTab = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.add_tab"), Icons.ADD,(simpleButton, mouseButton) -> {

                new TabPanel().openGui();

            }));


        add(resetTab = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.reset_tabs"), Icons.REFRESH,(simpleButton, mouseButton) -> {

            tab = "All";
            searchContent = "";
            getGui().refreshWidgets();

        }));

    }


    @Override
    public void alignWidgets() {

        search.setPosAndSize(83,3,90,13);

        entryPanel.setPosAndSize(65, 19,width - 65, height - 19);

        if(SDMShop.isEditMode()){

            addTovarTab.setPos(1,1);

        }

        tabPanel.setPosAndSize(0,19,64,height-19);

        resetTab.setPosAndSize(66, 3,14,14);

        basket.setPosAndSize(width - 17 , 2,15,15);


    }

}

