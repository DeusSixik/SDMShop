package net.sixik.sdmshop.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmshop.api.data.EntryAdditionalProperty;
import net.sixik.sdmshop.old_api.*;
import net.sixik.sdmshop.old_api.shop.*;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.entry_types.ItemEntryType;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;
import net.sixik.sdmshop.utils.DataSerializer;
import net.sixik.sdmshop.utils.RenderComponent;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopEntryTypeCreator;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import java.util.*;
import java.util.function.Supplier;

public class ShopEntry implements DataSerializer<CompoundTag>, ConditionSupport, ConfigSupport, RenderSupport, LimiterSupport, TooltipSupport, ShopObject {

    protected final BaseShop ownerShop;
    protected UUID uuid;
    protected UUID ownerTab;

    protected double price;
    protected long count = 1;
    protected int limitValue = 0;
    protected LimiterType limiterType = LimiterType.LocalPlayer;

    protected EntryAdditionalProperty scriptData = new EntryAdditionalProperty();

    protected Component title = Component.empty();
    protected List<String> descriptions = new ArrayList<>();

    protected AbstractEntryType entryType;
    protected AbstractEntrySellerType<?> entrySellerType;
    protected List<AbstractShopCondition> conditions = new ArrayList<>();

    protected RenderComponent renderComponent = new RenderComponent();

    public ShopEntryType type = ShopEntryType.Buy;

    public ShopEntry(BaseShop ownerShop) {
        this(ownerShop, UUID.randomUUID(), UUID.randomUUID(), new MoneySellerType(0D));
    }

    public ShopEntry(BaseShop ownerShop, UUID ownerTab) {
        this(ownerShop, UUID.randomUUID(), ownerTab, new MoneySellerType(0D));
    }

    public ShopEntry(BaseShop ownerShop, UUID uuid, UUID ownerTab, AbstractEntrySellerType<?> entrySellerType) {
        this.ownerShop = ownerShop;
        this.uuid = uuid;
        this.ownerTab = ownerTab;
        this.entrySellerType = entrySellerType;
        this.entryType = new ItemEntryType(this);
    }

    public ShopEntry setEntryType(AbstractEntryType entryType) {
        this.entryType = entryType;

        if(!this.entryType.getProperty().sellType.isBoth()) {
            type = this.entryType.getProperty().sellType.isSell() ? ShopEntryType.Sell : ShopEntryType.Buy;
        }

        return this;
    }

    public ShopEntry setPrice(double price) {
        this.price = price;
        return this;
    }

    public ShopEntry setCount(long count) {
        this.count = count;
        return this;
    }

    public ShopEntry updateIcon(ItemStack icon) {
        this.renderComponent.updateIcon(icon);
        return this;
    }

    public ShopEntry changeType(ShopEntryType type) {
        this.type = type;
        return this;
    }

    public ShopEntry copy() {
        return new ShopEntry(ownerShop, UUID.randomUUID(), ownerTab, entrySellerType).setEntryType(entryType.copy()).changeType(type);
    }

    public boolean onBuy(Player player, int count) {
        try {
            boolean value = getEntrySellerType().onBuy(player, this, count);

            ShopDebugUtils.log("On Buy value1: {}", value);
            if(value) {
                value = getEntryType().onBuy(player, this, count);
                ShopDebugUtils.log("On Buy value2: {}", value);
                return value;
            }
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error when try buy entry: ", e);
        }

        return false;
    }

