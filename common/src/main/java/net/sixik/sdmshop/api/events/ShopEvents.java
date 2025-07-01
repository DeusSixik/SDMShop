package net.sixik.sdmshop.api.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.api.shop.AbstractShopCondition;
import net.sixik.sdmshop.api.shop.ShopObject;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.UUID;

public class ShopEvents {

    @Deprecated
    public interface ConditionEvent {
        boolean onEvent(BaseShop shop, UUID objectId, ShopObject shopObject, AbstractShopCondition shopCondition);
    }

    @Deprecated
    public static final Event<ConditionEvent> CONDITION_EVENT =
            EventFactory.createEventResult(ConditionEvent.class);

    @Deprecated
    public static boolean triggerEvent(BaseShop shop, UUID objectId, ShopObject shopObject, AbstractShopCondition shopCondition) {
       return CONDITION_EVENT.invoker().onEvent(shop, objectId, shopObject, shopCondition);
    }
}
