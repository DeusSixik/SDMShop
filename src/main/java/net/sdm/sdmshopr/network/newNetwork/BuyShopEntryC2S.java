package net.sdm.sdmshopr.network.newNetwork;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sixik.sdmcore.impl.utils.serializer.DataNetworkHelper;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerHelper;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerNetwork;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;

public class BuyShopEntryC2S extends BaseC2SMessage {

    public IData data;

    public BuyShopEntryC2S(int tab, int entry, int count){
        KeyData d1 = new KeyData();
        d1.put("tab", tab);
        d1.put("entry", entry);
        d1.put("count", count);
        this.data = d1;
    }

    public BuyShopEntryC2S(IData data){
        this.data = data;
    }

    public BuyShopEntryC2S(FriendlyByteBuf buf){
        this.data = DataNetworkHelper.readData(buf);
    }


    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        DataNetworkHelper.writeData(friendlyByteBuf, data);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {

        int tab = data.asKeyMap().getData("tab").asInt();
        int entry = data.asKeyMap().getData("entry").asInt();
        int count = data.asKeyMap().getData("count").asInt();

        Player player = packetContext.getPlayer();

        ShopEntry<?> d1 = Shop.SERVER.shopTabs.get(tab).shopEntryList.get(entry);
        d1.execute((ServerPlayer) player, count, d1);
    }
}
