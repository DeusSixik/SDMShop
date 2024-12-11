package net.sixk.sdmshop.shop.network;

import net.sixk.sdmshop.shop.network.client.*;
import net.sixk.sdmshop.shop.network.server.SendEditModeS2C;
import net.sixk.sdmshop.shop.network.server.SendShopDataS2C;

public class ModNetwork {


    public static void init(){

        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerC2S(UpdateServerDataC2S.TYPE,UpdateServerDataC2S.STREAM_CODEC,UpdateServerDataC2S::handle);
        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerC2S(BuyShopTovarC2S.TYPE,BuyShopTovarC2S.STREAM_CODEC,BuyShopTovarC2S::handle);
        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerS2C(SendShopDataS2C.TYPE,SendShopDataS2C.STREAM_CODEC,SendShopDataS2C::handle);
        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerC2S(UpdateTovarDataC2S.TYPE,UpdateTovarDataC2S.STREAM_CODEC,UpdateTovarDataC2S::handle);
        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerC2S(UpdateTabDataC2S.TYPE,UpdateTabDataC2S.STREAM_CODEC,UpdateTabDataC2S::handle);
        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerS2C(SendEditModeS2C.TYPE, SendEditModeS2C.STREAM_CODEC,SendEditModeS2C::handle);
        dev.ftb.mods.ftblibrary.util.NetworkHelper.registerC2S(SellShopTovarC2S.TYPE, SellShopTovarC2S.STREAM_CODEC,SellShopTovarC2S::handle);

    }

}
