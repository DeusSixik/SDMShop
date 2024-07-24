package net.sdm.sdmshopr.data;

import net.sdm.sdmshopr.shop.limiter.ClientLimiterData;

public class ClientShopData {


    public ClientLimiterData limiterData;

    public ClientShopData(){
        limiterData = new ClientLimiterData();
    }

}
