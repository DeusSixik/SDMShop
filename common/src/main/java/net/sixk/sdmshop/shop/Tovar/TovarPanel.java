package net.sixk.sdmshop.shop.Tovar;


import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.BuyingWindow;
import net.sixk.sdmshop.shop.Tovar.AddTovar.AddTovarPanel;
import net.sixk.sdmshop.shop.network.client.UpdateTovarDataC2S;

public class TovarPanel extends Panel {

    public SimpleButton openBuy;
    public SimpleButton editMod;
    public SimpleButton delete;
    public TextField buy;
    public Tovar tovar;

    public TovarPanel(Panel panel, Tovar tovar) {

        super(panel);
        this.tovar = tovar;

    }

    @Override
    public boolean scrollPanel(double scroll) {
        return false;
    }


    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_1.draw(graphics, x , y , 27, 27);
        GuiHelper.drawHollowRect(graphics, x , y , 28, 28, NordColors.POLAR_NIGHT_0, false);
        NordColors.POLAR_NIGHT_1.draw(graphics, x, y + 30 , 27, 10);
        GuiHelper.drawHollowRect(graphics, x , y + 30 , 28, 11, NordColors.POLAR_NIGHT_0, true);
        GuiHelper.drawHollowRect(graphics, x + 1 , y + 1 , 26, 26, NordColors.POLAR_NIGHT_3, false);
        drawIcon(graphics, x + 4 , y + 4,25,25);

    }

    public void drawIcon(GuiGraphics graphics, int x, int y, int w, int h) {
        tovar.abstractTovar.getIcon().draw(graphics, x, y, 20, 20);
    }


    @Override
    public void addWidgets() {
        add(buy = new TextField(this).setText(tovar.toSell ? Component.translatable("sdm_shop.tovar_panel.sell") : Component.translatable("sdm_shop.tovar_panel.buy")));
        add(openBuy = new SimpleButton(this,Component.empty(), Icon.empty(),((simpleButton, mouseButton) -> {

            new BuyingWindow(tovar).openGui();

        })){
            @Override
            public boolean shouldAddMouseOverText() {
                return false;
            }
        });

        if(SDMShop.isEditMode()) {

            add(editMod = new SimpleButton(this, Component.translatable("sdm_shop.edit"), Icons.SETTINGS, ((simpleButton, mouseButton) -> {
                new AddTovarPanel(tovar).openGui();
            })));
            editMod.setPosAndSize(3,3,6,6);
            add(delete = new SimpleButton(this, Component.translatable("sdm_shop.delete"), Icons.REMOVE, ((simpleButton, mouseButton) -> {
                TovarList.CLIENT.tovarList.remove(TovarList.CLIENT.tovarList.indexOf(tovar));
                NetworkManager.sendToServer(new UpdateTovarDataC2S(TovarList.CLIENT.serialize(Minecraft.getInstance().level.registryAccess()).asNBT()));
                getGui().refreshWidgets();
            })));
            delete.setPosAndSize(19,3,6,6);
        }
    }

    @Override
    public void alignWidgets() {
        buy.setPos(6, 32);
        if (!Component.translatable("sdm_shop.tovar_panel.buy").getString().equals("Buy")) {
            buy.setScale(0.5f);
            buy.setPos(6,33);

        }
        if(tovar.toSell & !Component.translatable("sdm_shop.tovar_panel.sell").getString().equals("Sell")) {
            buy.setPos(4,33);
        }
        openBuy.setSize(28, 41);

    }
}
