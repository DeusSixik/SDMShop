package net.sixik.sdmshop.client.screen_new.components.buyer;

import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import net.sixik.sdmshop.shop.ShopEntry;

public class ShopBuyProductComponentModalPanel extends ModalPanel {

    protected final ShopEntry entry;

    public ShopBuyProductComponentModalPanel(
            final Panel panel,
            final ShopEntry entry
    ) {
        super(panel);
        this.entry = entry;
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void alignWidgets() {

    }
}
