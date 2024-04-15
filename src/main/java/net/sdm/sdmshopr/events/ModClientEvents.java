package net.sdm.sdmshopr.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.sdm.sdmshopr.SDMShopR;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= SDMShopR.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class ModClientEvents {
}
