package net.sixik.sdmshop.api.data;

public class EntryAdditionalProperty {

    protected double price;
    protected long count;
    protected int limit;

    public int getLimit() {
        return limit;
    }

    public long getCount() {
        return count;
    }

    public double getPrice() {
        return price;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
