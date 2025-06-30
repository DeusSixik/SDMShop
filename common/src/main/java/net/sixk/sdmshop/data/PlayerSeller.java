package net.sixk.sdmshop.data;

import net.minecraft.world.entity.player.Player;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializer;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;

public class PlayerSeller implements SDMSerializer<KeyData> {

    public Player player;

    public PlayerSeller(Player player){

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
