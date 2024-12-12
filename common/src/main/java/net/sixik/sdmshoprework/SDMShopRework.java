package net.sixik.sdmshoprework;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.sounds.SoundEvent;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.common.ModEvents;
import net.sixik.sdmshoprework.common.config.Config;
import net.sixik.sdmshoprework.common.integration.SDMShopRIntegration;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.register.SoundRegister;
import net.sixik.sdmshoprework.common.shop.condition.ShopFTBQuestsCondition;
import net.sixik.sdmshoprework.common.shop.type.*;
import net.sixik.sdmshoprework.common.shop.type.integration.ShopQuestEntryType;
import net.sixik.sdmshoprework.network.ShopNetwork;
import org.slf4j.Logger;

public class SDMShopRework
{
	public static final String MODID = "sdmshoprework";

	public static Logger LOGGER = LogUtils.getLogger();

	public static SoundEvent BUY_SOUND;
	public static SoundEvent CANCEL_SOUND;

	public static void init() {
		SDMShopPaths.initFilesAndFolders();

		register();
		ModEvents.init();
		ShopNetwork.init();
		SDMShopIcons.init();
		ItemsRegister.ITEMS.register();

		Config.init(SDMShopPaths.getModFolder());

		CommandRegistrationEvent.EVENT.register(SDMShopCommands::registerCommands);
		SDMShopRIntegration.init();
		BUY_SOUND = SoundRegister.registerSound("buy_sound");
		CANCEL_SOUND = SoundRegister.registerSound("cancel_sound");
		SoundRegister.init();

		EnvExecutor.runInEnv(Env.CLIENT, () -> SDMShopClient::init);
	}


	public static void printStackTrace(String str, Throwable s){
		StringBuilder strBuilder = new StringBuilder(str + " " + s.getMessage());
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

	public static String moneyString(long money) {
		return String.format("◎ %,d", money);
	}

	public static String moneyString(String money) {
		return "◎ " + money;
	}

	public static void register(){
		if(Platform.isModLoaded("ftbquests")) {
			ShopContentRegister.registerType(new ShopQuestEntryType.Constructor());
			ShopContentRegister.registerCondition(new ShopFTBQuestsCondition.Constructor());
		}

//        ShopContentRegister.registerIcon(new ShopItemIcon.ShopItemIconC());

		ShopContentRegister.registerType(new ShopItemEntryType.Constructor());
		ShopContentRegister.registerType(new ShopAdvancementEntryType.Constructor());
		ShopContentRegister.registerType(new ShopCommandEntryType.Constructor());
		ShopContentRegister.registerType(new ShopLocateBetaEntryType.Constructor());
		ShopContentRegister.registerType(new ShopXPEntryType.Constructor());
		ShopContentRegister.registerType(new ShopXPLevelEntryType.Constructor());
	}
}
