package net.sdm.sdmshopr.client.screen.createEntryScreen;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.api.register.EntryTypeRegister;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.client.widgets.BackToShopButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateEntryScreen extends BaseScreen {
    public boolean showNotLoadedContent = false;
    protected static final int sizeButton = 50;
    public BlankPanel createEntryPanel;
    public PanelScrollBar scrollBar;
    public BackToShopButton backToShopButton;
    public CreateShopOnlyLoadedButton shopOnlyLoadedButton;

    public MainShopScreen screen;
    public CreateEntryScreen(MainShopScreen screen){
        this.screen = screen;
    }
    @Override
    public boolean onInit() {
        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);
        closeContextMenu();

        this.createEntryPanel = new BlankPanel(this){
            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
                GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
            }
        };
        this.scrollBar = new PanelScrollBar(this,createEntryPanel){
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                Color4I.rgb(85,35,31).draw(graphics, x + 1, y + 1, w - 2, h - 2);
                GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.rgb(148,118,87), false);
            }
        };
        this.scrollBar.setCanAlwaysScroll(true);
        this.scrollBar.setScrollStep(20.0);

        createEntryPanel.setSize(this.width - 39, this.height - 30);

        updateEntry(getList());

        return true;
    }

    @Override
    public void addWidgets() {
        add(createEntryPanel);
        add(scrollBar);
        add(shopOnlyLoadedButton = new CreateShopOnlyLoadedButton(this, Component.translatable("sdm.shop.entry.creator.info")));
        add(backToShopButton = new BackToShopButton(this, Component.translatable("sdm.shop.entry.creator.back"), Icons.BACK));


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

    @Override
    public void alignWidgets() {
        Theme theme = new Theme();
        backToShopButton.setPosAndSize(8, this.height - 24, 60, 16);
        shopOnlyLoadedButton.setPosAndSize(backToShopButton.posX + backToShopButton.width + 4, backToShopButton.posY, 20 + theme.getStringWidth(I18n.get("sdm.shop.entry.creator.info")) + 5, 16);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }


    public int getCountInArray(){
        int x1 = 0;
        int x = sizeButton;

        for (int i = 0; i < 1000; i++) {
            x1 = (x * i ) + (3 * i);
            if(x1 > this.createEntryPanel.width) return i - 1;
        }
        return 0;
    }

    public int getStartPosX(int count){
        int x = sizeButton;
        int x1 = 0;
        for (int i = 0; i < count + 1; i++) {
            x1 = (x * i ) + (3 * i);
        }

        return (this.createEntryPanel.width / 2 ) - (x1 / 2);
    }

    private List<Widget> getList(){
        List<Widget> f1 = new ArrayList<>();

        int maxInArray = getCountInArray();
        int x = getStartPosX(getCountInArray());
        int y = 2;

        int i = 0;
        for (Map.Entry<String, IEntryType> d1 : EntryTypeRegister.TYPES.entrySet()) {

            CreateEntryButtonPanel b1 = new CreateEntryButtonPanel(this.createEntryPanel, d1.getValue());
            b1.setSize(sizeButton,sizeButton);
            if(ModList.get().isLoaded(d1.getValue().getModID()) || ((showNotLoadedContent && !ModList.get().isLoaded(d1.getValue().getModID())))){
                if(i > 0){
                    if (i % maxInArray == 0) {
                        y += sizeButton + 6;
                        x = getStartPosX(getCountInArray());
                    } else {
                        x += sizeButton + 3;
                    }
                    b1.setPos(x,y);
                } else b1.setPos(x,y);

                if(!ModList.get().isLoaded(d1.getValue().getModID())){
                    b1.isActive = false;
                }

                f1.add(b1);

                i++;
            }
        }
        return f1;
    }

    public void updateEntry(){
        this.updateEntry(getList());
    }

    private void updateEntry(List<Widget> items){
        createEntryPanel.setSize(this.width - 39, this.height - 30);
        this.createEntryPanel.getWidgets().clear();
        this.createEntryPanel.addAll(items);
        this.scrollBar.setPosAndSize(this.createEntryPanel.posX + this.createEntryPanel.width + 6, this.createEntryPanel.posY - 1, 16, this.createEntryPanel.height + 2);
        this.scrollBar.setValue(0.0);
    }

}
