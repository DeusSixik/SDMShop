package net.sixik.sdmshop.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface ShopEvents {

    Event<ShopBase.EntryBuyListener>    ENTRY_BUY_EVENT     = EventFactory.createLoop();
    Event<ShopBase.EntrySellListener>   ENTRY_SELL_EVENT    = EventFactory.createLoop();
    Event<ShopBase.ShopChangeListener>  SHOP_CHANGE_EVENT   = EventFactory.createLoop();
    Event<ShopBase.EntryAddListener>    ENTRY_ADD_EVENT     = EventFactory.createLoop();
    Event<ShopBase.EntryChangeListener> ENTRY_CHANGE_EVENT  = EventFactory.createLoop();
    Event<ShopBase.EntryRemoveListener> ENTRY_REMOVE_EVENT  = EventFactory.createLoop();
    Event<ShopBase.TabAddListener>      TAB_ADD_EVENT       = EventFactory.createLoop();
    Event<ShopBase.TabChangeListener>   TAB_CHANGE_EVENT    = EventFactory.createLoop();
    Event<ShopBase.TabRemoveListener>   TAB_REMOVE_EVENT    = EventFactory.createLoop();
}
