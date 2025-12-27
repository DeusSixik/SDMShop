package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface TooltipSupport {



    List<String> getTooltips();

    default void getTooltipConfig(ConfigGroup group) {
        ConfigGroup tooltipGroup = group.getOrCreateSubgroup("tooltips").setNameKey("sdm.shop.tooltips");

        tooltipGroup.addList("tooltip", getTooltips(), new StringConfig(null), "")
                .setNameKey("sdm.shop.tooltips.tooltip");
    }

    default void serializeTooltips(CompoundTag nbt) {
        ListTag listTag = new ListTag();

        for (String tooltip : getTooltips()) {
            listTag.add(StringTag.valueOf(tooltip));
        }

        nbt.put("tooltip_list", listTag);
    }

    default void deserializeTooltips(CompoundTag nbt) {
        if(!nbt.contains("tooltip_list")) return;
        ListTag listTag = (ListTag) nbt.get("tooltip_list");

        getTooltips().clear();

        for (Tag tag : listTag) {
            getTooltips().add(tag.getAsString());
        }
    }

    default void addTooltipToList(TooltipList list) {
        for (String tooltip : getTooltips()) {
            list.add(Component.translatable(tooltip));
        }
    }
}
