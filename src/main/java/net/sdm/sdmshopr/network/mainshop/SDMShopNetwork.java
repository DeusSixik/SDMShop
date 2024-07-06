package net.sdm.sdmshopr.network.mainshop;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.sdm.sdmshopr.SDMShopR;

public interface SDMShopNetwork {
    SimpleNetworkManager NET = SimpleNetworkManager.create(SDMShopR.MODID);

    MessageType SYNC_SHOP = NET.registerS2C("sync_shop", SyncShop::new);
    MessageType SYNC_SHOP_DATA = NET.registerS2C("sync_shop_data", SyncShopData::new);
    MessageType UPDATE_MONEY = NET.registerS2C("update_money", UpdateMoney::new);
    MessageType UPDATE_EDIT_MODE = NET.registerS2C("update_edit_mode", UpdateEditMode::new);

    MessageType EDIT_SHOP_TAB = NET.registerC2S("edit_shop_tab", EditShopTab::new);
    MessageType EDIT_SHOP_ENTRY = NET.registerC2S("edit_shop_entry", EditShopEntry::new);
    MessageType RELOAD_CLIENT = NET.registerS2C("reload_client", ReloadClientData::new);

    MessageType CREATE_SHOP_ENTRY = NET.registerC2S("create_shop_entry", CreateShopEntry::new);
    MessageType CREATE_SHOP_TAB = NET.registerC2S("create_shop_tab", CreateShopTab::new);
    MessageType MOVE_SHOP_ENTRY = NET.registerC2S("move_shop_entry", MoveShopEntry::new);
    MessageType MOVE_SHOP_TAB = NET.registerC2S("move_shop_tab", MoveShopTab::new);
    MessageType BUY_SHOP_ENTRY = NET.registerC2S("buy_shop_entry", BuyEntry::new);



    static void init(){

    }
}
