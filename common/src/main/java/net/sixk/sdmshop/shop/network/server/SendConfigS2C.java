package net.sixk.sdmshop.shop.network.server;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.data.config.Config;
import net.sixk.sdmshop.data.config.ConfigFile;

public class SendConfigS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendConfigS2C> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "send_config"));
    public static final StreamCodec<FriendlyByteBuf, SendConfigS2C> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, SendConfigS2C::getBool, SendConfigS2C::new);

    public static CompoundTag nbt;
    public boolean r;

    public SendConfigS2C(boolean r){
        this.r = r;
        nbt = new CompoundTag();
        nbt.putBoolean("disableKeyBind", ConfigFile.SERVER.disableKeyBind);
        nbt.putBoolean("styles", ConfigFile.SERVER.style);
    }

    public boolean getBool(){
        return r;
    }

    public static void  handle(SendConfigS2C message, NetworkManager.PacketContext context) {
        ConfigFile.CLIENT.disableKeyBind = nbt.getBoolean("disableKeyBind");
        ConfigFile.CLIENT.style = nbt.getBoolean("styles");
        Config.reload();
        context.getPlayer().sendSystemMessage(Component.literal("Reload Complete"));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}
}
