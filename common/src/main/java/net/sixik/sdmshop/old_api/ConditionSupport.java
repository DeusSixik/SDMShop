package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.BaseShop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConditionSupport {

    String CONDITION_KEY = "conditions";

    List<AbstractShopCondition> getConditions();

    default boolean addCondition(AbstractShopCondition condition) {
        if (condition == null) return false;
        synchronized (this) {
            return getConditions().add(condition);
        }
    }

    default boolean removeCondition(AbstractShopCondition condition) {
        boolean result;
        synchronized (this) {
            result = getConditions().removeIf(s -> s.equals(condition));
            if(!result)
                result = getConditions().removeIf(s -> s.getId().equals(condition.getId()));
        }

        return result;
    }

    default Optional<AbstractShopCondition> getCondition(int index) {
        synchronized (this) {
            int size = getConditions().size();
            if (index < 0 || index >= size) return Optional.empty();
            return Optional.ofNullable(getConditions().get(index));
        }
    }

    default boolean isLockedAny(ShopObject shopObject) {
        synchronized (this) {
            return !getConditions().isEmpty() && getConditions().stream().anyMatch(s -> s.isLocked(shopObject));
        }
    }

    default boolean isLockedAll(ShopObject shopObject) {
        synchronized (this) {
            return !getConditions().isEmpty() && getConditions().stream().allMatch(s -> s.isLocked(shopObject));
        }
    }

    default void serializeConditions(CompoundTag nbt) {
        synchronized (this) {
            ListTag listTag = new ListTag();
            for (AbstractShopCondition condition : getConditions()) {
                CompoundTag conditionNbt = new CompoundTag();
                conditionNbt.putString("id", condition.getId());
                conditionNbt.put("data", condition.serialize());
                listTag.add(conditionNbt);
            }

            nbt.put(CONDITION_KEY, listTag);
        }
    }

    default void deserializeConditions(CompoundTag tag, BaseShop shopBase) {
        synchronized (this) {


            getConditions().clear();
            List<String> conditionsIds = new ArrayList<>();

            if(tag.contains(CONDITION_KEY)){
                ListTag listTag = (ListTag) tag.get(CONDITION_KEY);

                for (Tag tag1 : listTag) {
                    CompoundTag conditionNbt = (CompoundTag) tag1;

                    String id = conditionNbt.getString("id");

                    Optional<Constructor<? extends AbstractShopCondition>> find =
                            ShopContentRegister.getCondition(id);

                    if (find.isEmpty()) continue;

                    AbstractShopCondition condition = find.get().createDefaultInstance();

                    conditionsIds.add(id);

                    condition.deserialize(conditionNbt.getCompound("data"));
                    condition.setShop(shopBase);
                    addCondition(condition);
                }
            }


            for (Map.Entry<String, Constructor<? extends AbstractShopCondition>> entry : ShopContentRegister.getConditions().entrySet()) {
                if(conditionsIds.contains(entry.getKey())) continue;

                AbstractShopCondition value = entry.getValue().createDefaultInstance();
                value.setShop(shopBase);
                addCondition(value);
            }
        }
    }

    default void getConditionConfig(ConfigGroup group) {
        ConfigGroup conditionGroup = group.getOrCreateSubgroup("conditions").setNameKey("sdm.shop.conditions");
        for (AbstractShopCondition condition : getConditions()) {
            condition.getConfig(conditionGroup);
        }
    }
}
