package net.sixk.sdmshop.api;

import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;

public interface ITovarTypa extends SDMSerializer<KeyData> {

    void buy(ServerPlayer player);
    void sell(ServerPlayer player);
    String getID();
    boolean isSellable();
    boolean haveLimit();
    default long getLimit(){
        return 0;
    }
}
