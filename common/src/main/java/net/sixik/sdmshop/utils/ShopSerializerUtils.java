package net.sixik.sdmshop.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

import java.util.*;

public class ShopSerializerUtils {

    public static void deleteEntriesWithNonexistentTabs(final BaseShop shop) {
        final List<ShopEntry> entries = shop.getEntries();
        final Set<UUID> existingTabIds = new ObjectArraySet<>();

        for (final ShopTab tab : shop.getTabs()) {
            existingTabIds.add(tab.getId());
        }

        final Set<UUID> nonexistentTabIds = new ObjectArraySet<>();

        for (final ShopEntry entry : entries) {
            final UUID tabId = entry.getTab();
            if (!existingTabIds.contains(tabId)) {
                nonexistentTabIds.add(tabId);
            }
        }

        shop.removeEntriesUnSafe(entry -> nonexistentTabIds.contains(entry.getTab()));
    }
}
