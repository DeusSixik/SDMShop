package net.sixik.sdmshoprework.common.utils;

import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;

public class LocalizationHelper {

    public static Component getTranslationFor(AbstractShopEntryType.SellType sellType){
        switch (sellType) {
            case ONLY_BUY -> Component.literal("Buy");
            case ONLY_SELL -> Component.literal("Sell");
        }
        return Component.literal("Both");
    }
}
