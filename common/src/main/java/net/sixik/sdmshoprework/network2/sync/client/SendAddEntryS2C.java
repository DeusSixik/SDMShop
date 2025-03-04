package net.sixik.sdmshoprework.network2.sync.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.ShopHandler;
import net.sixik.sdmshoprework.common.shop.ShopEntry;
import net.sixik.sdmshoprework.common.shop.ShopTab;

import java.util.Optional;
import java.util.UUID;

public class SendAddEntryS2C extends BaseS2CMessage {

    private final UUID tabID;
    private final CompoundTag entryNBT;

    public SendAddEntryS2C(UUID tabID, CompoundTag entryNBT) {
        this.tabID = tabID;
        this.entryNBT = entryNBT;
    }

    public SendAddEntryS2C(FriendlyByteBuf buf) {
        this.tabID = buf.readUUID();
        this.entryNBT = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(tabID);
        buf.writeNbt(entryNBT);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Optional<ShopTab> shopTab = ShopHandler.getShopTab(tabID, true);
        if(shopTab.isEmpty()) {
            sendError("No ShopTab found for id " + tabID);
            return;
        }

        ShopTab tab = shopTab.get();

        Optional<ShopEntry> entryOptional = ShopEntry.create(tab, entryNBT);
        if(entryOptional.isEmpty()) return;

        ShopEntry entry = entryOptional.get();

        if(tab.getTabEntry().removeIf(s -> s.entryUUID.equals(entry.entryUUID))) {
            SDMShopRework.LOGGER.info("[CLIENT] Removed old shop entry {}", entry.entryUUID + " and created new");
        }

        tab.getTabEntry().add(entry);
    }





    private void sendError(String error, Object... args) {
        SDMShopRework.LOGGER.warn(error, args);
        SDMShopRework.LOGGER.warn("[CLIENT] Can't add shop entry: {}", entryNBT);
    }
}
