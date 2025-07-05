package net.sixk.sdmshop.shop.Tovar.AddTovar;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TagPanel extends Panel {

    SimpleTextButton setTag;
    public ItemStack itemStack;
    static List<SimpleTextButton> tagList = new ArrayList<>();

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

        NordColors.POLAR_NIGHT_0.draw(graphics, x, y, 40, 70);
        GuiHelper.drawHollowRect(graphics, x, y, 40, 70, NordColors.POLAR_NIGHT_4, false);

    }

    public TagPanel(Panel panel, ItemStack itemStack) {
        super(panel);
        this.itemStack = itemStack;
    }

    @Override
    public void addWidgets() {

        tagList.clear();

        for (TagKey w : itemStack.getTags().toList()) {

            String i = w.location().toString();

            setTag = new SimpleTextButton(this, Component.literal(i), Icon.empty()) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    AddProperties.tag = w;
                    AddTovarPanel gui = (AddTovarPanel) getGui();
                    gui.refreshWidgets();

                }

            };

            add(setTag);
            tagList.add(setTag);

        }

        for (int n = 0; n < tagList.size(); n++) {

            SimpleTextButton w = tagList.get(n);
            w.setSize(36, 9);
            w.setPos(2, 1 + 10 * n);
        }
    }

    @Override
    public void alignWidgets() {

    }
}
