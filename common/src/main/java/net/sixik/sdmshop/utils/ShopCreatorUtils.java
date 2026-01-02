package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import net.sixik.sdmshop.old_api.LimiterSupport;
import net.sixik.sdmshop.utils.rendering.widgets.IntTextBox;

import java.util.function.Consumer;

public class ShopCreatorUtils {

    public static void createLimiterConfig(Panel panel, int posY,
                                           LimiterSupport support,
                                           Consumer<Integer> onValueChange
    ) {


        final TextField field = new TextField(panel);
        field.setText("Limiter Value");
        field.posY = posY;
        field.posX = (panel.width - field.width) / 2;

        final IntTextBox inputLimit = new IntTextBox(panel, 0, Integer.MAX_VALUE, 0, onValueChange);
        inputLimit.width = panel.width - 8;
        inputLimit.posX = 4;
        inputLimit.setHeight(20);
        inputLimit.posY = field.posY + field.height + 2;

        panel.add(field);
        panel.add(inputLimit);
    }

}
