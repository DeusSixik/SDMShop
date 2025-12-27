package net.sixik.sdmshop.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.*;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmshop.utils.RenderComponent;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ShopTab implements DataSerializerCompoundTag, ConditionSupport, ConfigSupport, RenderSupport, LimiterSupport, TooltipSupport, ShopObject {

    public Component title = Component.empty();
    protected final BaseShop ownerShop;
    protected UUID uuid;

    protected RenderComponent renderComponent = new RenderComponent();
    protected List<AbstractShopCondition> conditions = new ArrayList<>();

    protected int limitValue = 0;
    protected LimiterType limiterType = LimiterType.LocalPlayer;

    protected List<String> descriptions = new ArrayList<>();

    public ShopTab(BaseShop ownerShop) {
        this(ownerShop,  UUID.randomUUID());
    }

    public ShopTab(BaseShop ownerShop, UUID uuid) {
        this.uuid = uuid;
        this.ownerShop = ownerShop;
    }

    public UUID getId() {
        return uuid;
    }

    public BaseShop getOwnerShop() {
        return ownerShop;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("uuid", uuid);
        nbt.putString("title", title.getString());

        nbt.put(RenderComponent.KEY, renderComponent.serialize());
        serializeConditions(nbt);
        serializeLimiter(nbt);
        serializeTooltips(nbt);

        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.uuid = tag.getUUID("uuid");

        if(tag.contains("title"))
            title = Component.translatable(tag.getString("title"));

        renderComponent.deserialize(tag.getCompound(RenderComponent.KEY));

        deserializeConditions(tag, ownerShop);
        deserializeLimiter(tag);
        deserializeTooltips(tag);
    }

    @Override
    public List<AbstractShopCondition> getConditions() {
        return conditions;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        ShopUtils.addConfig(group, s -> {
            return s.addString("title", title.getString(), v -> title = Component.translatable(v), "");
        }, new TooltipList());

        group.addString("title", title.getString(), v -> title = Component.translatable(v), "");

        renderComponent.getConfig(group);
        getLimiterConfig(group);
        getTooltipConfig(group);
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
            ShopDebugUtils.error("Tab Limiter is null!");
            return 0;
        }

        ShopLimiter limiter = optLimiter.get();

        int used = 0;

        if (getLimiterType().isGlobal()) {
            used = limiter.getTabData(uuid).orElse(0);
        } else if (player != null && getLimiterType().isPlayer()) {
            used = limiter.getTabData(uuid, player).orElse(0);
        }

        return Math.max(0, getObjectLimit() - used);
    }

    @Override
    public int getObjectLimit() {
        return limitValue;
    }

    @Override
    public void changeObjectLimit(int value) {
        int old = this.limitValue;

        if(old != value) {
            if(value > 0)
                resetObjectLimit();
            else updateLimiterData(shopLimiter -> shopLimiter.deleteTabData(uuid));
        }

        this.limitValue = value;
    }

    @Override
    public void resetObjectLimit() {
        Optional<ShopLimiter> optLimiter = getShopLimiter();
        if(optLimiter.isEmpty()) return;

        optLimiter.get().resetTabDataAll(uuid);
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
        // 1. Если лимит выключен - разрешаем, но не записываем.
        if (!isLimiterActive()) return true;

        // 2. Если лимит уже достигнут - запрещаем.
        if (isLimitReached(player)) return false;

        Optional<ShopLimiter> limiterOpt = getShopLimiter();
        if (limiterOpt.isEmpty()) return false;

        ShopLimiter limiter = limiterOpt.get();

        // Вычисляем сколько реально списываем (чтобы не уйти в минус, если count пришел кривой)
        int left = getObjectLimitLeft(player);
        // Если left == MAX_VALUE (ошибка логики), берем count
        int v = (left == Integer.MAX_VALUE) ? count : Math.min(count, left);

        // 3. ЗАПИСЬ В БАЗУ
        // ВНИМАНИЕ: Для ShopTab используем addTabData!
        // В старом коде ты писал в EntryData, это ошибка.
        if (player == null) {
            limiter.addTabData(uuid, v);
        } else {
            limiter.addTabData(uuid, player.getGameProfile().getId(), v);
        }

        return true;
    }

    @Override
    public List<String> getTooltips() {
        return descriptions;
    }

    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.SHOP_TAB;
    }
}
