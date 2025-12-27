package net.sixik.sdmshop.old_api;

public enum ShopEntryType {
    Sell,
    Buy;

    public boolean isSell() {
        return this == Sell;
    }

    public boolean isBuy() {
        return this == Buy;
    }
}
