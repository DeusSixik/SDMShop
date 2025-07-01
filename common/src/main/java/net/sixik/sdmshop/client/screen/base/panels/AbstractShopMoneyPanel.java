package net.sixik.sdmshop.client.screen.base.panels;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import net.sixik.sdmshop.client.screen.base.AbstractShopPanel;

public abstract class AbstractShopMoneyPanel extends AbstractShopPanel {

    public TextField moneyTitleField;
    public TextField moneyCountField;

    public AbstractShopMoneyPanel(Panel panel) {
        super(panel);
    }
}
