package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.old_api.ShopEntryType;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.v2.color.RGB;
import net.sixik.v2.color.RGBA;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface GUIShopMenu {

    RGBA EMPTY = RGBA.create(255, 255, 255, 1);
    int EMPTY_INT = EMPTY.toInt();

    int BORDER_WIDTH = 1;
    int CORNER_SIZE = 4;

    RGBA BACKGROUND = RGBA.create(0, 0, 0, 85);
    RGBA BORDER = RGBA.create(255, 255, 255, 28);
    RGBA BORDER_2 = RGBA.create(255, 255, 255, 128);
    RGBA BORDER_3 = RGBA.create(66, 170, 255, 255);
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

    ObjectArrayList<ShopScreenEvents.OnModalOpen> getModalOpenListeners();

    ObjectArrayList<ShopScreenEvents.OnModalClose> getModalCloseListeners();

    static Map<Class<? extends AbstractEntryType>, List<AbstractEntryTypeFilter<? extends AbstractEntryType>>> createFilters() {
        final ObjectArrayList<Function<Class<? extends AbstractEntryType>, AbstractEntryTypeFilter<? extends AbstractEntryType>>> factories =
                ShopContentRegister.getFilters();

        final List<ShopEntry> entries = SDMShopClient.CurrentShop.getEntries();

        final it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<Class<? extends AbstractEntryType>> classes =
                new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>();

        for (int i = 0; i < entries.size(); i++) {
            classes.add(entries.get(i).getEntryType().getClass());
        }

        final Map<Class<? extends AbstractEntryType>, List<AbstractEntryTypeFilter<? extends AbstractEntryType>>> map =
                new Object2ObjectOpenHashMap<>(classes.size());

        for (Class<? extends AbstractEntryType> cls : classes) {
            final ObjectArrayList<AbstractEntryTypeFilter<? extends AbstractEntryType>> filtersForClass =
                    new ObjectArrayList<>();

            for (int f = 0; f < factories.size(); f++) {
                final AbstractEntryTypeFilter<? extends AbstractEntryType> filter = factories.get(f).apply(cls);
                if (filter != null) {
                    filtersForClass.add(filter);
                }
            }

            if (!filtersForClass.isEmpty()) {
                map.put(cls, filtersForClass);
            }
        }

        for (int i = 0; i < entries.size(); i++) {
            final AbstractEntryType type = entries.get(i).getEntryType();
            final Class<? extends AbstractEntryType> cls = type.getClass();

            final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filters = map.get(cls);
            if (filters == null) continue;

            for (int j = 0; j < filters.size(); j++) {
                filters.get(j).collectFromImpl(type);
            }
        }

        return map;
    }
}
