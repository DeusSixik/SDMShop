package net.sixik.sdmshop.server;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;

public class SDMShopEvents {

    public static void init() {
        LifecycleEvent.SERVER_BEFORE_START.register(SDMShopServer::new);

        LifecycleEvent.SERVER_STOPPED.register(s -> {
            SDMShopServer.Instance().save(s);
        });

        PlayerEvent.PLAYER_JOIN.register(serverPlayer -> {
            new SendLimiterS2C(SDMShopServer.Instance().getShopLimiter().serializeClient(serverPlayer)).sendTo(serverPlayer);
        });

//        PlayerEvent.DROP_ITEM.register((player, itemEntity) -> {
//
//            player.sendSystemMessage(Component.literal(String.valueOf(ShopItemHelper.countItem(player.getInventory(), itemEntity.getItem(), !itemEntity.getItem().hasTag()))));
//
//            if(itemEntity.getItem().is(Items.BEDROCK)) {
//                player.sendSystemMessage(Component.literal(String.valueOf(ShopItemHelper.giveItems(player, Items.DIAMOND.getDefaultInstance(), 1000))));
//            }
//
//            return EventResult.interruptDefault();
//        });
    }
}
