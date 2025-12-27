package net.sixik.sdmshop.compat.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ShopServerJS {

    default boolean setMoney(Player player, double count) {
        return ShopUtils.setMoney(player, count);
    }

    default boolean setMoney(Player player, double count, String money_id) {
        return ShopUtils.setMoney(player, money_id, count);
    }

    default boolean addMoney(Player player, double count) {
        return ShopUtils.addMoney(player, count);
    }

    default boolean addMoney(Player player, double count, String money_id) {
        return ShopUtils.addMoney(player, money_id, count);
    }

    default double getMoney(Player player) {
        return ShopUtils.getMoney(player);
    }

    default double getMoney(Player player, String money_id) {
        return ShopUtils.getMoney(player, money_id);
    }

    default @Nullable BaseShop getShopByUUID(UUID shopId) {
        return SDMShopServer.Instance().getShop(shopId).orElse(null);
    }

    default @Nullable BaseShop getShopByRegistryId(ResourceLocation id) {
        return SDMShopServer.Instance().getShop(id).orElse(null);
    }

    default @Nullable ShopTab getShopTabByUUID(BaseShop shop, UUID tabId) {
        return shop.findTabByUUID(tabId).orElse(null);
    }

    default @Nullable ShopEntry getShopEntryByUUID(BaseShop shop, UUID entryId) {
        return shop.findShopEntryByUUID(entryId).orElse(null);
    }

    default @Nullable List<ShopTab> getShopTabs(BaseShop shop) {
        return shop.getTabsList();
    }

    default @Nullable List<ShopEntry> getShopEntries(BaseShop shop) {
        return shop.getEntriesList();
    }

    default @Nullable ShopLimiter getLimiter() {
        return SDMShopServer.Instance() == null ? null : SDMShopServer.Instance().getShopLimiter();
    }

    default @Nullable SDMShopServer getServerData() {
        return SDMShopServer.Instance();
    }
}
