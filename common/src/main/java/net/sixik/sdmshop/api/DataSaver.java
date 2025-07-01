package net.sixik.sdmshop.api;

import net.minecraft.server.MinecraftServer;

public interface DataSaver {

    void save(MinecraftServer server);

    void load(MinecraftServer server);
}
