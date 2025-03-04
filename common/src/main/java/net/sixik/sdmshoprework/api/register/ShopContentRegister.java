package net.sixik.sdmshoprework.api.register;

import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.customization.AbstractShopEntryButton;
import net.sixik.sdmshoprework.api.shop.AbstractShopSellerType;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryCondition;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopContentRegister {

    public static final Map<String, IConstructor<AbstractShopEntryCondition>> SHOP_ENTRY_CONDITIONS = new HashMap<>();
    public static final LinkedHashMap<String, IConstructor<AbstractShopEntryType>> SHOP_ENTRY_TYPES = new LinkedHashMap<>();
    public static final Map<String, IConstructor<AbstractShopEntryButton>> BUTTON_STYLE = new HashMap<>();
    public static final LinkedHashMap<String, IConstructor<AbstractShopSellerType<?>>> SELLER_TYPES = new LinkedHashMap<>();

    public static AbstractShopEntryType registerType(IConstructor<AbstractShopEntryType> constructor) {
        AbstractShopEntryType icon = constructor.createDefaultInstance();
        if(!SHOP_ENTRY_TYPES.containsKey(icon.getId())) {
            SHOP_ENTRY_TYPES.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Shop Type {} already registered!", icon.getId());
        }

        return icon;
    }


    public static AbstractShopSellerType<?> registerSellerType(IConstructor<AbstractShopSellerType<?>> constructor) {
        AbstractShopSellerType<?> icon = constructor.createDefaultInstance();
        if(!SELLER_TYPES.containsKey(icon.getId())) {
            SELLER_TYPES.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Shop Condition {} already registered!", icon.getId());
        }

        return icon;
    }

    public static AbstractShopEntryCondition registerCondition(IConstructor<AbstractShopEntryCondition> constructor) {
        AbstractShopEntryCondition icon = constructor.createDefaultInstance();
        if(!SHOP_ENTRY_CONDITIONS.containsKey(icon.getId())) {
            SHOP_ENTRY_CONDITIONS.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Shop Condition {} already registered!", icon.getId());
        }

        return icon;
    }

    public static AbstractShopEntryButton registerButtonStyle(IConstructor<AbstractShopEntryButton> constructor) {
        AbstractShopEntryButton icon = constructor.createDefaultInstance();
        if(!BUTTON_STYLE.containsKey(icon.getId())) {
            BUTTON_STYLE.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Shop Button Style {} already registered!", icon.getId());
        }

        return icon;
    }

    public static void init(){

    }
}
