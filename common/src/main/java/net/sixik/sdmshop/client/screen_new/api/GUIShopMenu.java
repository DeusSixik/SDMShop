package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.sixik.v2.color.RGB;
import net.sixik.v2.color.RGBA;

public interface GUIShopMenu {

    RGBA EMPTY = RGBA.create(255,255,255,1);
    int EMPTY_INT = EMPTY.toInt();

    int BORDER_WIDTH = 1;
    int CORNER_SIZE = 4;

    RGBA BACKGROUND = RGBA.create(0, 0, 0, 85);
    RGBA BORDER = RGBA.create(255, 255, 255, 28);
    RGBA BORDER_2 = RGBA.create(255, 255, 255, 128);
    RGBA BORDER_3 = RGBA.create(66,170,255, 255);
    int BACKGROUND_INT = BACKGROUND.toInt();
    int BORDER_INT = BORDER.toInt();
    int BORDER_2_INT = BORDER_2.toInt();
    int BORDER_3_INT = BORDER_3.toInt();

    int INPUT_BOX_INT = 0xFF1F1F1F;
    int INPUT_BOX_BORDER_INT = 0xFF333333;

    /**
     * Method from {@link Widget#getParent()}
     */
    Panel getParent();

    /**
     * Method from {@link Panel#alignWidgets()}
     */
    void alignWidgets();

    /**
     * Method from {@link Panel#addWidgets()}
     */
    void addWidgets();

    /**
     * Method from {@link Panel#add(Widget)}
     */
    void add(Widget widget);

    default void add(Widget widget, int w, int h) {
        add(widget);
        widget.setSize(w, h);
    }

    default BaseScreen self() {
        return (BaseScreen) this;
    }
}
