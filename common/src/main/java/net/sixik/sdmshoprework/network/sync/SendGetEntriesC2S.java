package net.sixik.sdmshoprework.network.sync;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.ShopHandler;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmshoprework.network.ShopNetwork;

import java.util.Optional;
import java.util.UUID;

public class SendGetEntriesC2S extends BaseC2SMessage {

    private final UUID tabUUID;

    public SendGetEntriesC2S(UUID tabUUID) {
        this.tabUUID = tabUUID;
    }

    public SendGetEntriesC2S(FriendlyByteBuf bug) {
        this.tabUUID = bug.readUUID();
    }

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_GET_ENTRIES;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(tabUUID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Optional<ShopTab> tabOptional = ShopHandler.getShopTab(tabUUID, false);
        if(tabOptional.isEmpty()) {
            SDMShopRework.LOGGER.warn("Shop tab not exists: " + tabUUID);
            return;
        }

        ShopTab tab = tabOptional.get();

        for (AbstractShopEntry entry : tab.getTabEntry()) {
            new SendShopEntryS2C(entry).sendTo((ServerPlayer) context.getPlayer());

        }

        SDMShopRework.LOGGER.debug("Send shopTabEntries: {}", tab.getTabEntry().size());

    }
}
