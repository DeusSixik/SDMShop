package net.sixik.sdmshop.old_api.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.ModObjectIdentifier;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEntrySellerType<T> implements DataSerializerCompoundTag, ModObjectIdentifier, ShopObject {

    public static final String ID_KEY = "register_id";

    protected T objectType;
    protected boolean shopTooltip = false;

    protected AbstractEntrySellerType(T objectType) {
        this.objectType = objectType;
    }

    public abstract boolean onBuy(Player player, ShopEntry shopEntry, long countSell);

    public abstract boolean onSell(Player player, ShopEntry shopEntry, long countSell);

    public abstract double getMoney(Player player, ShopEntry shopEntry);

    public boolean haveMoney(Player player, ShopEntry shopEntry, long countSell) {
        return shopEntry.getPrice() * countSell <= getMoney(player, shopEntry);
    }

    public boolean isFractionalNumber() {
        return true;
    }

    public abstract AbstractEntrySellerType<T> copy();

    public final void getConfig(ConfigGroup configGroup) {
        configGroup.addBool("shopTooltip", shopTooltip, v -> shopTooltip = v, false);
        _getConfig(configGroup);
    }

    public void _getConfig(ConfigGroup configGroup) {}

    public abstract String getId();

    @Override
    public final CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(ID_KEY, getId());
        nbt.put("data", _serialize());
        nbt.putBoolean("shopTooltip", shopTooltip);
        return nbt;
    }

    @Override
    public final void deserialize(CompoundTag tag) {
        if(tag.contains("shopTooltip"))
            shopTooltip = tag.getBoolean("shopTooltip");
        _deserialize(tag.getCompound("data"));
    }



    public abstract CompoundTag _serialize();

    public abstract void _deserialize(CompoundTag tag);

    public abstract String getEnumName();

    public void addEntryTooltip(TooltipList list, ShopEntry entry) {}

    public abstract String moneyToString(ShopEntry entry);

    @Environment(EnvType.CLIENT)
    public abstract int getRenderWight(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize);

    @Environment(EnvType.CLIENT)
    public abstract void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize);


    @Override
    public final ShopObjectTypes getShopType() {
        return ShopObjectTypes.ENTRY_SELLER_TYPE;
    }
}
