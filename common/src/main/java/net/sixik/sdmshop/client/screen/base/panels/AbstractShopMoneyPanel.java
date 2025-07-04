package net.sixik.sdmshop.client.screen.base.panels;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import net.sixik.sdmshop.client.screen.base.AbstractShopPanel;
import net.sixik.sdmshop.client.screen.modern.wallet.OpenWalletButton;

public abstract class AbstractShopMoneyPanel extends AbstractShopPanel {

    public TextField moneyTitleField;
    public TextField moneyCountField;
    public OpenWalletButton openWalletButton;

    public AbstractShopMoneyPanel(Panel panel) {
        super(panel);
    }
}
