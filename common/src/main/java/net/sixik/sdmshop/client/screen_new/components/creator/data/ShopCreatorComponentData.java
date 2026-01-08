package net.sixik.sdmshop.client.screen_new.components.creator.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopTab;

import java.util.Map;

public class ShopCreatorComponentData {

    public static ShopCreatorComponentData Data = getData();
    private static ShopCreatorComponentData _data;

    private static ShopCreatorComponentData getData() {
        if(_data == null) {
            _data = new ShopCreatorComponentData();
            _data.setDefault();
        }

        return _data;
    }

    public SelectedCreatorEnum SelectedCreator = SelectedCreatorEnum.Entry;
    public Entry Entry;
    public Category Category;
    public Map<String, Object> CustomData = new Object2ObjectOpenHashMap<>();

    public static void loadDefault() {
        getData().setDefault();
    }

    private void setDefault() {
        SelectedCreator = SelectedCreatorEnum.Entry;
        Entry = new Entry();
        Category = new Category();
    }

    public static class Entry {

        public AbstractEntryType selectedType = null;
        public ItemStack lastSelectedItemStack = ItemStack.EMPTY;
        public ShopTab selectedTab;

        public Entry() {}
    }

    public static class Category {

        public String name = "";

        public Category() {}
    }
}
