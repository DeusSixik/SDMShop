package net.sdm.sdmshoprework;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sdm.sdmshoprework.api.register.ShopContentRegister;
import net.sdm.sdmshoprework.common.ModEvents;
import net.sdm.sdmshoprework.common.config.Config;
import net.sdm.sdmshoprework.common.icon.ShopItemIcon;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.shop.condition.ManaAndArtifice.ShopMNAFactionCondition;
import net.sdm.sdmshoprework.common.shop.condition.ManaAndArtifice.ShopMNALevelCondition;
import net.sdm.sdmshoprework.common.shop.condition.ManaAndArtifice.ShopMNATierCondition;
import net.sdm.sdmshoprework.common.shop.condition.ShopFTBQuestsCondition;
import net.sdm.sdmshoprework.common.shop.condition.ShopGameStagesCondition;
import net.sdm.sdmshoprework.common.shop.type.*;
import net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice.ShopMNAFactionEntryType;
import net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice.ShopMNALevelEntryType;
import net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice.ShopMNAProgressionEntryType;
import net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice.ShopMNATierEntryType;
import net.sdm.sdmshoprework.common.shop.type.integration.ShopGameStagesEntryType;
import net.sdm.sdmshoprework.common.shop.type.integration.ShopQuestEntryType;
import net.sdm.sdmshoprework.common.shop.type.integration.ShopSkillTreeEntryType;
import net.sdm.sdmshoprework.network.ShopNetwork;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SDMShopRework.MODID)
public class SDMShopRework {

    public static boolean isEditMode = true;

    public static final String MODID = "sdmshoprework";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void printStackTrace(String str, Throwable s){
        StringBuilder strBuilder = new StringBuilder(str);
        for (StackTraceElement stackTraceElement : s.getStackTrace()) {
            strBuilder.append("\t").append(" ").append("at").append(" ").append(stackTraceElement).append("\n");
        }
        str = strBuilder.toString();

        for (Throwable throwable : s.getSuppressed()) {
            printStackTrace(str, throwable);
        }

        Throwable ourCause = s.getCause();
        if(ourCause != null){
            printStackTrace(str, ourCause);
        }


        SDMShopRework.LOGGER.error(str);

    }

    public SDMShopRework() {

        ItemsRegister.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SDMShopPaths.initFilesAndFolders();
        Config.init(SDMShopPaths.getModFolder().resolve("sdmshop" + "-client.toml"));
        initEvents();
        register();


        new SDMShopClient().init();

        ShopNetwork.init();
        ShopContentRegister.init();
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SDMShopCommands.registerCommands(event.getDispatcher());
    }

    public void initEvents() {
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    public static String moneyString(long money) {
        return String.format("◎ %,d", money);
    }

    public void register(){
        if(ModList.get().isLoaded("ftbquests")) {
            ShopContentRegister.registerType(new ShopQuestEntryType.Constructor());
            ShopContentRegister.registerCondition(new ShopFTBQuestsCondition.Constructor());
        }

        ShopContentRegister.registerIcon(new ShopItemIcon.ShopItemIconC());

        ShopContentRegister.registerType(new ShopItemEntryType.Constructor());
        ShopContentRegister.registerType(new ShopTagEntryType.Constructor());
        ShopContentRegister.registerType(new ShopAdvancementEntryType.Constructor());
        ShopContentRegister.registerType(new ShopCommandEntryType.Constructor());
        ShopContentRegister.registerType(new ShopLocateBetaEntryType.Constructor());
        ShopContentRegister.registerType(new ShopXPEntryType.Constructor());
        ShopContentRegister.registerType(new ShopXPLevelEntryType.Constructor());
        ShopContentRegister.registerType(new ShopGameStagesEntryType.Constructor());

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
