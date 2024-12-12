package net.sixik.sdmshoprework.client.screen.legacy.createEntry;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.client.screen.basic.createEntry.AbstractCreateEntryButton;

public class LegacyCreateEntryButton extends AbstractCreateEntryButton {

    public TextField field;

    public LegacyCreateEntryButton(Panel panel, AbstractShopEntryType entryType) {
        super(panel, entryType);

        field = new TextField(new BaseScreen() {
            @Override
            public void addWidgets() {

            }
        });
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.add(shopEntryType.getTranslatableForCreativeMenu());

        if(!isActive()){
            list.add(Component.translatable("sdmr.shop.entry.creator.require").append(Component.literal(!shopEntryType.getModNameForContextMenu().isEmpty() ? shopEntryType.getModNameForContextMenu() : shopEntryType.getModId()).withStyle(ChatFormatting.RED)));
        }

        if(!shopEntryType.getDescriptionForContextMenu().isEmpty()){
            for (Component descriptionForContextMenu : shopEntryType.getDescriptionForContextMenu()) {
                list.add(descriptionForContextMenu);
            }
        }

        if(isActive()) {
            list.add(Component.translatable("sdm.shop.entry.creator.keyinfo").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BOLD));
        }
    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {


        field.setText(shopEntryType.getTranslatableForCreativeMenu());
        field.setPos(2, this.height - 20);
        field.setSize(this.width + 4, this.height);
        field.setScale(0.9f);
        field.setMaxWidth(this.width + 4);
        field.addFlags(4);
        field.resize(theme);


        if(!isActive()) {
            SDMShopClient.getTheme().draw(graphics, x, y, w, h, 100);
            shopEntryType.getCreativeIcon().draw(graphics, x + ((w / 2) - 8), y + 3, 16, 16);
        } else {
            SDMShopClient.getTheme().draw(graphics, x, y, w, h);
            shopEntryType.getCreativeIcon().draw(graphics, x + ((w / 2) - 8), y + 3, 16, 16);
        }

        field.draw(graphics, theme, x + field.posX, y + field.posY, field.width, field.height);
    }

    @Override
    public void drawIcon(PoseStack graphics, Theme theme, int x, int y, int w, int h) {

    }
}
