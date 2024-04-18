package net.sdm.sdmshopr.client.screen.createEntryScreen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.api.IEntryType;

public class CreateEntryButtonPanel extends Panel {
    public boolean isActive = true;
    public final IEntryType entryType;
    public CreateEntryButton button;

    public TextField field;
    public CreateEntryButtonPanel(Panel panel, IEntryType entryType) {
        super(panel);
        this.entryType = entryType;
    }

    @Override
    public void addWidgets() {
        if(isActive) add(button = new CreateEntryButton(this,entryType));
        add(field = new TextField(this));
    }

    @Override
    public void alignWidgets() {
        if(isActive) button.setSize(this.width, this.height);
        field.setPos(2, this.height - 20);
        field.setSize(this.width + 4, this.height);
        field.setScale(0.9f);
        field.setMaxWidth(this.width + 4);
        field.setText(entryType.getTranslatableForContextMenu());



    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.add(entryType.getTranslatableForContextMenu());

        if(!isActive){
            list.add(Component.translatable("sdmr.shop.entry.creator.require").append(Component.literal(!entryType.getModNameForContextMenu().isEmpty() ? entryType.getModNameForContextMenu() : entryType.getModID()).withStyle(ChatFormatting.RED)));
        }

        if(!entryType.getDescriptionForContextMenu().isEmpty()){
            for (Component descriptionForContextMenu : entryType.getDescriptionForContextMenu()) {
                list.add(descriptionForContextMenu);
            }
        }

        if(isActive) {
            list.add(Component.translatable("sdm.shop.entry.creator.keyinfo").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BOLD));
        }
        //        list.add(Component.translatable("sdmr.shop.entry.creator.type.canbuy", entryType.isCanBuy()));
//        list.add(Component.translatable("sdmr.shop.entry.creator.type.cansell", entryType.isSellable()));



    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        if(!isActive) {
            SDMShopRClient.shopTheme.getShadow().withAlpha(100).draw(graphics, x, y, w, h + 4);
            SDMShopRClient.shopTheme.getBackground().withAlpha(100).draw(graphics, x + 1, y + 1, w - 2, h - 2);
            GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact().withAlpha(100), false);
            GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke().withAlpha(100), false);

            entryType.getCreativeIcon().draw(graphics, x + ((w / 2) - 8), y + 3, 16, 16);
        } else {
            SDMShopRClient.shopTheme.getShadow().draw(graphics, x, y, w, h + 4);
            SDMShopRClient.shopTheme.getBackground().draw(graphics, x + 1, y + 1, w - 2, h - 2);
            GuiHelper.drawHollowRect(graphics, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
            GuiHelper.drawHollowRect(graphics, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);

            entryType.getCreativeIcon().draw(graphics, x + ((w / 2) - 8), y + 3, 16, 16);
        }
    }

    @Override
    public void drawOffsetBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {

    }
}
