package net.sixik.sdmshop.network.economy;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.utils.ShopUtils;

/**
 * Only the administrator can change the money from the client.
 */
public class ShopChangeMoneyC2S extends BaseC2SMessage {

    private final String moneyName;
    private final double value;

    public ShopChangeMoneyC2S(String moneyName, double value) {
        this.moneyName = moneyName;
        this.value = value;
    }

    public ShopChangeMoneyC2S(FriendlyByteBuf byteBuf) {
        this.moneyName = byteBuf.readUtf();
        this.value = byteBuf.readDouble();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.SHOP_CHANGE_MONEY;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(moneyName);
        friendlyByteBuf.writeDouble(value);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        if(!CurrencyHelper.isAdmin(packetContext.getPlayer())) return;

        ShopUtils.setMoney(packetContext.getPlayer(), moneyName, value);
    }
}
