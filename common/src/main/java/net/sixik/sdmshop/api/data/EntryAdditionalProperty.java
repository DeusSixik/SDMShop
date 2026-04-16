package net.sixik.sdmshop.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class EntryAdditionalProperty {

    public static Codec<EntryAdditionalProperty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("price").forGetter(EntryAdditionalProperty::getPrice),
            Codec.LONG.fieldOf("count").forGetter(EntryAdditionalProperty::getCount),
            Codec.INT.fieldOf("limit").forGetter(EntryAdditionalProperty::getLimit)
    ).apply(instance, EntryAdditionalProperty::new));

    public EntryAdditionalProperty(double price, long count, int limit){
        this.price = price;
        this.count = count;
        this.limit = limit;
    }

    public EntryAdditionalProperty() {}

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

    public boolean isEmpty() {
        return price == 0 && count == 0 && limit == 0;
    }
}
