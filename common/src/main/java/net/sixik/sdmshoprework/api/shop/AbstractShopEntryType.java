package net.sixik.sdmshoprework.api.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdmshoprework.api.IModIdentifier;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractShopEntryType implements INBTSerializable<CompoundTag>, IModIdentifier {

    public AbstractShopEntry shopEntry;
//    public AbstractShopIcon creativeIcon = new ShopItemIcon(Items.BARRIER.getDefaultInstance());

    public SellType getSellType() {
        return SellType.BOTH;
    }

    public void setShopEntry(AbstractShopEntry shopEntry) {
        this.shopEntry = shopEntry;
    }

    public static AbstractShopEntryType fromOld(CompoundTag nbt) {
        try {
            String id = nbt.getString("type");
            if(id.equals("itemType")) {
                id = "shopItemEntryType";

                if(nbt.contains("item")) {
                    if (nbt.get("item") instanceof CompoundTag it) {
                        nbt.put("itemStack", it);
//                        SDMShopRework.LOGGER.info("SDM Item -> " + it.toString());

                    } else {
                        nbt.putString("itemStack", nbt.getString("item"));
//                        SDMShopRework.LOGGER.info("SDM Item -> " + nbt.getString("item"));
                    }
                }
            }



            AbstractShopEntryType type = ShopContentRegister.SHOP_ENTRY_TYPES.getOrDefault(id, null).createDefaultInstance();
            type.deserializeNBT(nbt);
            return type;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static AbstractShopEntryType from(CompoundTag nbt) {
        try {
            String id = nbt.getString("shopEntryTypeID");
            AbstractShopEntryType type = ShopContentRegister.SHOP_ENTRY_TYPES.getOrDefault(id, null).createDefaultInstance();
            type.deserializeNBT(nbt);
            return type;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSearch(String search) {
        return true;
    }

    public abstract void getConfig(ConfigGroup configGroup);

    public abstract AbstractShopEntryType copy();

    public abstract Component getTranslatableForCreativeMenu();

    public List<Component> getDescriptionForContextMenu(){
        return List.of(Component.translatable("sdmr.shop.entry.creator.type." + getId() + ".description"));
    }

    public boolean isCountable() {
        return true;
    }

    public Icon getCreativeIcon() {
        return Icons.DIAMOND;
    }

    public Icon getIcon() {
        return ItemIcon.getItemIcon(Items.BARRIER);
    }

    public void buy(Player player, int countBuy, AbstractShopEntry entry){}
    public void sell(Player player, int countSell, AbstractShopEntry entry) {}

    public void sendNotifiedMessage(Player player) {}

    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry){
        long playerMoney = CurrencyHelper.Basic.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    public int howMany(Player player, boolean isSell, AbstractShopEntry entry){
        return 1;
    }

    public String getModNameForContextMenu(){
        return "";
    }

    public enum SellType {
        ONLY_SELL,
        ONLY_BUY,
        BOTH
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("shopEntryTypeID", getId());
        return nbt;
    }


}