package net.sixik.sdmshop.shop.limiter;

import org.jetbrains.annotations.NotNull;

public record ShopLimiterData(@NotNull ShopLimiterAttachType attachType, int value) {
}
