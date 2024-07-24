package net.sdm.sdmshopr.api.limiter;

import net.minecraft.world.entity.player.Player;

public interface ILimiter {

    boolean canUseEntryOnServer(Player player);
    boolean canUseEntryOnClient();

    int getLimitOnEntry();
    boolean isHaveLimit();
    boolean isGlobal();
}
