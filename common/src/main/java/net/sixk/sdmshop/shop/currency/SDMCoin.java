package net.sixk.sdmshop.shop.currency;

import net.sixik.sdmeconomy.economy.Currency;

public class SDMCoin extends Currency {

    public static final String ID = "sdmcoin";

    public SDMCoin() {
        super(SDMCoin.ID);
        canDelete(false);
    }
}
