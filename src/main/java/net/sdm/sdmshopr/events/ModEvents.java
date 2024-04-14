package net.sdm.sdmshopr.events;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.network.SyncShop;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.entry.type.ItemEntryType;
import net.sdm.sdmshopr.shop.tab.ShopTab;

import static net.sdm.sdmshopr.SDMShopR.getFile;

@Mod.EventBusSubscriber(modid= SDMShopR.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.DEDICATED_SERVER)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity().level().isClientSide) return;

        if(event.getEntity() instanceof ServerPlayer player && Shop.SERVER != null) {
            new SyncShop(Shop.SERVER.serializeNBT()).sendTo(player);
        }
    }

    @SubscribeEvent
    public static void onLevelSavedEvent(LevelEvent.Save event){
        if(event.getLevel() instanceof Level && Shop.SERVER != null && Shop.SERVER.needSave && !event.getLevel().isClientSide() && ((Level) event.getLevel()).dimension() == Level.OVERWORLD){
            Shop.SERVER.needSave = false;
            SNBT.write(getFile(), Shop.SERVER.serializeNBT());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onWorldLoaded(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level && !event.getLevel().isClientSide() && ((Level) event.getLevel()).dimension() == Level.OVERWORLD) {
            Shop.SERVER = new Shop();

//            ShopTab tab = new ShopTab(Shop.SERVER);
//            tab.shopEntryList.add(new ShopEntry<>(tab,
//                    ItemEntryType.of(Items.ACACIA_DOOR.getDefaultInstance()), 10,10,true));
//            tab.shopEntryList.add(new ShopEntry<>(tab,
//                    ItemEntryType.of(Items.ITEM_FRAME.getDefaultInstance()), 10,10,true));
//            Shop.SERVER.shopTabs.add(tab);
//            tab = new ShopTab(Shop.SERVER);
//            Shop.SERVER.shopTabs.add(tab);

            Shop.SERVER.needSave();

            CompoundTag nbt = SNBT.read(getFile());

            if (nbt != null) {
                Shop.SERVER.deserializeNBT(nbt);
            }
        }
    }
}
