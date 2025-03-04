package net.sixik.sdmshoprework.api.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshoprework.api.IModIdentifier;
import net.sixik.sdmshoprework.api.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractShopSellerType<T> implements INBTSerializable<CompoundTag>, IModIdentifier {

    protected T type;

    public AbstractShopSellerType(T type) {
        this.type = type;
    }

    public abstract long getCount(Player player);

    public abstract boolean buy(Player player, AbstractShopEntry shopEntry, long countSell);

    public T getType() {
        return type;
    }

    public abstract String getEnumName();

    public boolean hasConfig() {
        return false;
    }

    public void getConfig(ConfigGroup configGroup) {}

    @Override
    public String getModId() {
        return "minecraft";
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("shopSellerTypeID", getId());
        return nbt;
    }

    public void addTooltip(TooltipList tooltipList, AbstractShopEntry entry) {}

    @Environment(EnvType.CLIENT)
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, long count, @Nullable Widget widget, int additionSize) {}
}
