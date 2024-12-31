package net.sixik.sdmshoprework.api.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;

public interface ShopEvents {

    Event<EntryBuyEvent> ENTRY_BUY = EventFactory.createLoop();
    Event<EntrySellEvent> ENTRY_SELL = EventFactory.createLoop();

    interface ShopEvent {
        void onEvent(ServerPlayer player, AbstractShopEntry entry);
    }

    interface EntryBuyEvent extends ShopEvent {}
    interface EntrySellEvent extends ShopEvent {}
}
