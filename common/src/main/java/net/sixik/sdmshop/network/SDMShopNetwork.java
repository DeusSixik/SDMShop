package net.sixik.sdmshop.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.ASK.ASK_base.ShopDataSyncASKC2S;
import net.sixik.sdmshop.network.ASK.ASK_base.ShopDataSyncASKS2C;
import net.sixik.sdmshop.network.ASK.GetShopAndOpenASK;
import net.sixik.sdmshop.network.ASK.SyncAndOpenShopASK;
import net.sixik.sdmshop.network.ASK.SyncShopASK;
import net.sixik.sdmshop.network.economy.ShopChangeMoneyC2S;
import net.sixik.sdmshop.network.server.ChangeEditModeC2S;
import net.sixik.sdmshop.network.server.SendBuyEntryC2S;
import net.sixik.sdmshop.network.sync.*;
import net.sixik.sdmshop.network.sync.server.*;
import net.sixik.sdmshop.registers.ShopContentRegister;

public class SDMShopNetwork {

    private static final SimpleNetworkManager NET = SimpleNetworkManager.create(SDMShop.MODID);

    public static final MessageType CHANGE_EDIT_MODE_C2S = NET.registerC2S("change_edit_mode", ChangeEditModeC2S::new);

    public static final MessageType SHOP_CHANGE_MONEY = NET.registerC2S("shop_change_money", ShopChangeMoneyC2S::new);

    public static final MessageType SHOP_ADD_ENTRY_C2S = NET.registerC2S("shop_add_entry_c2s", SendAddEntryC2S::new);
    public static final MessageType SHOP_ADD_ENTRY_S2C = NET.registerS2C("shop_add_entry_s2c", SendAddEntryS2C::new);

    public static final MessageType SHOP_REMOVE_ENTRY_C2S = NET.registerC2S("shop_remove_entry_c2s", SendRemoveEntryC2S::new);
    public static final MessageType SHOP_REMOVE_ENTRY_S2C = NET.registerS2C("shop_remove_entry_s2c", SendRemoveEntryS2C::new);

    public static final MessageType SHOP_CHANGE_ENTRY_C2S = NET.registerC2S("shop_change_entry_c2s", SendChangeEntryC2S::new);
    public static final MessageType SHOP_CHANGE_ENTRY_S2C = NET.registerS2C("shop_change_entry_s2c", SendChangeEntryS2C::new);

    public static final MessageType SHOP_ADD_TAB_C2S = NET.registerC2S("shop_add_tab_c2s", SendAddTabC2S::new);
    public static final MessageType SHOP_ADD_TAB_S2C = NET.registerS2C("shop_add_tab_s2c", SendAddTabS2C::new);

    public static final MessageType SHOP_REMOVE_TAB_C2S = NET.registerC2S("shop_remove_tab_c2s", SendRemoveTabC2S::new);
    public static final MessageType SHOP_REMOVE_TAB_S2C = NET.registerS2C("shop_remove_tab_s2c", SendRemoveTabS2C::new);

    public static final MessageType SHOP_CHANGE_TAB_C2S = NET.registerC2S("shop_change_tab_c2s", SendChangeTabC2S::new);
    public static final MessageType SHOP_CHANGE_TAB_S2C = NET.registerS2C("shop_change_tab_s2c", SendChangeTabS2C::new);

    public static final MessageType SHOP_MOVE_ENTRY_C2S = NET.registerC2S("shop_move_entry_c2s", SendMoveEntryC2S::new);
    public static final MessageType SHOP_MOVE_ENTRY_S2C = NET.registerS2C("shop_move_entry_s2c", SendMoveEntryS2C::new);

    public static final MessageType SHOP_MOVE_TAB_C2S = NET.registerC2S("shop_move_tab_c2s", SendMoveTabC2S::new);
    public static final MessageType SHOP_MOVE_TAB_S2C = NET.registerS2C("shop_move_tab_s2c", SendMoveTabS2C::new);

    public static final MessageType SHOP_BUY_ENTRY = NET.registerC2S("shop_buy_entry_cs2", SendBuyEntryC2S::new);
    public static final MessageType SEND_LIMITER = NET.registerS2C("send_limiter", SendLimiterS2C::new);

    public static final MessageType CHANGE_PARAMS_C2S = NET.registerC2S("shop_change_params_c2s", SendChangeShopParamsC2S::new);
    public static final MessageType CHANGE_PARAMS_S2C = NET.registerS2C("shop_change_params_s2c", SendChangeShopParamsS2C::new);



    public static final MessageType ASK_TO_SERVER = NET.registerC2S("ask_to_server", ShopDataSyncASKC2S::new);
    public static final MessageType ASK_TO_CLIENT = NET.registerS2C("ask_to_client", ShopDataSyncASKS2C::new);

    public static final String SYNC_SHOP_REQUEST = ShopContentRegister.registerRequest("sync_shop", SyncShopASK::new);
    public static final String SYNC_SHOP_AND_OPEN_REQUEST = ShopContentRegister.registerRequest("sync_shop_and_open", SyncAndOpenShopASK::new);
    public static final String GET_SHOP_AND_OPEN = ShopContentRegister.registerRequest("get_shop_and_open", GetShopAndOpenASK::new);

    public static void init() {

    }

}
