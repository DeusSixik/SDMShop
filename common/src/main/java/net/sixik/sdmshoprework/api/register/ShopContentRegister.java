package net.sixik.sdmshoprework.api.register;

import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.customization.AbstractShopEntryButton;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryCondition;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryLimiter;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.api.shop.AbstractShopIcon;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopContentRegister {

    public static final Map<String, IConstructor<AbstractShopEntryCondition>> SHOP_ENTRY_CONDITIONS = new HashMap<>();
    public static final Map<String, IConstructor<AbstractShopEntryLimiter>> SHOP_ENTRY_LIMITERS = new HashMap<>();
    public static final LinkedHashMap<String, IConstructor<AbstractShopEntryType>> SHOP_ENTRY_TYPES = new LinkedHashMap<>();
    public static final Map<String, IConstructor<AbstractShopIcon>> SHOP_ICONS = new HashMap<>();
    public static final Map<String, IConstructor<AbstractShopEntryButton>> BUTTON_STYLE = new HashMap<>();

    public static AbstractShopIcon registerIcon(IConstructor<AbstractShopIcon> constructor) {
        AbstractShopIcon icon = constructor.createDefaultInstance();
        if(!SHOP_ICONS.containsKey(icon.getId())) {
            SHOP_ICONS.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Icon {} already registered!", icon.getId());
        }

        return icon;
    }

    public static AbstractShopEntryType registerType(IConstructor<AbstractShopEntryType> constructor) {
        AbstractShopEntryType icon = constructor.createDefaultInstance();
        if(!SHOP_ENTRY_TYPES.containsKey(icon.getId())) {
            SHOP_ENTRY_TYPES.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Shop Type {} already registered!", icon.getId());
        }

        return icon;
    }

    public static AbstractShopEntryLimiter registerLimiter(IConstructor<AbstractShopEntryLimiter> constructor) {
        AbstractShopEntryLimiter icon = constructor.createDefaultInstance();
        if(!SHOP_ENTRY_LIMITERS.containsKey(icon.getId())) {
            SHOP_ENTRY_LIMITERS.put(icon.getId(), constructor);
        } else {
//            SDMShopRework.LOGGER.error("Shop Limiter {} already registered!", icon.getId());
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
