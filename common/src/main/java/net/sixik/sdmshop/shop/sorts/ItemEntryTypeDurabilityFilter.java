package net.sixik.sdmshop.shop.sorts;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshop.client.screen_new.api.FilterRefreshWidget;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.entry_types.ItemEntryType;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.function.Consumer;

public class ItemEntryTypeDurabilityFilter extends AbstractEntryTypeFilter<ItemEntryType> {

    protected int damageFromPercent = 0;
    protected int damageToPercent = 100;

    public ItemEntryTypeDurabilityFilter(Class<? extends AbstractEntryType> sortElementClass) {
        super(sortElementClass);
    }


    @Override
    protected boolean isSupported(ItemEntryType entryType) {
        return entryType.getItemStack().isDamageableItem();
    }

    @Override
    protected boolean sort(ShopEntry entry, ShopTab tab, ItemEntryType entryType) {
        final ItemStack stack = entryType.getItemStack();

        /*
            If the item doesn't break, we consider it 100%
         */
        final int max = stack.getMaxDamage();
        if (max <= 0) return true;

        final int dmg = stack.getDamageValue();
        final int percent = (int) Math.round(((max - dmg) * 100.0) / max);

        return percent >= damageFromPercent && percent <= damageToPercent;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Damage %");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addWidget(Panel panel) {
        panel.add(new InputPanel(panel, s -> damageFromPercent = s, s -> damageToPercent = s));
    }

    @Environment(EnvType.CLIENT)
    protected class InputPanel extends Panel implements FilterRefreshWidget {

        private final Consumer<Integer> from;
        private final Consumer<Integer> to;

        protected TextBox fromBox;
        protected TextBox toBox;

        private boolean syncing;

        public InputPanel(Panel panel, Consumer<Integer> from, Consumer<Integer> to) {
            super(panel);
            this.from = from;
            this.to = to;
        }

        @Override
        public void addWidgets() {
            add(fromBox = new TextBox(this) {
                @Override
                public void onTextChanged() {
                    applyFromChanged();
                }

                @Override
                public void setText(String string, boolean triggerChange) {
                    super.setText(string, triggerChange);
                }
            });

            add(toBox = new TextBox(this) {
                @Override
                public void onTextChanged() {
                    applyToChanged();
                }
            });

            /*
                filter 0..100 (and an empty string is allowed)
             */
            fromBox.setFilter(ShopUtils.DIGITS_0_100);
            toBox.setFilter(ShopUtils.DIGITS_0_100);

            fromBox.setText(String.valueOf(ItemEntryTypeDurabilityFilter.this.damageFromPercent)); // 0
            toBox.setText(String.valueOf(ItemEntryTypeDurabilityFilter.this.damageToPercent));     // 100
        }

        private void applyFromChanged() {
            if (syncing) return;

            String txt = fromBox.getText();
            if (txt == null || txt.isEmpty()) return; // даём человеку набрать

            int newFrom = clamp0_100(parseIntSafe(txt, ItemEntryTypeDurabilityFilter.this.damageFromPercent));
            int curTo   = ItemEntryTypeDurabilityFilter.this.damageToPercent;

            /*
                if from has become greater than to, we raise to to from (so that the range remains valid)
             */
            if (newFrom > curTo) curTo = newFrom;

            syncing = true;
            try {
                ItemEntryTypeDurabilityFilter.this.damageFromPercent = newFrom;
                ItemEntryTypeDurabilityFilter.this.damageToPercent = curTo;

                /*
                    normalize the text (in case of "000" or "101", etc.)
                 */
                fromBox.setText(String.valueOf(newFrom));
                toBox.setText(String.valueOf(curTo));

                from.accept(newFrom);
                to.accept(curTo);
            } finally {
                syncing = false;
            }
        }

        private void applyToChanged() {
            if (syncing) return;

            String txt = toBox.getText();
            if (txt == null || txt.isEmpty()) return;

            int newTo   = clamp0_100(parseIntSafe(txt, ItemEntryTypeDurabilityFilter.this.damageToPercent));
            int curFrom = ItemEntryTypeDurabilityFilter.this.damageFromPercent;

            /*
                if to is smaller than from, omit from before to
             */
            if (newTo < curFrom) curFrom = newTo;

            syncing = true;
            try {
                ItemEntryTypeDurabilityFilter.this.damageFromPercent = curFrom;
                ItemEntryTypeDurabilityFilter.this.damageToPercent = newTo;

                fromBox.setText(String.valueOf(curFrom));
                toBox.setText(String.valueOf(newTo));

                from.accept(curFrom);
                to.accept(newTo);
            } finally {
                syncing = false;
            }
        }

        private static int clamp0_100(int v) {
            return v < 0 ? 0 : Math.min(v, 100);
        }

        private static int parseIntSafe(String s, int def) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return def;
            }
        }

        @Override
        public void alignWidgets() {
            final int elementW = (this.width / 2) - 2;

            fromBox.setWidth(elementW);
            fromBox.setHeight(this.height);

            toBox.posX = elementW + 2;
            toBox.setWidth(elementW);
            toBox.setHeight(this.height);
        }

        @Override
        public void updateWidget() {
            clearWidgets();
            addWidgets();
            alignWidgets();
        }

        @Override
        public void setFocus(boolean value) {
            fromBox.setFocused(value);
            toBox.setFocused(value);
        }
    }
}
