package net.sixik.sdmshop.forge;

import net.sixik.sdmshop.SDMShop;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SDMShop.MODID)
public final class SDMShopForge {
    public SDMShopForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(SDMShop.MODID, FMLJavaModLoadingContext.get().getModEventBus());



        // Run our common setup.
        SDMShop.init();
    }
}
