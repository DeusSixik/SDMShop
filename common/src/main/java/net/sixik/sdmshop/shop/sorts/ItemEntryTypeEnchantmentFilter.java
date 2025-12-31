package net.sixik.sdmshop.shop.sorts;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.FilterPanelWidget;
import net.sixik.sdmshop.client.screen_new.api.FilterRefreshWidget;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.entry_types.ItemEntryType;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.*;
import static net.sixik.sdmshop.client.screen_new.api.GUIShopMenu.BORDER_INT;

public class ItemEntryTypeEnchantmentFilter extends AbstractEntryTypeFilter<ItemEntryType> {

    protected final Object2ObjectOpenHashMap<Enchantment, Int2IntOpenHashMap> collected = new Object2ObjectOpenHashMap<>();
    protected final Object2ObjectOpenHashMap<Enchantment, Int2IntOpenHashMap> selected = new Object2ObjectOpenHashMap<>();

    public ItemEntryTypeEnchantmentFilter(Class<? extends AbstractEntryType> sortElementClass) {
        super(sortElementClass);
    }

    @Override
    protected boolean isSupported(ItemEntryType entryType) {
        final var item = entryType.getItemStack();
        return !item.getEnchantmentTags().isEmpty();
    }

    @Override
    protected void collectFrom(ItemEntryType entryType) {
        final ListTag tags = entryType.getItemStack().getEnchantmentTags();
        if (tags.isEmpty()) return;

        final Map<Enchantment, Integer> one = ShopUtils.deserializeEnchantments(tags);
        if (one.isEmpty()) return;

        for (Map.Entry<Enchantment, Integer> entry : one.entrySet()) {
            final Enchantment ench = entry.getKey();
            final int lvl = entry.getValue() != null ? entry.getValue() : 0;
            if (ench == null || lvl <= 0) continue;

            collected.computeIfAbsent(ench, k -> new Int2IntOpenHashMap())
                    .addTo(lvl, 1);
        }
    }

    @Override
    protected boolean sort(ShopEntry entry, ShopTab tab, ItemEntryType entryType) {
        final ItemStack itemStack = entryType.getItemStack();

        if (selected.isEmpty()) return true;

        if (!itemStack.isEnchanted()) return false;

        final Map<Enchantment, Integer> itemMap = ShopUtils.deserializeEnchantments(itemStack.getEnchantmentTags());
        if (itemMap.isEmpty()) return false;

        for (var it : itemMap.entrySet()) {
            final Enchantment ench = it.getKey();
            final int lvl = it.getValue() != null ? it.getValue() : 0;

            final var allowedLvls = selected.get(ench);
            if (allowedLvls != null) {
                if (allowedLvls.isEmpty() || allowedLvls.containsKey(lvl))
                    return true;
            }
        }

        return false;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Enchantments");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addWidget(Panel panel) {
        panel.add(new MainPanel(panel, this));
    }

    public Map<Enchantment, Int2IntOpenHashMap> getCollectedSorted() {
        final LinkedHashMap<Enchantment, Int2IntOpenHashMap> out = new LinkedHashMap<>();

        collected.keySet().stream()
                .sorted(Comparator.comparing(e -> String.valueOf(BuiltInRegistries.ENCHANTMENT.getKey(e))))
                .forEach(ench -> {
                    final Int2IntOpenHashMap levels = collected.get(ench);

                    out.put(ench, levels);
                });

        return out;
    }

    public static class MainPanel extends Panel implements FilterRefreshWidget, FilterPanelWidget {

        private final ItemEntryTypeEnchantmentFilter filter;
        protected EnchantmentListBox listBox;
        protected PanelScrollBar listBoxScroll;

        public MainPanel(Panel panel, ItemEntryTypeEnchantmentFilter filter) {
            super(panel);
            this.filter = filter;
        }

