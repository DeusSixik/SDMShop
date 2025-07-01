package net.sixik.sdmshop.api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public interface LimiterSupport {

    /**
     * @return if didn't have limit return -1
     */
    int getObjectLimitLeft(@Nullable Player player);

    int getObjectLimit();

    void changeObjectLimit(int value);

    void resetObjectLimit();

    boolean updateLimit(@Nullable Player player, int count);

    default boolean isLimiterActive() {
        return getObjectLimit() > 0;
    }

    LimiterType getLimiterType();

    void changeLimiterType(LimiterType type);

    default boolean isLimitReached(@Nullable Player player) {
       return isLimiterActive() && getObjectLimitLeft(player) == 0;
    }

    default Optional<ShopLimiter> getShopLimiter() {
        ShopLimiter limiter = null;

        if(SDMShopServer.Instance() != null) {
            limiter = SDMShopServer.Instance().getShopLimiter();
        } else if(SDMShopClient.shopLimiter != null)
            limiter = SDMShopClient.shopLimiter;
        return Optional.ofNullable(limiter);
    }

    static Optional<ShopLimiter> getShopLimiterStatic() {
        ShopLimiter limiter = null;

        if(SDMShopServer.Instance() != null) {
            limiter = SDMShopServer.Instance().getShopLimiter();
        } else if(SDMShopClient.shopLimiter != null)
            limiter = SDMShopClient.shopLimiter;
        return Optional.ofNullable(limiter);
    }

    default void getLimiterConfig(ConfigGroup group) {
        ConfigGroup limiterGroup = group.getOrCreateSubgroup("limiter");
        limiterGroup.setNameKey("sdm.shop.limiter");

        limiterGroup.addInt("value", getObjectLimit(), this::changeObjectLimit, 0, 0, Integer.MAX_VALUE)
                .setNameKey("sdm.shop.limiter.value");

        limiterGroup.addEnum("type", getLimiterType().name(), v -> {
            if(!Objects.equals(v, getLimiterType().name())) {
                changeLimiterType(LimiterType.valueOf(v));
            }
        }, LimiterType.getTypeList())
                .setNameKey("sdm.shop.limiter.type");
    }

    default void serializeLimiter(CompoundTag nbt) {
        nbt.putInt("limiter_value", getObjectLimit());
        nbt.putString("limiter_type", getLimiterType().name());
    }

    default void deserializeLimiter(CompoundTag nbt) {
        if(nbt.contains("limiter_value"))
            changeObjectLimit(nbt.getInt("limiter_value"));
        if(nbt.contains("limiter_type"))
            changeLimiterType(LimiterType.valueOf(nbt.getString("limiter_type")));
    }

    default void updateLimiterData(Consumer<ShopLimiter> limiterConsumer) {
        SDMShopServer.InstanceOptional().ifPresent(sdmShopServer -> {
            limiterConsumer.accept(sdmShopServer.getShopLimiter());
        });
    }
}
