package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.function.Function;

public class ConfigBuilder<T> {

    protected ConfigGroup group;
    protected Function<ConfigGroup, ConfigValue<T>> fun;
    protected TooltipList list = new TooltipList();

    public ConfigBuilder(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun) {
        this.group = group;
    }

    public ConfigBuilder<T> addTooltip(Component component) {
        list.add(component);
        return this;
    }

    public ConfigBuilder<T> addTooltip(Component... component) {
        for (Component component1 : component) {
            list.add(component1);
        }
        return this;
    }

    public ConfigBuilder<T> addTooltip(Collection<Component> components) {
        for (Component component1 : components) {
            list.add(component1);
        }
        return this;
    }

    public ConfigValue<T> getValue() {
        var t = fun.apply(group);
        t.addInfo(list);
        return t;
    }
}
