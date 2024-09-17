package net.sdm.sdmshoprework.client.screen.basic.panel;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopPanel;

public abstract class AbstractShopMoneyPanel extends AbstractShopPanel {

    public TextField moneyTitleField;
    public TextField moneyCountField;

    public AbstractShopMoneyPanel(Panel panel) {
        super(panel);
    }
}
