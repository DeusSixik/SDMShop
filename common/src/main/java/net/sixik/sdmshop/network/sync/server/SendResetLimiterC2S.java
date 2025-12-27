package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.api.ShopApi;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.ShopAdminUtils;
import net.sixik.sdmshop.utils.ShopUtils;

import java.util.UUID;

public class SendResetLimiterC2S extends BaseC2SMessage {

    private final UUID objectId;
    private final ShopObjectTypes type;

    public SendResetLimiterC2S(final UUID objectId, ShopObjectTypes type) {
        this.objectId = objectId;
        this.type = type;
    }

    public SendResetLimiterC2S(FriendlyByteBuf byteBuf) {
        this.objectId = byteBuf.readUUID();
        this.type = ShopObjectTypes.values()[byteBuf.readShort()];
    }

    @Override
    public MessageType getType() {
        return SDMShopNetwork.RESET_LIMITER_C2S;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(objectId);
        friendlyByteBuf.writeShort(type.ordinal());
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        final Player player = packetContext.getPlayer();
        if(!ShopUtils.isEditMode(player)) {
            ShopAdminUtils.error(player, "Failed to clear limiter data: administrator rights required.");
            return;
        }
        ShopApi.resetAllData(objectId, type);

        ShopAdminUtils.info(player, "Limiter data for type %s with id %s cleared", type.name(), objectId);

        final ShopLimiter limiter = ShopApi.getLimiter();
        for (final ServerPlayer p : packetContext.getPlayer().getServer().getPlayerList().getPlayers()) {
            new SendLimiterS2C(limiter.serializeClient(p)).sendTo(p);
        }

    }
}
