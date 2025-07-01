package net.sixik.sdmshop.client.screen.base.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;

public class AbstractShopEntrySearch extends TextBox {

    public AbstractShopEntrySearch(Panel panel) {
        super(panel);
    }

    @Override
    public void onTextChanged() {
        getShopScreen().searchField = this.getText();

    }

    @Override
    public void onEnterPressed() {
        getShopScreen().searchField = this.getText();
        getShopScreen().onRefresh();
    }

    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }
}
