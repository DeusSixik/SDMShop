package net.sixik.sdmshop.network.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.utils.ShopUtils;

public class ChangeEditModeC2S extends BaseC2SMessage {

    private final boolean value;

    public ChangeEditModeC2S(boolean value) {
        this.value = value;
    }

    public ChangeEditModeC2S(FriendlyByteBuf value) {
        this.value = value.readBoolean();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.CHANGE_EDIT_MODE_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(value);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!CurrencyHelper.isAdmin(packetContext.getPlayer())) return;

        ShopUtils.changeEditMode(packetContext.getPlayer(), value);
    }
}
