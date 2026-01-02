package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.components.creator.data.SelectedCreatorEnum;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

import java.util.*;
import java.util.function.Function;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.INPUT_BOX_INT;

public class ShopCreatorEntryTypesPanel extends Panel {

    public static List<AbstractEntryType> Cached = null;

    public static final ShopEntry EMPTY = new ShopEntry(null);

    private int padding = 6;
    private int spacingX = 6;
    private int spacingY = 6;

    public final ShopCreatorEntryPanel parentPanel;
    public ShopCreatorEntryTypesPanel(ShopCreatorEntryPanel panel) {
        super(panel);
        this.parentPanel = panel;
    }

    @Override
    public boolean shouldDraw() {
        return ShopCreatorComponentData.Data.SelectedCreator == SelectedCreatorEnum.Entry;
    }

    @Override
    public void addWidgets() {

        if(Cached == null) {
            Cached = new ObjectArrayList<>();

            for (Map.Entry<String, Function<ShopEntry, AbstractEntryType>> entrySet :
                    ShopContentRegister.getEntryTypes().entrySet()) {
                final AbstractEntryType entryType = entrySet.getValue().apply(EMPTY);
                if(entryType == null) continue;
                Cached.add(entryType);
            }

            Cached.sort((o1, o2) ->
                    String.CASE_INSENSITIVE_ORDER.compare(o1.getId(), o2.getId())
            );
        }

        for (int i = 0; i < Cached.size(); i++) {
            add(new Button(this, Cached.get(i)));
        }
    }

    @Override
    public void alignWidgets() {
        final int zoneW = this.width - padding * 2;
        if (zoneW <= 0) {
            setHeight(padding * 2);
            return;
        }

        final var list = widgets;
        if (list.isEmpty()) {
            setHeight(padding * 2);
            return;
        }

        int y = padding;
        boolean anyRow = false;

        final List<Widget> row = new ObjectArrayList<>(16);

        for (int i = 0; i < list.size();) {
            row.clear();

            int rowW = 0;
            int rowH = 0;

            while (i < list.size()) {
                final var w = list.get(i++);

                if (!w.isEnabled()) continue;

                int ww = Math.max(1, w.width);
                int wh = Math.max(1, w.height);

                if (ww > zoneW) {
                    ww = zoneW;
                    w.setSize(ww, wh);
                }

                final int add = row.isEmpty() ? ww : (spacingX + ww);

                if (!row.isEmpty() && (rowW + add) > zoneW) {
                    i--;
                    break;
                }

                row.add(w);
                rowW += add;
                rowH = Math.max(rowH, wh);
            }

            if (row.isEmpty()) continue;

            anyRow = true;

            final int offsetX = Math.max(0, (zoneW - rowW) / 2);
            int x = padding + offsetX;

            for (int k = 0; k < row.size(); k++) {
                final var w = row.get(k);

                final int ww = Math.min(Math.max(1, w.width), zoneW);

                w.posX = x;
                w.posY = y;

                x += ww + spacingX;
            }

            y += rowH + spacingY;
        }

        if (!anyRow) {
            setHeight(padding * 2);
            return;
        }

        y -= spacingY;
        final int newH = y + padding;

        if (this.height != newH) {
            setHeight(newH);
        }
    }

    public class Button extends SimpleTextButton {

        public final AbstractEntryType entryType;

        public Button(Panel panel, AbstractEntryType entryType) {
            super(panel, entryType.getTranslatableForCreativeMenu(), entryType.getCreativeIcon());
            this.entryType = entryType;
        }

        public boolean isSelected() {
            return Objects.equals(ShopCreatorComponentData.Data.Entry.selectedType, entryType);
        }

        @Override
        public void onClicked(MouseButton button) {
            ShopCreatorComponentData.Data.Entry.selectedType = entryType;
            parentPanel.modalPanel.onSelectEntryType();
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if(isMouseOver) ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_4_INT);
            else if(isSelected()) ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_3_INT);
            else ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.pose(), x, y, w, h, 5, INPUT_BOX_INT);
        }
    }
}
