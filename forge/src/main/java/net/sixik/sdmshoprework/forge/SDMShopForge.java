package net.sixik.sdmshoprework.forge;

import dev.architectury.platform.forge.EventBuses;
import net.sixik.sdmshoprework.SDMShopRework;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SDMShopRework.MODID)
public class SDMShopForge {
    public SDMShopForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SDMShopRework.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        SDMShopRework.init();
    }
}