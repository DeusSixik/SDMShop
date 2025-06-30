package net.sixk.sdmshop.shop.network.server;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.shop.BuyingWindow;
import net.sixk.sdmshop.shop.PlayerBasket;
import net.sixk.sdmshop.shop.ShopPage;
import net.sixk.sdmshop.shop.Tab.TovarTab;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.TovarList;

import java.util.Iterator;

public class SendShopDataS2C implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<SendShopDataS2C> TYPE = new CustomPacketPayload.Type(ResourceLocation.tryBuild("sdmshop", "send_data"));
    public static final StreamCodec<FriendlyByteBuf, SendShopDataS2C> STREAM_CODEC;
    Tag tovarTag;
    Tag tabTag;

    public SendShopDataS2C(Tag tovarTag, Tag tabTag) {
        this.tovarTag = tovarTag;
        this.tabTag = tabTag;
    }

    public static void handle(SendShopDataS2C message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            TovarList.CLIENT.deserialize((KeyData)IData.valueOf(message.tovarTag), context.registryAccess());
            TovarTab.CLIENT.deserialize((KeyData)IData.valueOf(message.tabTag), context.registryAccess());

            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof ScreenWrapper screenWrapper) {
                BaseScreen patt3$temp = screenWrapper.getGui();
                if (patt3$temp instanceof ShopPage shopPageScreen) {
                    shopPageScreen.getGui().refreshWidgets();
                }

                patt3$temp = screenWrapper.getGui();
                if (patt3$temp instanceof PlayerBasket playerBasketScreen) {
                    playerBasketScreen.getGui().refreshWidgets();
                }

                patt3$temp = screenWrapper.getGui();
                if (patt3$temp instanceof BuyingWindow buyingWindowScreen) {
                    Iterator var11 = TovarList.CLIENT.tovarList.iterator();

                    while(var11.hasNext()) {
                        AbstractTovar tovar = (AbstractTovar)var11.next();
                        if (tovar.uuid.equals(buyingWindowScreen.tovar.uuid)) {
                            buyingWindowScreen.getGui().closeGui();
                            (new BuyingWindow(tovar.uuid)).openGui();
                        }
                    }
                }
            }

        });
    }

    public Tag getTovarTag() {
        return this.tovarTag;
    }

    public Tag getTabTag() {
        return this.tabTag;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.TAG, SendShopDataS2C::getTovarTag, ByteBufCodecs.TAG, SendShopDataS2C::getTabTag, SendShopDataS2C::new);
    }
}

