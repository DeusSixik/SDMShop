package net.sixik.sdmshop.network.async;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.UUID;

public class AsyncClientTasks {

    public static void init() {
        AsyncBridge.registerHandler(AsyncServerTasks.OPEN_SHOP, buf -> {
            final ResourceLocation shopID = buf.readResourceLocation();
            final UUID shopUId = buf.readUUID();
            final CompoundTag shopData = buf.readNbt();

            if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUId)) {
                SDMShopClient.CurrentShop = new BaseShop(shopID, shopUId);
            }

            SDMShopClient.CurrentShop.deserialize(shopData);

            new ModernShopScreen().openGui();

            return null; // No response to the server is required.
        });
    }

    public static void openShop(ResourceLocation shopId) {
        AsyncBridge.askServer(AsyncServerTasks.GET_OPEN_SHOP, buf -> {
            buf.writeResourceLocation(shopId);
            return buf;
        }).thenAcceptAsync(response -> {
            final boolean found = response.readBoolean();
            if (!found) {
                Minecraft.getInstance().player.displayClientMessage(Component.literal("Shop not found!"), false);
                return;
            }
            final UUID shopUID = response.readUUID();
            final CompoundTag shopData = response.readNbt();

            if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUID)) {
                SDMShopClient.CurrentShop = new BaseShop(shopId, shopUID);
            }

            SDMShopClient.CurrentShop.deserialize(shopData);

            new ModernShopScreen().openGui();
        }, Minecraft.getInstance()).exceptionally(ex -> {
            SDMShop.LOGGER.error("Failed to load shop", ex);
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Error loading shop!"), false);
            return null;
        });
    }
}