        @Override
        public void addWidgets() {
            add(listBox = new EnchantmentListBox(this, filter));
            add(listBoxScroll = new PanelScrollBar(this, listBox) {
                @Override
                public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    SDMShopClient.someColor.draw(graphics, x, y, w, h   );
                }

                @Override
                public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
                }
            });
        }

        @Override
        public void alignWidgets() {
            this.listBox.width = this.width;
            this.listBox.height = this.height - 1;

            listBoxScroll.setPosAndSize(
                    listBox.getPosX() + listBox.getWidth() - 2,
                    listBox.getPosY(),
                    2,
                    listBox.getHeight()
            );

            this.listBox.clearWidgets();
            this.listBox.addWidgets();
            this.listBox.alignWidgets();
        }

        @Override
        public int getPanelHeight(int panelH) {
            return panelH / 3;
        }

        @Override
        public void updateWidget() {
            clearWidgets();
            addWidgets();
            alignWidgets();

            listBox.updateAllSelected();
        }

        @Override
        public void setFocus(boolean value) {

        }
    }

    @Environment(EnvType.CLIENT)
    public static class EnchantmentListBox extends Panel {

        protected final ItemEntryTypeEnchantmentFilter filter;
        private Map<Enchantment, Int2IntOpenHashMap> sortEnch;


        public EnchantmentListBox(Panel panel, ItemEntryTypeEnchantmentFilter filter) {
            super(panel);
            this.filter = filter;
        }

        @Override
        public void addWidgets() {

            if (sortEnch == null || sortEnch.isEmpty()) {
                sortEnch = filter.getCollectedSorted();
            }
            if (sortEnch == null || sortEnch.isEmpty()) return;

            for (var e : sortEnch.entrySet()) {
                final Enchantment ench = e.getKey();
                final Int2IntOpenHashMap levels = e.getValue();

                // Header: имя зачара (кнопка-лейбл)
                add(new EnchHeaderButton(this, Component.translatable(ench.getDescriptionId()), ench));

                // Уровни: I, II, III... (или цифры)
                int[] ks = levels.keySet().toIntArray();
                java.util.Arrays.sort(ks);
                for (int lvl : ks) {
                    int count = levels.get(lvl);
                    add(new EnchLevelButton(this, levelComponent(lvl), ench, lvl, count));
                }
            }
        }

        private static Component levelComponent(int lvl) {
            // Вариант 1: римские, как ванилла в названии
            // return Component.translatable("enchantment.level." + lvl);

            // Вариант 2: просто цифрой (как версии на Modrinth)
            return Component.literal(String.valueOf(lvl));
        }

        @Override
        public void alignWidgets() {
            this.width -= 8;
            this.posX = 4;

            final int paddingX = 6;
            final int paddingY = 6;

            final int headerH = 12;         // высота строки заголовка
            final int chipH = 12;           // высота "чипа"
            final int headerGap = 4;        // отступ между header и чипами
            final int chipGap = 4;          // расстояние между чипами по X
            final int rowGap = 8;           // расстояние между группами (после последней строки чипов)

            final int zoneW = this.width - paddingX * 2;
            if (zoneW <= 0) return;

            int x = paddingX;
            int y = paddingY;

            final var ws = getWidgets();
            if (ws.isEmpty()) return;

            final var font = Minecraft.getInstance().font;

            boolean inGroup = false;
            boolean placedChip = false;

            for (int i = 0; i < ws.size(); i++) {
                final Widget w = ws.get(i);

                if (w instanceof EnchHeaderButton) {
                    // закрываем предыдущую группу (после последней строки чипов)
                    if (inGroup) {
                        if (placedChip) y += chipH + rowGap;
                        else y += rowGap;
                        placedChip = false;
                    }

                    // header на всю ширину
                    w.setWidth(zoneW);
                    w.setHeight(headerH);
                    w.posX = paddingX;
                    w.posY = y;

                    y += headerH + headerGap;
                    x = paddingX;

                    inGroup = true;
                    continue;
                }

                // --- chip layout ---
                placedChip = true;

                // ширину чипа считаем от текста (похоже на Modrinth)
                final String txt = w.getTitle().getString();
                int wW = font.width(txt) + 10;      // padding внутри "чипа"
                wW = Mth.clamp(wW, 18, zoneW);      // минимум/максимум

                w.setWidth(wW);
                w.setHeight(chipH);

                // перенос строки
                if (x != paddingX && (x + wW) > (paddingX + zoneW)) {
                    x = paddingX;
                    y += chipH + chipGap;
                }

                w.posX = x;
                w.posY = y;

                x += wW + chipGap;
            }

            // финальный отступ после последней группы
            if (inGroup) {
                if (placedChip) y += chipH + rowGap;
                else y += rowGap;
            }

            TextField textField = new TextField(this) {
                @Override public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) { }
            };
            textField.setHeight((int) (font.lineHeight * 1.5));
            textField.posY = widgets.get(widgets.size() - 1).posY + font.lineHeight;
            add(textField);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            ShopRenderingWrapper.beginBatch(w, h, CORNER_SIZE, BORDER_WIDTH);

            ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, BACKGROUND_INT, BORDER_INT);

            ShopRenderingWrapper.endBatch();
        }

        public void updateAllSelected() {
            final var list = getWidgets();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i) instanceof Button button)
                    button.updateSelected();
            }
        }
    }

    public static class Button extends SimpleTextButton {

        protected final EnchantmentListBox listBox;
        public boolean selected = false;

        public Button(EnchantmentListBox panel, Component txt) {
            super(panel, txt, Icon.empty());
            this.listBox = panel;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }


        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if(selected || isMouseOver) ShopRenderingWrapper.drawRoundedRect(graphics.pose(), x, y, w, h, 5, 1, INPUT_BOX_INT, BORDER_3_INT);
            else ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.pose(), x, y, w, h, 5, INPUT_BOX_INT);
        }

        @Override
        public void onClicked(MouseButton button) {

        }

        public void updateSelected() {}
    }

    public static class EnchHeaderButton extends Button {
        public final Enchantment ench;

        public EnchHeaderButton(EnchantmentListBox panel, Component txt, Enchantment ench) {
            super(panel, txt);
            this.ench = ench;
            this.height = 12;
        }

        @Override
        public void onClicked(MouseButton button) {
            final var sel = listBox.filter.selected;

            if (sel.containsKey(ench)) {
                sel.remove(ench);
            } else {
                sel.put(ench, new Int2IntOpenHashMap());
            }

            listBox.updateAllSelected();
        }

        @Override
        public void updateSelected() {
            selected = listBox.filter.selected.get(ench) != null;
        }
    }

    public static class EnchLevelButton extends Button {
        public final Enchantment ench;
        public final int level;
        public final int count;

        public EnchLevelButton(EnchantmentListBox panel, Component txt, Enchantment ench, int level, int count) {
            super(panel, txt);
            this.ench = ench;
            this.level = level;
            this.count = count;
            this.height = 12;
        }

        @Override
        public void onClicked(MouseButton button) {
            final var sel = listBox.filter.selected;

            final Int2IntOpenHashMap levels = sel.computeIfAbsent(ench, e -> new Int2IntOpenHashMap());

            try {
                if (levels.isEmpty()) {
                    levels.put(level, 1);
                    return;
                }

                if (levels.containsKey(level)) {
                    levels.remove(level);

                    if (levels.isEmpty()) {
                        sel.remove(ench);
                    }
                } else {
                    levels.put(level, 1);
                }
            } finally {
                listBox.updateAllSelected();
            }
        }

        @Override
        public void updateSelected() {
            final var levels = listBox.filter.selected.get(ench);
            if (levels == null) {
                selected = false;
                return;
            }
            if (levels.isEmpty()) {
                selected = true;
                return;
            }
            selected = levels.containsKey(level);
        }
    }
}
