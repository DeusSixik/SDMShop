package net.sixik.sdmshop.currencies;

import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol;

public class SDMCoin extends BaseCurrency {

    public SDMCoin() {
        super(getId(), new CurrencySymbol("◎"));
        canDelete(false);
    }

    public SDMCoin(double defaultValue) {
        super(getId(), new CurrencySymbol("◎"), defaultValue);
        canDelete(false);
    }

    public static String getId() {
        return "sdm_coin";
    }
}
