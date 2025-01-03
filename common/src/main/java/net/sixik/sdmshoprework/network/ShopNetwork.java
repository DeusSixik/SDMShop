package net.sixik.sdmshoprework.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.network.client.SendBuyShopEntryC2S;
import net.sixik.sdmshoprework.network.client.SendEditModeS2C;
import net.sixik.sdmshoprework.network.client.SendEntryLimitS2C;
import net.sixik.sdmshoprework.network.client.SyncShopS2C;
import net.sixik.sdmshoprework.network.server.*;
import net.sixik.sdmshoprework.network.server.create.SendCreateShopEntryC2S;
import net.sixik.sdmshoprework.network.server.create.SendCreateShopTabC2S;
import net.sixik.sdmshoprework.network.server.edit.SendEditShopEntryC2S;
import net.sixik.sdmshoprework.network.server.edit.SendEditShopTabC2S;
import net.sixik.sdmshoprework.network.server.misc.SendConfigS2C;
import net.sixik.sdmshoprework.network.server.misc.SendOpenShopScreenS2C;
import net.sixik.sdmshoprework.network.server.move.SendMoveShopEntryC2S;
import net.sixik.sdmshoprework.network.server.move.SendMoveShopTabC2S;
import net.sixik.sdmshoprework.network.server.reload.SendReloadConfigS2C;
import net.sixik.sdmshoprework.network.sync.SendClearTabsS2C;
import net.sixik.sdmshoprework.network.sync.SendGetTabsC2S;
import net.sixik.sdmshoprework.network.sync.SendShopEntryS2C;
import net.sixik.sdmshoprework.network.sync.SendShopTabS2C;

public class ShopNetwork {

    private static final SimpleNetworkManager NET = SimpleNetworkManager.create(SDMShopRework.MODID);

    public static final MessageType SYNC_SHOP = NET.registerS2C("sync_shop", SyncShopS2C::new);
    public static final MessageType SEND_EDIT_MODE = NET.registerS2C("send_edit_mode", SendEditModeS2C::new);
    public static final MessageType SEND_RELOAD_CONFIG = NET.registerS2C("send_reload_config", SendReloadConfigS2C::new);
    public static final MessageType SEND_CONFIG = NET.registerS2C("send_config", SendConfigS2C::new);
    public static final MessageType SEND_OPEN_SHOP = NET.registerS2C("send_open_shop", SendOpenShopScreenS2C::new);
    public static final MessageType SEND_ENTRY_LIMIT = NET.registerS2C("send_entry_limit", SendEntryLimitS2C::new);

    public static final MessageType SEND_SHOP_TAB = NET.registerS2C("send_shop_tab", SendShopTabS2C::new);
    public static final MessageType SEND_SHOP_ENTRY = NET.registerS2C("send_shop_entry", SendShopEntryS2C::new);
    public static final MessageType SEND_CLEAR_TAB = NET.registerS2C("send_clear_tab", SendClearTabsS2C::new);
    public static final MessageType SEND_GET_TABS = NET.registerC2S("send_get_tabs", SendGetTabsC2S::new);


    public static final MessageType SEND_BUY_ENTRY = NET.registerC2S("send_buy_entry", SendBuyShopEntryC2S::new);
    public static final MessageType CREATE_SHOP_TAB = NET.registerC2S("create_shop_tab", SendCreateShopTabC2S::new);
    public static final MessageType CREATE_SHOP_ENTRY = NET.registerC2S("create_shop_entry", SendCreateShopEntryC2S::new);
    public static final MessageType SEND_CHANGES_SHOP = NET.registerC2S("send_changes_shop", SendChangesShopC2S::new);
    public static final MessageType SEND_CHANGE_ENTRIES = NET.registerC2S("send_change_entries", SendChangesShopEntriesC2S::new);
    public static final MessageType SEND_CHANGE_TAB = NET.registerC2S("senc_change_tab", SendChangeShopTabC2S::new);

    public static final MessageType SEND_EDIT_TAB = NET.registerC2S("send_edit_tab", SendEditShopTabC2S::new);
    public static final MessageType SEND_EDIT_ENTRY = NET.registerC2S("send_edit_entry", SendEditShopEntryC2S::new);

    public static final MessageType SEND_MOVE_TAB = NET.registerC2S("send_move_tab", SendMoveShopTabC2S::new);
    public static final MessageType SEND_MOVE_ENTRY = NET.registerC2S("send_move_entry", SendMoveShopEntryC2S::new);


    public static void init() {

    }
}