    public boolean onSell(Player player, int count) {
        try {
            boolean value = getEntrySellerType().onSell(player, this, count);

            if(value) {
                value = getEntryType().onSell(player, this, count);
                return value;
            }
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error when try sell entry: ", e);
        }
        return false;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        nbt.putUUID("uuid", uuid);
        nbt.putUUID("ownerTab", ownerTab);
        nbt.putDouble("price", price);
        nbt.putLong("count", this.count);

        if(!title.getString().isEmpty())
            nbt.putString("title", title.getString());

        if(entryType != null) {
            CompoundTag entryNbt = entryType.serialize();

            entryNbt.putString("type_id", entryType.getId());
            nbt.put("entry_type", entryNbt);
        }

        nbt.put("seller_type", entrySellerType.serialize());
        nbt.putInt("type", type.ordinal());

        nbt.put(RenderComponent.KEY, renderComponent.serialize());

        if(limitValue > 0)
            nbt.putInt("limiter", limitValue);

        serializeConditions(nbt);
        serializeLimiter(nbt);
        serializeTooltips(nbt);

        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {

        this.uuid = tag.getUUID("uuid");
        this.ownerTab = tag.getUUID("ownerTab");

        if(tag.contains("price"))
            this.price = tag.getDouble("price");

        if(tag.contains("count"))
            this.count = tag.getLong("count");

        if(tag.contains("title"))
            this.title = Component.translatable(tag.getString("title"));

        if(tag.contains("entry_type")) {
            this.entryType = ShopEntryTypeCreator.createEntryType(this, tag.getCompound("entry_type")).orElse(null);
        }

        if(tag.contains("seller_type")) {
            CompoundTag sellerData = tag.getCompound("seller_type");
            String id = sellerData.getString(AbstractEntrySellerType.ID_KEY);
            Optional<Supplier<AbstractEntrySellerType<?>>> opt = ShopContentRegister.getSellerType(id);
            if(opt.isEmpty())
                this.entrySellerType = new MoneySellerType();
            else {
                this.entrySellerType = opt.get().get();
                this.entrySellerType.deserialize(tag.getCompound("seller_type"));
            }
        }

        if(tag.contains("limiter")) {
            this.limitValue = tag.getInt("limiter");
        }

        if(tag.contains("type"))
            this.type = ShopEntryType.values()[tag.getInt("type")];



        renderComponent.deserialize(tag.getCompound(RenderComponent.KEY));
        deserializeConditions(tag, ownerShop);
        deserializeLimiter(tag);
        deserializeTooltips(tag);

    }

    @Override
    public List<AbstractShopCondition> getConditions() {
        return conditions;
    }

    public long getCount() {
        return count + scriptData.getCount();
    }

    public double getPrice() {
        return price + scriptData.getPrice();
    }

    public UUID getId() {
        return uuid;
    }

    public UUID getTab() {
        return ownerTab;
    }

    public AbstractEntryType getEntryType() {
        return entryType;
    }

    public BaseShop getOwnerShop() {
        return ownerShop;
    }

    public ShopEntryType getType() {
        return type;
    }

    public AbstractEntrySellerType<?> getEntrySellerType() {
        return entrySellerType;
    }

    public Component getTitle() {
        return title;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addLong("count", count, v -> count = v, 1, 1, Long.MAX_VALUE);

        entryType.getConfig(group);

        ConfigGroup sellerGroup = group.getOrCreateSubgroup("seller_type");

        if(getEntrySellerType().isFractionalNumber())
             sellerGroup.addDouble("price", price, v -> price = v, 0.0, 0, Integer.MAX_VALUE);
        else sellerGroup.addInt("price", (price % 1 != 0) ? 0 : (int) price, v -> price = v, 0, 0, Integer.MAX_VALUE);

        sellerGroup.addEnum("type_id", getEntrySellerType().getEnumName(), v -> {
            if(!Objects.equals(v, getEntrySellerType().getEnumName())) {
                ShopContentRegister.getSellerTypeByEnumName(v).ifPresent(findType -> {
                    entrySellerType = findType.get();
                });
            }
        }, ShopContentRegister.getSellerTypesForConfig());
        getEntrySellerType().getConfig(sellerGroup);

        renderComponent.getConfig(group);
        getTooltipConfig(group);
        getLimiterConfig(group);
        getConditionConfig(group);
    }

    @Override
    public RenderComponent getRenderComponent() {
        return renderComponent;
    }

    @Override
    public int getObjectLimitLeft(@Nullable Player player) {
        if(!isLimiterActive()) return Integer.MAX_VALUE;

        Optional<ShopLimiter> optLimiter = getShopLimiter();

        if(optLimiter.isEmpty()) {
            ShopDebugUtils.error("Entry Limiter is null!");
            return 0;
        }

        ShopLimiter limiter = optLimiter.get();

        int used = 0;

        if (getLimiterType().isGlobal()) {
            used = limiter.getEntryData(uuid).orElse(0);
        } else if (player != null && getLimiterType().isPlayer()) {
            used = limiter.getEntryData(uuid, player).orElse(0);
        }

        return Math.max(0, getObjectLimit() - used);
    }

    @Override
    public int getObjectLimit() {
        return limitValue + scriptData.getLimit();
    }

    @Override
    public void changeObjectLimit(int value) {
        int old = this.limitValue;

        if(old != value) {
            if(value > 0)
                resetObjectLimit();
            else updateLimiterData(shopLimiter -> shopLimiter.deleteEntryData(uuid));
        }

        this.limitValue = value;
    }

    @Override
    public void resetObjectLimit() {
        Optional<ShopLimiter> optLimiter = getShopLimiter();
        if(optLimiter.isEmpty()) return;

        optLimiter.get().resetEntryDataAll(uuid);
    }

    @Override
    public LimiterType getLimiterType() {
        return limiterType;
    }

    @Override
    public void changeLimiterType(LimiterType type) {
        limiterType = type;
    }

    @Override
    public boolean updateLimit(@Nullable Player player, int count) {

        /*
            If the limit is disabled, we allow it, but do not record it.
         */
        if (!isLimiterActive()) return true;

        /*
            If the limit has already been reached, we prohibit it.
         */
        if (isLimitReached(player)) return false;

        Optional<ShopLimiter> limiterOpt = getShopLimiter();
        if (limiterOpt.isEmpty()) return false;

        ShopLimiter limiter = limiterOpt.get();

        /*
            We calculate how much we actually write off (so as not to go into the red if the count is skewed).
         */
        int left = getObjectLimitLeft(player);

        /*
            If left == MAX_VALUE (logic error), take count
         */
        int v = (left == Integer.MAX_VALUE) ? count : Math.min(count, left);

        /*
            Write to database
         */
        if (player == null) {
            limiter.addEntryData(uuid, v);
        } else {
            limiter.addEntryData(uuid, player.getGameProfile().getId(), v);
        }

        return true;
    }

    @Override
    public List<String> getTooltips() {
        return descriptions;
    }

    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.SHOP_ENTRY;
    }

    public EntryAdditionalProperty getScriptData() {
        return scriptData;
    }
}
