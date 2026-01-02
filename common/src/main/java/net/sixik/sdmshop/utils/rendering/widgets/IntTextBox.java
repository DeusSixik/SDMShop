package net.sixik.sdmshop.utils.rendering.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.function.Consumer;

public class IntTextBox extends TextBox {

    protected int min;
    protected int max;
    protected int value;
    protected Consumer<Integer> onValueChange;

    public IntTextBox(Panel panel, int min, int max, int value, Consumer<Integer> onValueChange) {
        super(panel);
        this.min = min;
        this.max = max;
        this.value = value;
        this.onValueChange = onValueChange;
    }

    @Override
    public boolean isValid(String txt) {
        return ShopUtils.isDigitsInRange(txt, min, max);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.add(Component.literal("From " + min + " to " + max));
    }

    @Override
    public void onTextChanged() {
        final String t = getText();
        if (ShopUtils.isDigitsInRange(t, min, max)) {
            onValueChange.accept(Integer.parseInt(t));
        }
    }
}
