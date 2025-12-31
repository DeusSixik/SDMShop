package net.sixik.sdmshop.shop.sorts;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

import javax.swing.*;
import java.util.Objects;

public abstract class AbstractEntryTypeFilter<T extends AbstractEntryType> {

    protected final Class<? extends T> sortElementClass;
    protected boolean acceptSort = true;

    public AbstractEntryTypeFilter(Class<? extends AbstractEntryType> sortElementClass) {
        this.sortElementClass = (Class<? extends T>) sortElementClass;
    }

    public final boolean isCurrent(final AbstractEntryType entryType) {
        return isCurrent(entryType.getClass());
    }

    public final boolean isCurrent(final Class<? extends AbstractEntryType> entryTypeClass) {
        return Objects.equals(entryTypeClass, sortElementClass);
    }

    public final boolean isSupportedImpl(final AbstractEntryType entryType) {
        return isCurrent(entryType) && isSupported((T) entryType);
    }

    public final boolean sorting(
            final ShopEntry entry,
            final ShopTab tab,
            final AbstractEntryType entryType
    ) {
        return !acceptSort || sort(entry, tab, (T) entryType);
    }

    protected abstract boolean isSupported(T entryType);

    protected abstract boolean sort(final ShopEntry entry, final ShopTab tab, final T entryType);

    public final void collectFromImpl(final AbstractEntryType type) {
        collectFrom((T) type);
    }

    protected void collectFrom(final T entryType) {}

    public abstract Component getTitle();

    public void addTooltips(final TooltipList tooltipList) {}

    @Environment(EnvType.CLIENT)
    public abstract void addWidget(final Panel panel);
}
