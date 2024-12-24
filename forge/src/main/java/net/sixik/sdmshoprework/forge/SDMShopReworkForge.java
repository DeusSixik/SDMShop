package net.sixik.sdmshoprework.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sixik.sdmshoprework.SDMShopRework;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.forge.shop.condition.ManaAndArtifice.ShopMNAFactionCondition;
import net.sixik.sdmshoprework.forge.shop.condition.ManaAndArtifice.ShopMNALevelCondition;
import net.sixik.sdmshoprework.forge.shop.condition.ManaAndArtifice.ShopMNATierCondition;
import net.sixik.sdmshoprework.forge.shop.condition.ShopGameStagesCondition;
import net.sixik.sdmshoprework.forge.shop.type.ManaAndArtifice.ShopMNAFactionEntryType;
import net.sixik.sdmshoprework.forge.shop.type.ManaAndArtifice.ShopMNALevelEntryType;
import net.sixik.sdmshoprework.forge.shop.type.ManaAndArtifice.ShopMNAProgressionEntryType;
import net.sixik.sdmshoprework.forge.shop.type.ManaAndArtifice.ShopMNATierEntryType;
import net.sixik.sdmshoprework.forge.shop.type.ShopGameStagesEntryType;
import net.sixik.sdmshoprework.forge.shop.type.ShopSkillTreeEntryType;
import net.sixik.sdmshoprework.forge.shop.type.ShopTagEntryType;

@Mod(SDMShopRework.MODID)
public class SDMShopReworkForge {
    public SDMShopReworkForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SDMShopRework.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        register();
        SDMShopRework.init();
    }

    public static void register(){
        ShopContentRegister.registerType(new ShopGameStagesEntryType.Constructor());
        ShopContentRegister.registerType(new ShopTagEntryType.Constructor());
        ShopContentRegister.registerType(new ShopSkillTreeEntryType.Constructor());
        ShopContentRegister.registerType(new ShopMNAFactionEntryType.Constructor());
        ShopContentRegister.registerType(new ShopMNALevelEntryType.Constructor());
        ShopContentRegister.registerType(new ShopMNAProgressionEntryType.Constructor());
        ShopContentRegister.registerType(new ShopMNATierEntryType.Constructor());

        ShopContentRegister.registerCondition(new ShopGameStagesCondition.Constructor());
        ShopContentRegister.registerCondition(new ShopMNATierCondition.Constructor());
        ShopContentRegister.registerCondition(new ShopMNAFactionCondition.Constructor());
        ShopContentRegister.registerCondition(new ShopMNALevelCondition.Constructor());
    }
}