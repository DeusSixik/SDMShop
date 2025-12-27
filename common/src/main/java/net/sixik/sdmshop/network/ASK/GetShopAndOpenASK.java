package net.sixik.sdmshop.network.ASK;

import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.old_api.network.AbstractASKRequest;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.Optional;
import java.util.UUID;

public class GetShopAndOpenASK extends AbstractASKRequest {

    private static final String SHOP_ID_KEY = "shop_id";
    private static final String SHOP_ID_KEY_RESOURCE = "shop_id_resource";

    public GetShopAndOpenASK(Void empty) {
        super(empty);
    }

    @Override
    public void onServerTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext) {
        if(ShopConfig.DISABLE_KEYBIND.get()) return;

        if(data.contains(SHOP_ID_KEY))
            new SyncAndOpenShopASK(null).startRequest((ServerPlayer) packetContext.getPlayer(), data.getUUID(SHOP_ID_KEY));
        else {
            String id = data.getString(SHOP_ID_KEY_RESOURCE);

            Optional<BaseShop> opt = SDMShopServer.Instance().getShop(SDMShopServer.parseLocation(id));

            if(opt.isEmpty()) {
                packetContext.getPlayer().sendSystemMessage(Component.literal("Can't open shop. Because not found! [" + id + "]").withStyle(ChatFormatting.RED));
            } else {
                new SyncAndOpenShopASK(null).startRequest((ServerPlayer) packetContext.getPlayer(), opt.get().getId());
            }
        }
    }

    @Override
    public void onClientTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext) {}

    public void execute(UUID shopId) {
        sendToServer(task(shopId));
    }

    public void execute(String shopId) {
        sendToServer(task(SDMShopServer.parseLocation(shopId)));
    }

    public void execute(ResourceLocation shopId) {
        sendToServer(task(shopId));
    }

    protected CompoundTag task(UUID shopId) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID(SHOP_ID_KEY, shopId);
        return nbt;
    }

    protected CompoundTag task(ResourceLocation shopId) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(SHOP_ID_KEY_RESOURCE, shopId.toString());
        return nbt;
    }

    @Override
    public String getId() {
        return SDMShopNetwork.GET_SHOP_AND_OPEN;
    }
}
