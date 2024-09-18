package net.sdm.sdmshoprework.client.screen.basic.widget;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopPanel;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopScreen;

public abstract class AbstractShopEntrySearch extends TextBox {
    public AbstractShopEntrySearch(Panel panel) {
        super(panel);
    }

    @Override
    public void onTextChanged() {
        getShopScreen().searchField = this.getText();
        getShopScreen().addEntriesButtons();
    }

    @Override
    public void onEnterPressed() {
        getShopScreen().searchField = this.getText();
        getShopScreen().addEntriesButtons();
    }


    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }
}
