package net.sixik.sdmshop.utils;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.CustomPlayerData;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.utils.ErrorCodes;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.async.AsyncClientTasks;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.network.async.AsyncServerTasks;
import net.sixik.sdmshop.network.economy.ShopChangeMoneyC2S;
import net.sixik.sdmshop.network.server.ChangeEditModeC2S;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiterAttachType;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.config.ConfigBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShopUtils {

    public static final boolean isMarketLoaded = Platform.isModLoaded("sdm_market");

    public static ResourceLocation location(String path) {
        return new ResourceLocation(SDMShop.MODID, path);
    }

    @Environment(EnvType.CLIENT)
    public static boolean isEditModeClient() {
        return isEditMode(Minecraft.getInstance().player);
    }

    public static boolean isEditMode(Player player) {
        CustomPlayerData.Data data;

        if(player.isLocalPlayer()) data = EconomyAPI.getCustomClientData().data;
        else                       data = EconomyAPI.getCustomServerData().getPlayerCustomData(player);

        return data.nbt.contains("edit_mode") && data.nbt.getBoolean("edit_mode");
    }

    @Environment(EnvType.CLIENT)
    public static void changeEditModeClient(boolean value) {
        changeEditMode(Minecraft.getInstance().player, value);
    }

    public static void changeEditMode(Player player, boolean value) {
        if(player.isLocalPlayer()) {
            new ChangeEditModeC2S(value).sendToServer();
            return;
        }

        try {
            CustomPlayerData.Data data = EconomyAPI.getCustomServerData().getPlayerCustomData(player);
            data.nbt.putBoolean("edit_mode", value);

            CurrencyHelper.syncCustomData((ServerPlayer) player);
        } catch (Exception e) {
            SDMEconomy.printStackTrace("", e);
        }
    }

    public static double getMoney(Player player) {
        return getMoney(player, SDMCoin.getId());
    }

    public static double getMoney(Player player, String moneyName) {
        if(player.isLocalPlayer())
            return EconomyAPI.getPlayerCurrencyClientData().getBalance(moneyName);

        return EconomyAPI.getPlayerCurrencyServerData().getBalance(player, moneyName).value;
    }

    public static boolean addMoney(Player player, double value) {
        return addMoney(player, SDMCoin.getId(), value);
    }

    public static boolean addMoney(Player player, String moneyName, double value) {
        if(player.isLocalPlayer()) {
            new ShopChangeMoneyC2S(moneyName, getMoney(player) + value).sendToServer();
            return CurrencyHelper.isAdmin(player);
        }

        ErrorCodes result = EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, moneyName, value);
        if(result.isSuccess()) {
            EconomyAPI.syncPlayer((ServerPlayer) player);
            return true;
        }
        return false;
    }

    public static boolean setMoney(Player player, double value) {
        return setMoney(player, SDMCoin.getId(), value);
    }

    public static boolean setMoney(Player player, String moneyName, double value) {
        if(player.isLocalPlayer()) {
            new ShopChangeMoneyC2S(moneyName, value).sendToServer();
            return CurrencyHelper.isAdmin(player);
        }

        ErrorCodes result = EconomyAPI.getPlayerCurrencyServerData().setCurrencyValue(player, moneyName, value);
        if(result.isSuccess()) {
            EconomyAPI.syncPlayer((ServerPlayer) player);
            return true;
        }
        return false;
    }

    public static String moneyToString(Player player) {
        return moneyToString(player, SDMCoin.getId());
    }

    public static String moneyToString(Player player, String moneyName) {
        StringBuilder builder = new StringBuilder();

        if(player.isLocalPlayer()) {
            Optional<CurrencyPlayerData.PlayerCurrency> opt = EconomyAPI.getPlayerCurrencyClientData().getCurrency(moneyName);
            if(opt.isPresent()) {
                var currency = opt.get();

                if(currency.currency.symbol.type == CurrencySymbol.Type.CHAR)
                    builder.append(currency.currency.symbol.value).append(" ");

                builder.append(currency.balance);


            }

            return builder.toString();
        }

        Optional<CurrencyPlayerData.PlayerCurrency> opt = EconomyAPI.getPlayerCurrencyServerData().getPlayerCurrency(player, moneyName);
        if(opt.isPresent()) {
            var currency = opt.get();

            if(currency.currency.symbol.type == CurrencySymbol.Type.CHAR)
                builder.append(currency.currency.symbol.value).append(" ");

            builder.append(currency.balance);

        }

        return builder.toString();
    }

    public static String moneyToString(double l, String moneyName) {
        for (BaseCurrency currency : EconomyAPI.getAllCurrency().value.currencies) {
            if(Objects.equals(currency.getName(), moneyName))
                return (currency.symbol.type == CurrencySymbol.Type.CHAR ? currency.symbol.value : "") + " " + l;
        }

        return " " + l;
    }

    public static void sendOpenShop(MinecraftServer server, String shopId) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AsyncServerTasks.openShop(player, SDMShopServer.parseLocation(shopId));
        }
    }

    public static void sendOpenShop(ServerPlayer player, String shopId) {
        AsyncServerTasks.openShop(player, SDMShopServer.parseLocation(shopId));
    }

    public static void openShopClient(String shopId) {
        AsyncClientTasks.openShop(SDMShopServer.parseLocation(shopId));
    }

    public static <T> ConfigValue<T> addConfig(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun, Component[] components) {
        TooltipList list = new TooltipList();
        for (Component component : components) {
            list.add(component);
        }

        return addConfig(group, fun, list);
    }

    public static <T> ConfigValue<T> addConfig(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun, TooltipList list) {
        ConfigValue<T> value = fun.apply(group);
        value.addInfo(list);
        return value;
    }

    public static <T> ConfigValue<T> addConfig(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun, Consumer<ConfigBuilder<T>> builderConsumer) {
        ConfigBuilder<T> builder = addConfigBuilder(group, fun);
        builderConsumer.accept(builder);
        return builder.getValue();
    }

    public static <T> ConfigBuilder<T> addConfigBuilder(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun) {
        return new ConfigBuilder<>(group, fun);
    }

    public static ShopLimiterData getShopLimit(ShopTab shopTab, ShopEntry shopEntry, Player player) {

        /*
            By default, we assume that there is no limit (Infinity)
         */
        int tabLimit = Integer.MAX_VALUE;
        int entryLimit = Integer.MAX_VALUE;

        /*
            If the Tab has a limiter enabled, we get the remainder.
            getObjectLimitLeft will return the correct remainder or MAX_VALUE.
         */
        if (shopTab.isLimiterActive()) {
            tabLimit = shopTab.getObjectLimitLeft(player);
        }

        /*
            If the Entry has a limiter enabled
         */
        if (shopEntry.isLimiterActive()) {
            entryLimit = shopEntry.getObjectLimitLeft(player);
        }

        /*
            If both are unlimited
         */
        if (tabLimit == Integer.MAX_VALUE && entryLimit == Integer.MAX_VALUE) {
            return new ShopLimiterData(ShopLimiterAttachType.None, Integer.MAX_VALUE);
        }

        /*
            If the Tab is unlimited, we refund the Entry limit.
         */
        if (tabLimit == Integer.MAX_VALUE) {
            return new ShopLimiterData(ShopLimiterAttachType.Entry, entryLimit);
        }

        /*
            If the Entry is unlimited, we will refund the Tab limit.
         */
        if (entryLimit == Integer.MAX_VALUE) {
            return new ShopLimiterData(ShopLimiterAttachType.Tab, tabLimit);
        }

        /*
            If both have a limit, we return the one that is lower (stricter)
         */
        if (tabLimit <= entryLimit) {
            return new ShopLimiterData(ShopLimiterAttachType.Tab, tabLimit);
        } else {
            return new ShopLimiterData(ShopLimiterAttachType.Entry, entryLimit);
        }
    }

    public static int getMaxEntryOfferSize(ShopTab shopTab, ShopEntry shopEntry, Player player) {
        return getMaxEntryOfferSize(shopEntry, player, getShopLimit(shopTab, shopEntry, player).value());
    }

    public static int getMaxEntryOfferSize(ShopEntry shopEntry, Player player, int size) {
        int howMany = shopEntry.getEntryType().howMany(player, shopEntry);
        if (size <= -1) return howMany;
        return Math.min(howMany, size);
    }

    public static int getPlayerXP(Player player) {
        return (int)((float)getExperienceForLevel(player.experienceLevel) + player.experienceProgress * (float)player.getXpNeededForNextLevel());
    }

    public static int getLevelForExperience(int targetXp) {
        int level = 0;

        while(true) {
            int xpToNextLevel = xpBarCap(level);
            if (targetXp < xpToNextLevel) {
                return level;
            }

            ++level;
            targetXp -= xpToNextLevel;
        }
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) {
            return 0;
        } else if (level <= 15) {
            return sum(level, 7, 2);
        } else {
            return level <= 30 ? 315 + sum(level - 15, 37, 5) : 1395 + sum(level - 30, 112, 9);
        }
    }

    public static int xpBarCap(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

}
