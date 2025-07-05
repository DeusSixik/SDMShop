package net.sixk.sdmshop.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixk.sdmshop.api.DataSerializerCompound;

public class PlayerBuyer implements DataSerializerCompound {

    public Player player;

   public PlayerBuyer(Player player){

       this.player = player;

   }


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {

    }
}
