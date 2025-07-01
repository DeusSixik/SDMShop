package net.sixik.sdmshop.api;

import dev.architectury.platform.Platform;

public interface ModObjectIdentifier extends ObjectIdentifier{

    default String getModId() {
        return "minecraft";
    }

    default boolean isModLoaded() {
        return Platform.isModLoaded(getModId());
    }
}
