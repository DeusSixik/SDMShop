package net.sdm.sdmshoprework.network.server.reload;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshoprework.SDMShopPaths;
import net.sdm.sdmshoprework.common.config.Config;
import net.sdm.sdmshoprework.network.ShopNetwork;

public class SendReloadConfigS2C extends BaseS2CMessage {

    public SendReloadConfigS2C() {}
    public SendReloadConfigS2C(FriendlyByteBuf buf) {}

    @Override
    public MessageType getType() {
        return ShopNetwork.SEND_RELOAD_CONFIG;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(NetworkManager.PacketContext packetContext) {
        if(packetContext.getEnv().isClient()) {
            Config.init(SDMShopPaths.getModFolder().resolve("sdmshop" + "-client.toml"));
            packetContext.getPlayer().sendSystemMessage(Component.literal("Reload Complete"));
        }
    }
}
