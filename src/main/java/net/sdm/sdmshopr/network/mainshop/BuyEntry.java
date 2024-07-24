package net.sdm.sdmshopr.network.mainshop;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sdm.sdmshopr.data.ServerShopData;
import net.sdm.sdmshopr.network.SDMShopNetwork;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

public class BuyEntry extends BaseC2SMessage {

    public final int tab;
    public final int entry;
    public final int count;

    public BuyEntry(int tab, int entry, int count) {
        this.tab = tab;
        this.entry = entry;
        this.count = count;
    }

    public BuyEntry(FriendlyByteBuf buf){
        this.tab = buf.readInt();
        this.entry = buf.readInt();
        this.count = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.BUY_SHOP_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(tab);
        buf.writeInt(entry);
        buf.writeInt(count);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Player player = packetContext.getPlayer();

        ShopEntry<?> d1 = Shop.SERVER.shopTabs.get(tab).shopEntryList.get(entry);
        d1.execute((ServerPlayer) player, count, d1);

        ServerShopData.INSTANCE.limiterData.addSellable(d1.entryID, d1.isGlobal(), player.getUUID(), count);
        ServerShopData.INSTANCE.saveOnFile();
        ServerShopData.INSTANCE.syncDataWithClient((ServerPlayer) player);
    }
}
