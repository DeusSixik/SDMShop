package net.sixik.sdmshop.network.ASK;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.old_api.network.AbstractASKRequest;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeShopParamsS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopNBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyncAndOpenShopASK extends AbstractASKRequest {

    private static final String STAGE_KEY = "current_stage";
    private static final String SYNC_KEY = "sync_size";
    private static final String SHOP_ID_KEY = "shop_id";
    private static final String DATA_KEY = "packet_data";

    public static final int STAGES = 3;
    public static final int CLEAR_STAGE = 1;
    public static final int SEND_DATA_STAGE = 2;
    public static final int OPEN_STAGE = 3;

    public SyncAndOpenShopASK(Void empty) {
        super(empty);
    }

    @Override
    @Environment(EnvType.SERVER)
    public void onServerTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext) {
        if(data == null) {
            logError("Server Request: NBT Data is null!");
            return;
        }

        if (!data.contains(STAGE_KEY)) {
            logError("Missing stage key in server request");
            return;
        }

        if (!(packetContext.getPlayer() instanceof ServerPlayer player)) {
            logError("Invalid player in server request");
            return;
        }

        int stage = data.getInt(STAGE_KEY);

        UUID shopId = data.getUUID(SHOP_ID_KEY);
        CompoundTag responseData = new CompoundTag();

        switch (stage) {
            case CLEAR_STAGE -> handleClearStage(data, shopId, player, responseData);
            case SEND_DATA_STAGE -> handleSendDataStage(data, player, responseData);
            case OPEN_STAGE -> {}
            default -> logError("Unknown stage: " + stage);
        }
    }

    @Environment(EnvType.SERVER)
    private void handleClearStage(CompoundTag data, UUID shopId, ServerPlayer player, CompoundTag responseData) {
        Optional<BaseShop> shop = SDMShopServer.Instance().getShop(shopId);
        if (shop.isEmpty()) {
            logError("Shop not found for ID: " + shopId);
            return;
        }

        List<CompoundTag> shopData = shop.get().splitToNetworkPackages();
        List<CompoundTag> packetsToSend = new ArrayList<>();

        responseData.merge(data);
        responseData.putInt(STAGE_KEY, SEND_DATA_STAGE);

        int remainingPackets = shopData.size();
        for (CompoundTag shopDatum : shopData) {
            CompoundTag packetData = new CompoundTag();
            packetData.merge(responseData);
            packetData.putInt(SYNC_KEY, --remainingPackets);
            packetData.put(DATA_KEY, shopDatum);
            packetsToSend.add(packetData);
        }

        if(packetsToSend.isEmpty()) {
            CompoundTag packetData = new CompoundTag();
            packetData.merge(responseData);
            packetData.putInt(SYNC_KEY, 0);
            packetsToSend.add(packetData);
        }

        sendTo(player, packetsToSend.toArray(new CompoundTag[0]));
    }

    @Environment(EnvType.SERVER)
    private void handleSendDataStage(CompoundTag data, ServerPlayer player, CompoundTag responseData) {
        if (!data.contains(SYNC_KEY) || data.getInt(SYNC_KEY) <= 0) {
            responseData.putInt(STAGE_KEY, OPEN_STAGE);
            sendTo(player, responseData);

            SDMShopServer.InstanceOptional().ifPresent(sdmShopServer -> {
                sdmShopServer.getShop(data.getUUID(SHOP_ID_KEY)).ifPresent(shop -> {
                    new SendChangeShopParamsS2C(shop).sendTo(player);
                });
            });
        }
    }

    /////////////////////////////////
    //////     CLIENT    ////////////
    /////////////////////////////////

    @Override
    @Environment(EnvType.CLIENT)
    public void onClientTakeRequest(CompoundTag data, NetworkManager.PacketContext packetContext) {
        if(data == null) {
            logError("Client Request: NBT Data is null!");
            return;
        }

        if (!data.contains(STAGE_KEY)) {
            logError("Missing stage key in client request");
            return;
        }

        int stage = data.getInt(STAGE_KEY);
        CompoundTag responseData = data.copy();

        switch (stage) {
            case CLEAR_STAGE -> clearClientShopData(data);
            case SEND_DATA_STAGE -> processShopData(data);
            case OPEN_STAGE -> openShopScreen(data);
            default -> logError("Unknown stage: " + stage);
        }

        Minecraft.getInstance().execute(() -> sendToServer(responseData));
    }

    @Environment(EnvType.CLIENT)
    private void clearClientShopData(CompoundTag data) {
        if (SDMShopClient.CurrentShop != null) {
            SDMShopClient.CurrentShop = new BaseShop(new ResourceLocation(SDMShopConstants.DEFAULT_SHOP), data.getUUID(SHOP_ID_KEY));
        }
    }

    @Environment(EnvType.CLIENT)
    private void processShopData(CompoundTag data) {
        if (!data.contains(DATA_KEY)) {
            logWarn("Missing data key in SEND_DATA_STAGE");
            return;
        }

        createShopIfNull(data);

        CompoundTag shopData = data.getCompound(DATA_KEY);
        SDMShopClient.CurrentShop.deserializeSplitedData(shopData);
    }

    @Environment(EnvType.CLIENT)
    private void openShopScreen(CompoundTag data) {
        createShopIfNull(data);
        Minecraft.getInstance().execute(() -> new ModernShopScreen().openGui());
    }

    public void startRequest(ServerPlayer player, UUID shopId) {
        executePerSend(() -> task(shopId), player);
    }

    public void startRequest(MinecraftServer server, UUID shopId) {
        executePerSend(() -> task(shopId), server);
    }

    public void startRequest(ServerPlayer player, String shopId) {
        executePerSend(() -> task(shopId), player);
    }

    public void startRequest(MinecraftServer server, String shopId) {
        executePerSend(() -> task(shopId), server);
    }

    protected CompoundTag[] task(UUID shopId) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(STAGE_KEY, CLEAR_STAGE);
        nbt.putUUID(SHOP_ID_KEY, shopId);
        return new CompoundTag[] { nbt };
    }

    protected CompoundTag[] task(String shopId) {
        ResourceLocation id = SDMShopServer.fromString(shopId);
        var opt = SDMShopServer.Instance().getShop(id);
        if(opt.isEmpty()) return new CompoundTag[0];
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(STAGE_KEY, CLEAR_STAGE);
        nbt.putUUID(SHOP_ID_KEY, opt.get().getId());
        return new CompoundTag[] { nbt };
    }

    protected void createShopIfNull(CompoundTag data) {
        if (SDMShopClient.CurrentShop == null) {
            SDMShopClient.CurrentShop = new BaseShop(new ResourceLocation("default"), ShopNBTUtils.get(data, SHOP_ID_KEY, NbtUtils::loadUUID).orElseGet(() -> {
                ShopDebugUtils.error("Can't load shop ID from packet");
                return UUID.randomUUID();
            }));
        }
    }

    @Override
    public String getId() {
        return SDMShopNetwork.SYNC_SHOP_AND_OPEN_REQUEST;
    }

    private void logError(String message) {
        SDMShop.LOGGER.error("SyncAndOpenShopASK: {}", message);
    }

    private void logWarn(String message) {
        SDMShop.LOGGER.warn("SyncAndOpenShopASK: {}", message);
    }
}
