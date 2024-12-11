package net.sixk.sdmshop.shop.network.server;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdm_economy.api.ICustomData;
import net.sixk.sdmshop.SDMShop;

public class SendEditModeS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendEditModeS2C> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild(SDMShop.MODID, "editmod"));
    public static final StreamCodec<FriendlyByteBuf, SendEditModeS2C> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, SendEditModeS2C::getEditMod, SendEditModeS2C::new);

    boolean editMod;

    public SendEditModeS2C(boolean editMod){
        this.editMod = editMod;

    }

    public boolean getEditMod(){
        return editMod;
    }


    public static void  handle(SendEditModeS2C message, NetworkManager.PacketContext context) {
        ((ICustomData) Minecraft.getInstance().player).sdm$getCustomData().putBoolean("edit_mode", message.editMod);
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
