package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import net.sixik.sdmshop.api.screen.ConfigScreenRefresher;
import net.sixik.sdmshop.mixin.accessors.EditConfigScreenAccessor;

public class SDMEditConfigScreen extends EditConfigScreen implements ConfigScreenRefresher {

    private EditConfigScreenAccessor accessor;

    public SDMEditConfigScreen(ConfigGroup configGroup) {
        super(configGroup);
        this.accessor = (EditConfigScreenAccessor) this;
    }


    @Override
    public void refreshAndSafe(ConfigGroup group) {
        if(accessor.isChanged()) {
            accessor.getGroup().save(true);
        }

        accessor.setGroup(group);
        refreshWidgets();
    }
}
