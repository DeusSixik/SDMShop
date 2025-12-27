package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.config.NameMap;

import java.util.ArrayList;
import java.util.List;

public enum LimiterType {
    LocalPlayer,
    Global;

    public boolean isPlayer() {
        return this == LocalPlayer;
    }

    public boolean isGlobal() {
        return this == Global;
    }

    public static NameMap<String> getTypeList(){
        List<String> str = new ArrayList<>();

        for (LimiterType value : LimiterType.values()) {
            str.add(value.name());
        }

        return NameMap.of(LimiterType.LocalPlayer.name(), str).create();
    }
}
