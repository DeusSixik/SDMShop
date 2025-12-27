package net.sixik.sdmshop.client.screen.modern.create_entry;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen.base.create_entry.AbstractCreateEntryButton;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.RenderHelper;

public class ModernCreateEntryButton extends AbstractCreateEntryButton {
    public TextField field = new TextField(new BaseScreen() {
        public void addWidgets() {
        }
    });

    public ModernCreateEntryButton(Panel panel, AbstractEntryType entryType) {
        super(panel, entryType);
    }

    public void addMouseOverText(TooltipList list) {
        list.add(this.shopEntryType.getTranslatableForCreativeMenu());
        if (!this.isActive()) {
            list.add(Component.translatable("sdm.shop.entry.creator.require").append(Component.translatable(!this.shopEntryType.getModNameForContextMenu().isEmpty() ? this.shopEntryType.getModNameForContextMenu() : this.shopEntryType.getModId()).withStyle(ChatFormatting.RED)));
        }

        if (!this.shopEntryType.getDescriptionForContextMenu().isEmpty()) {
            for(Component descriptionForContextMenu : this.shopEntryType.getDescriptionForContextMenu()) {
                list.add(descriptionForContextMenu);
            }
        }

        if (this.isActive()) {
            list.add(Component.translatable("sdm.shop.entry.creator.keyinfo").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BOLD));
        }

    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0, 0, 0, 85).draw(graphics, x, y, w, h);

        this.field.setText(this.shopEntryType.getTranslatableForCreativeMenu());
        this.field.setPos(2, this.height - 20);
        this.field.setSize(this.width + 4, this.height);
        this.field.setScale(0.9F);
        this.field.setMaxWidth(this.width + 4);
        this.field.addFlags(Theme.CENTERED | Theme.CENTERED_V);
        this.field.resize(theme);
        if (!this.isActive()) {
            RenderHelper.drawHollowRect(graphics, x, y, w, h, RGBA.create(255, 0, 0, 100).withAlpha(100), false);
        } else {
            RenderHelper.drawHollowRect(graphics, x, y, w, h, RGBA.create(0, 255, 0, 100).withAlpha(100), false);
        }

        this.shopEntryType.getCreativeIcon().draw(graphics, x + (w / 2 - 8), y + 3, 16, 16);
        this.field.draw(graphics, theme, x + this.field.posX, y + this.field.posY, this.field.width, this.field.height);
    }

    public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
    }
}
