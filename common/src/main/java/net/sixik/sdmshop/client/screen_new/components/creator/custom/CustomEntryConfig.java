package net.sixik.sdmshop.client.screen_new.components.creator.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryTypesPanel;
import net.sixik.sdmshop.shop.ShopEntry;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The Y-zone size will be calculated automatically based on the added widgets.
 */
public abstract class CustomEntryConfig {

    @Environment(EnvType.CLIENT)
    public abstract void addWidgets(final ShopCreatorEntryPanel panel, final BooleanSupplier musBeRender, final ShopCreatorComponentData data);

    @Environment(EnvType.CLIENT)
    public abstract void alignWidgets(
            final ShopCreatorEntryPanel panel,
            final ShopCreatorEntryTypesPanel entryTypesPanel,
            final ShopCreatorComponentData data
    );

    public abstract void applyCreate(final ShopCreatorComponentData data, final ShopEntry shopEntry);

}
