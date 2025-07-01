package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.sixik.sdmshop.mixin.accessors.ConfigGroupAccess;

public class SDMConfigGroup extends ConfigGroup {

    private ConfigGroupAccess access = (ConfigGroupAccess) this;

    public SDMConfigGroup(String id) {
        super(id);
    }

    public SDMConfigGroup(String id, ConfigCallback savedCallback) {
        super(id, savedCallback);
    }
}
