package net.sixik.sdmshop;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.sixik.sdmeconomy.currencies.CustomCurrencies;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.compat.SDMShopIntegration;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.registers.ShopItemRegisters;
import net.sixik.sdmshop.server.SDMShopEvents;
import org.slf4j.Logger;

public final class SDMShop {
    public static final String MODID = "sdmshop";
    public static final Logger LOGGER = LogUtils.getLogger();



    public static void init() {
        CustomCurrencies.CURRENCIES.put(SDMCoin.getId(), SDMCoin::new);

        ShopConfig.loadConfig();

        ShopContentRegister.init();
        SDMShopNetwork.init();
        SDMShopEvents.init();
        CommandRegistrationEvent.EVENT.register(SDMShopCommands::registerCommands);
        ShopItemRegisters.ITEMS.register();
        SDMShopIntegration.init();

        EnvExecutor.runInEnv(Env.CLIENT, () -> SDMShopClient::init);
    }
}
