package net.sixik.sdmshop.api;

public enum ShopEntrySellType {
    ONLY_SELL,
    ONLY_BUY,
    BOTH;

    public boolean isSell() {
        return this == ONLY_SELL || this == BOTH;
    }

    public boolean isBuy() {
        return this == ONLY_BUY || this == BOTH;
    }

    public boolean isBoth() {
        return this == BOTH;
    }
}
