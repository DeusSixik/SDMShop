package net.sixk.sdmshop.data;

import net.minecraft.world.entity.player.Player;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;

public class PlayerBuyer implements SDMSerializer<KeyData> {

    public Player player;

   public PlayerBuyer(Player player){

       this.player = player;

   }




    @Override
    public KeyData serialize() {
        return null;
    }

    @Override
    public void deserialize(KeyData data) {

    }
}
