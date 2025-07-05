package net.sixk.sdmshop.api;

import net.minecraft.server.level.ServerPlayer;

public interface ITovarType extends DataSerializerCompound {

    void buy(ServerPlayer player);
    void sell(ServerPlayer player);
    String getID();
    boolean isSellable();
    boolean haveLimit();
    default long getLimit(){
        return 0;
    }
}
