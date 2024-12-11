package net.sixk.sdmshop.neoforge;

import net.sixk.sdmshop.SDMShop;
import net.neoforged.fml.common.Mod;

@Mod(SDMShop.MODID)
public final class SDMShopNeoForge {
    public SDMShopNeoForge() {
        // Run our common setup.
        SDMShop.init();
    }
}
