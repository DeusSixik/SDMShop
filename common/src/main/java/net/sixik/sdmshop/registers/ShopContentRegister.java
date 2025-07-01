package net.sixik.sdmshop.registers;

import dev.ftb.mods.ftblibrary.config.NameMap;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.Constructor;
import net.sixik.sdmshop.api.network.AbstractASKRequest;
import net.sixik.sdmshop.api.shop.AbstractEntrySellerType;
import net.sixik.sdmshop.api.shop.AbstractEntryType;
import net.sixik.sdmshop.api.shop.AbstractShopCondition;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.conditions.integration.StageCondition;
import net.sixik.sdmshop.shop.entry_types.*;
import net.sixik.sdmshop.shop.entry_types.integration.StageEntryType;
import net.sixik.sdmshop.shop.seller_types.ItemSellerType;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShopContentRegister {

    protected static final Map<String, Constructor<? extends AbstractShopCondition>> CONDITIONS = new LinkedHashMap<>();
    protected static final Map<String, Supplier<AbstractEntrySellerType<?>>> SELLER_TYPES = new LinkedHashMap<>();
    protected static final Map<String, Function<ShopEntry, AbstractEntryType>> ENTRY_TYPES = new LinkedHashMap<>();
    protected static final Map<String, Function<Void, AbstractASKRequest>> REQUESTS = new LinkedHashMap<>();

    public static void registerCondition(String id, Constructor<? extends AbstractShopCondition> function) {
        if(CONDITIONS.containsKey(id))
            throw new RuntimeException("Condition with " + id + " id already registered!");

        CONDITIONS.put(id, function);
        SDMShop.LOGGER.info("Registered condition [{}]", id);
    }

    public static void registerSellerType(String id, Supplier<AbstractEntrySellerType<?>> supplier) {
        if(SELLER_TYPES.containsKey(id))
            throw new RuntimeException("SellerType with " + id + " id already registered!");

        SELLER_TYPES.put(id, supplier);
        SDMShop.LOGGER.info("Registered seller type [{}]", id);
    }

    public static void registerEntryType(String id, Function<ShopEntry, AbstractEntryType> func) {
        if(ENTRY_TYPES.containsKey(id))
            throw new RuntimeException("Entry Type with " + id + " id already registered!");

        ENTRY_TYPES.put(id, func);
        SDMShop.LOGGER.info("Registered entry type [{}]", id);
    }

    public static String registerRequest(String id, Function<Void, AbstractASKRequest> func) {
        if(REQUESTS.containsKey(id))
            throw new RuntimeException("Entry Type with " + id + " id already registered!");

        REQUESTS.put(id, func);
        SDMShop.LOGGER.info("Registered ASK request [{}]", id);
        return id;
    }

    public static Map<String, Constructor<? extends AbstractShopCondition>> getConditions() {
        return new HashMap<>(CONDITIONS);
    }

    public static Map<String, Supplier<AbstractEntrySellerType<?>>> getSellerTypes() {
        return new HashMap<>(SELLER_TYPES);
    }

    public static Map<String, Function<ShopEntry, AbstractEntryType>> getEntryTypes() {
        return new HashMap<>(ENTRY_TYPES);
    }

    public static Map<String, Function<Void, AbstractASKRequest>> getRequests() {
        return new HashMap<>(REQUESTS);
    }

    public static Optional<Supplier<AbstractEntrySellerType<?>>> getSellerType(String id) {
        return Optional.ofNullable(SELLER_TYPES.getOrDefault(id, null));
    }

    public static Optional<Supplier<AbstractEntrySellerType<?>>> getSellerTypeByEnumName(String id) {
        return SELLER_TYPES.values().stream().filter(s -> Objects.equals(s.get().getEnumName(), id)).findFirst();
    }

    public static Optional<Constructor<? extends AbstractShopCondition>> getCondition(String id) {
        return Optional.ofNullable(CONDITIONS.getOrDefault(id, null));
    }

    public static Optional<Function<ShopEntry, AbstractEntryType>> getEntryType(String id) {
        return Optional.ofNullable(ENTRY_TYPES.getOrDefault(id, null));
    }

    public static Optional<Function<Void, AbstractASKRequest>> getRequest(String id) {
        return Optional.ofNullable(REQUESTS.getOrDefault(id, null));
    }

    public static void init() {
        registerSellerType("money_seller", MoneySellerType::new);
        registerSellerType("item_seller", ItemSellerType::new);

        registerEntryType("shopItemEntryType", ItemEntryType::new);
        registerEntryType("itemTag", TagEntryType::new);
        registerEntryType("advancementType", AdvancementEntryType::new);
        registerEntryType("commandType", CommandEntryType::new);
        registerEntryType("xpType", XPEntryType::new);
        registerEntryType("xpLevelType", XPLevelEntryType::new);
        registerEntryType("stageType", StageEntryType::new);

        registerCondition("stageCondition", StageCondition::new);
    }

    public static NameMap<String> getSellerTypesForConfig() {
        List<String> str = new ArrayList<>();
        for (Supplier<AbstractEntrySellerType<?>> value : getSellerTypes().values()) {
            str.add(value.get().getEnumName());
        }

        return NameMap.of("MONEY", str).create();
    }
}
