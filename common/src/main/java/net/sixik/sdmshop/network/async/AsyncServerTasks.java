package net.sixik.sdmshop.network.async;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.Optional;

public class AsyncServerTasks {

    public static final String OPEN_SHOP = "open_shop";
    public static final String GET_OPEN_SHOP = "get_open_shop";

    public static void init() {
        AsyncBridge.registerHandler(GET_OPEN_SHOP, buf -> {
            final ResourceLocation shopId = buf.readResourceLocation();
            final Optional<BaseShop> optionalShop = SDMShopServer.Instance().getShop(shopId);
            if (optionalShop.isEmpty()) {
                SDMShop.LOGGER.error("Can't find shop with id [{}]", shopId);
                return null;
            }
            final BaseShop shop = optionalShop.get();
            final CompoundTag fullShopData = shop.serialize();

            final FriendlyByteBuf hugeData = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
            hugeData.writeBoolean(true);
            hugeData.writeUUID(shop.getId());
            hugeData.writeNbt(fullShopData);
            return hugeData;
        });

    }

    public static void openShop(ServerPlayer player, ResourceLocation shopId) {
        Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
        if (shopOpt.isEmpty()) return;

        BaseShop shop = shopOpt.get();
        CompoundTag data = shop.serialize();

        AsyncBridge.askPlayer(player, OPEN_SHOP, buf -> {
            buf.writeResourceLocation(shopId);
            buf.writeUUID(shop.getId());
            buf.writeNbt(data);
            return buf;
        });
    }


}
